package net.explorviz.landscape.kafka;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import net.explorviz.avro.Span;
import net.explorviz.landscape.service.HashHelper;
import net.explorviz.landscape.service.cassandra.ReactiveSpanStructureService;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Builds a KafkaStream topology instance with all its transformers. Entry point of the stream
 * analysis.
 */
@ApplicationScoped
public class TopologyProducer {

  private static final String KEY_VALUE_STORE_NAME = "cachedSpans";

  @ConfigProperty(name = "explorviz.kafka-streams.topics.in")
  /* default */ String inTopic; // NOCS

  @Inject
  /* default */ SpecificAvroSerde<Span> spanAvroSerde; // NOCS

  @Inject
  /* default */ ReactiveSpanStructureService spanStructureService; // NOCS

  @Inject
  /* default */ SpanFilterTransformer spanTransformer; // NOCS

  @Produces
  public Topology buildTopology() {

    final StreamsBuilder builder = new StreamsBuilder();

    // create store
    final StoreBuilder<KeyValueStore<String, Integer>> storeBuilder = Stores.keyValueStoreBuilder(
        Stores.persistentKeyValueStore(KEY_VALUE_STORE_NAME), Serdes.String(), Serdes.Integer());
    // register store
    builder.addStateStore(storeBuilder);

    // BEGIN Span conversion

    // Span stream
    final KStream<String, Span> spanStream = builder.stream(this.inTopic,
        Consumed.with(Serdes.String(), this.spanAvroSerde));

    // Generate HashCode
    final KStream<String, Span> spanStreamWithHashCodes = spanStream.mapValues(
        (readOnlyKey, value) -> {
          value.setHashCode(HashHelper.createHash(value));
          return value;
        });

    // TODO: Replace deprecated transform() with process()
    final KStream<String, Span> toBeSavedSpans = spanStreamWithHashCodes.transform(
        () -> spanTransformer, KEY_VALUE_STORE_NAME);

    // TODO: How to handle failures in dao? Use insert(...).onFailure() to handle
    toBeSavedSpans.mapValues(
        avro -> new net.explorviz.landscape.persistence.model.SpanStructure.Builder().fromAvro(avro)
            .build()).foreach((k, rec) -> {
              this.spanStructureService.add(rec).subscribeAsCompletionStage();
            });

    // END Span conversion

    return builder.build();
  }

}
