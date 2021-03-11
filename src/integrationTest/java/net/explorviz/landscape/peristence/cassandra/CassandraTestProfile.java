package net.explorviz.landscape.peristence.cassandra;

import io.quarkus.test.junit.QuarkusTestProfile;
import java.util.Collections;
import java.util.Map;


/**
 * Profile used for testing Cassandra with testcontainers.
 */
public class CassandraTestProfile implements QuarkusTestProfile {

  @Override
  public Map<String, String> getConfigOverrides() {
    return Collections.singletonMap(
        "quarkus.cassandra.contact-points",
        "${quarkus.cassandra.docker_host}:${quarkus.cassandra.docker_port}");
  }

  @Override
  public String getConfigProfile() {
    return "cassandra";
  }
}

