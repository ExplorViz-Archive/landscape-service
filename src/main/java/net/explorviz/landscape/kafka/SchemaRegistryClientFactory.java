package net.explorviz.landscape.kafka;

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.quarkus.arc.DefaultBean;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class SchemaRegistryClientFactory {
  
  private static final int MAX_NUM_OF_SCHEMAS = 10;

  @ConfigProperty(name = "explorviz.schema-registry.url")
  /* default */ String schemaRegistryUrl; //NOCS

  @Produces
  @DefaultBean
  public SchemaRegistryClient schemaRegistryClient() {
    return new CachedSchemaRegistryClient(this.schemaRegistryUrl, MAX_NUM_OF_SCHEMAS);
  }
}
