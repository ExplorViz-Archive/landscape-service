package net.explorviz.landscape.kafka;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import io.quarkus.scheduler.Scheduled;
import java.util.concurrent.atomic.AtomicInteger;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import net.explorviz.avro.SpanStructure;
import net.explorviz.landscape.persistence.SpanStructureRepositoy;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopologyProducer {

  private static final Logger LOGGER = LoggerFactory.getLogger(TopologyProducer.class);

  @ConfigProperty(name = "explorviz.kafka-streams.topics.in")
  /* default */ String inTopic; // NOCS

  @ConfigProperty(name = "explorviz.kafka-streams.topics.out.structure")
  /* default */ String structureOutTopic; // NOCS

  @Inject
  /* default */ SpecificAvroSerde<SpanStructure> structureAvroSerde; // NOCS

  @Inject
  /* default */ SpanCache cache; // NOCS

  @Inject
  /* default */ SpanStructureRepositoy repository; // NOCS

  // Logged and reset every n seconds
  private final AtomicInteger lastReceivedTotalSpans = new AtomicInteger(0);
  private final AtomicInteger lastReceivedUnknownSpans = new AtomicInteger(0);
  private final AtomicInteger lastReceivedCachedSpans = new AtomicInteger(0);

  @Produces
  public Topology buildTopology() {

    final StreamsBuilder builder = new StreamsBuilder();

    // BEGIN Span conversion

    // Span Structure stream
    final KStream<String, SpanStructure> spanStream =
        builder.stream(this.inTopic, Consumed.with(Serdes.String(), this.structureAvroSerde));

    // DEBUG Total spans
    spanStream.foreach((key, value) -> {
      this.lastReceivedTotalSpans.incrementAndGet();
    });

    // Check the cache, newSpanStream only contains spans that have not been seen
    // recently
    final KStream<String, SpanStructure> newSpanStream =
        spanStream.filter((k, v) -> !this.cache.exists(v.getHashCode()));

    // DEBUG Cached spans
    spanStream.filter((k, v) -> this.cache.exists(v.getHashCode())).foreach((key, value) -> {
      this.lastReceivedCachedSpans.incrementAndGet();
    });

    // DEBUG Completely new spans
    newSpanStream.foreach((key, value) -> {
      this.lastReceivedUnknownSpans.incrementAndGet();
    });

    // TODO: How to handle failures in dao? Use insert(...).onFailure() to handle
    newSpanStream
        .mapValues(avro -> new net.explorviz.landscape.persistence.model.SpanStructure.Builder()
            .fromAvro(avro).build())
        .foreach((k, rec) -> {
          // System.out.println("Span: " + rec.getLandscapeToken());
          this.repository.add(rec).subscribeAsCompletionStage();

          // Add to cache
          this.cache.put(rec.getHashCode());
        });

    // END Span conversion

    return builder.build();
  }

  @Scheduled(every = "{explorviz.log.span.interval}") // NOPMD
  void logStatus() { // NOPMD
    final int totalSpans = this.lastReceivedTotalSpans.getAndSet(0);
    final int cachedSpans = this.lastReceivedCachedSpans.getAndSet(0);
    final int newSpans = this.lastReceivedUnknownSpans.getAndSet(0);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "Received {} " + "spans: {} cached / already saved spans, " + "{} unknown / saved spans.",
          totalSpans, cachedSpans, newSpans);
    }
  }

}
