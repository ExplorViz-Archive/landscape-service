package net.explorviz.landscape.kafka;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.kafka.KafkaStreamsMetrics;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.scheduler.Scheduled;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import net.explorviz.avro.SpanStructure;
import net.explorviz.landscape.peristence.SpanStructureRepositoy;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KafkaStreams.State;
import org.apache.kafka.streams.KafkaStreams.StateListener;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Kafka stream processors to convert structural runtime information to landscape records, which are
 * afterwards persisted in a Apache Cassandra Database.
 */
@ApplicationScoped
public class SpanToRecordStream {

  private static final Logger LOGGER = LoggerFactory.getLogger(SpanToRecordStream.class);

  @Inject
  // NOPMD
  /* default */ MeterRegistry meterRegistry; // NOPMD NOCS

  private final KafkaHelper kafkaHelper;

  private final AtomicInteger lastReceivedTotalSpans = new AtomicInteger(0);
  private final AtomicInteger lastReceivedUnknownSpans = new AtomicInteger(0);
  private final AtomicInteger lastReceivedCachedSpans = new AtomicInteger(0);

  private KafkaStreams streams;

  private final SpanStructureRepositoy repository;

  private final Properties props;

  private final Topology topology;

  private final SpanCache cache;

  @Inject
  public SpanToRecordStream(final KafkaHelper kafkaHelper, final SpanStructureRepositoy repository,
      final SpanCache cache) {
    this.kafkaHelper = kafkaHelper;
    this.repository = repository;
    this.topology = this.buildTopology();
    this.props = kafkaHelper.newDefaultStreamProperties();
    this.cache = cache;

  }

  /* default */ void onStart(@Observes final StartupEvent event) { // NOPMD
    this.streams = new KafkaStreams(this.topology, this.props);
    this.streams.cleanUp();
    this.streams.setStateListener(new ErrorStateListener());

    this.streams.start();

    final KafkaStreamsMetrics ksm = new KafkaStreamsMetrics(this.streams); // NOPMD
    ksm.bindTo(this.meterRegistry);
  }

  /* default */ void onStop(@Observes final ShutdownEvent event) { // NOPMD
    this.streams.close();
  }

  public KafkaStreams getStream() {
    return this.streams;
  }

  private Topology buildTopology() {
    final StreamsBuilder builder = new StreamsBuilder();

    // Span Structure stream
    final KStream<String, SpanStructure> spanStream =
        builder.stream(this.kafkaHelper.getTopicSpanStructure(),
            Consumed.with(Serdes.String(), this.kafkaHelper.getAvroValueSerde()));

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
        .mapValues(avro -> new net.explorviz.landscape.peristence.model.SpanStructure.Builder()
            .fromAvro(avro).build())
        .foreach((k, rec) -> {
          // System.out.println("Span: " + rec.getLandscapeToken());
          this.repository.add(rec).subscribeAsCompletionStage();

          // Add to cache
          this.cache.put(rec.getHashCode());
        });

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

  private static class ErrorStateListener implements StateListener {

    @Override
    public void onChange(final State newState, final State oldState) {
      if (newState.equals(State.ERROR)) {

        if (LOGGER.isErrorEnabled()) {
          LOGGER.error("Kafka Streams thread died. "
              + "Are Kafka topic initialized? Quarkus application will shut down.");
        }
        Quarkus.asyncExit(-1);
      }

    }
  }

}
