package net.explorviz.landscape.kafka;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import net.explorviz.avro.SpanStructure;
import net.explorviz.landscape.persistence.SpanStructureRepositoy;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
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
  /* default */ SpecificAvroSerde<SpanStructure> structureAvroSerde; // NOCS

  @Inject
  /* default */ SpanStructureRepositoy repository; // NOCS

  @Inject
  /* default */ SpanFilterTransformer spanTransformer; // NOCS

  @Produces
  public Topology buildTopology() {

    final StreamsBuilder builder = new StreamsBuilder();

    // create store
    final StoreBuilder storeBuilder = Stores.keyValueStoreBuilder(
        Stores.persistentKeyValueStore(KEY_VALUE_STORE_NAME), Serdes.String(), Serdes.Integer());
    // register store
    builder.addStateStore(storeBuilder);

    // BEGIN Span conversion

    // Span Structure stream
    final KStream<String, SpanStructure> spanStream = builder.stream(this.inTopic,
        Consumed.with(Serdes.String(), this.structureAvroSerde));

    final KStream<String, SpanStructure> toBeSavedSpans = spanStream.transform(
        () -> spanTransformer, KEY_VALUE_STORE_NAME);

    // TODO: How to handle failures in dao? Use insert(...).onFailure() to handle
    toBeSavedSpans.mapValues(
        avro -> new net.explorviz.landscape.persistence.model.SpanStructure.Builder().fromAvro(avro)
            .build()).foreach((k, rec) -> {
              this.repository.add(rec).subscribeAsCompletionStage();
            });

    // END Span conversion

    return builder.build();
  }

}
