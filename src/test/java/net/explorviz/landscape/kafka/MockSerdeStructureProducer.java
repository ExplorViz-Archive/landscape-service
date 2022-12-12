package net.explorviz.landscape.kafka;

import io.confluent.kafka.schemaregistry.avro.AvroSchema;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import io.quarkus.arc.profile.IfBuildProfile;
import java.io.IOException;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import net.explorviz.avro.SpanStructure;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Returns an injectable {@link SpecificAvroSerde}.
 */
@Dependent
public class MockSerdeStructureProducer {

  @ConfigProperty(name = "explorviz.kafka-streams.topics.in")
  /* default */ String inTopicStructure; // NOCS

  @Inject
  /* default */ SchemaRegistryClient registry; // NOCS

  @Produces
  @IfBuildProfile("test")
  public SpecificAvroSerde<SpanStructure> produceMockSpecificAvroSerde()
      throws IOException, RestClientException {

    this.registry.register(this.inTopicStructure + "-value", new AvroSchema(SpanStructure.SCHEMA$));

    final SpecificAvroSerde<SpanStructure> valueSerde = new SpecificAvroSerde<>(this.registry);
    valueSerde.configure(
        Map.of(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://registry:1234"),
        false);
    return valueSerde;
  }
}

