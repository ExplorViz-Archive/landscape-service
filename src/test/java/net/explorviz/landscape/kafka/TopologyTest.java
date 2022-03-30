package net.explorviz.landscape.kafka;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import io.quarkus.test.junit.QuarkusTest;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import javax.inject.Inject;
import net.explorviz.avro.SpanStructure;
import net.explorviz.avro.Timestamp;
import net.explorviz.landscape.persistence.SpanStructureRepositoy;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@QuarkusTest
class TopologyTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(TopologyTest.class);

  private TopologyTestDriver driver;

  private TestInputTopic<String, SpanStructure> inputTopic;

  @ConfigProperty(name = "explorviz.kafka-streams.topics.in")
  /* default */ String inTopic;

  @Inject
  Topology topology;

  @Inject
  SpecificAvroSerde<SpanStructure> spanStructureSerDe; // NOCS

  @Inject
  SpanCache spanCache;
  
  @Inject
  SpanStructureRepositoy repository;

  @BeforeEach
  void setUp() {
    final Properties config = new Properties();
    config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
    config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class.getName());
    config.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://registry:1234");

    this.driver = new TopologyTestDriver(this.topology, config);

    this.inputTopic = this.driver.createInputTopic(this.inTopic, Serdes.String().serializer(),
        this.spanStructureSerDe.serializer());

  }

  @AfterEach
  void tearDown() {
    this.spanStructureSerDe.close();
    this.driver.close();
  }

  private SpanStructure sampleSpanStructure() {

    // CHECKSTYLE:OFF

    return SpanStructure.newBuilder().setSpanId("testSpanId")
        .setLandscapeToken("testLandscapeToken")
        .setTimestamp(Timestamp.newBuilder().setSeconds(123).setNanoAdjust(456).build())
        .setHashCode("testHashcode").setHostname("testHost").setHostIpAddress("testIp")
        .setAppName("testAppName").setAppInstanceId("testAppInstanceId")
        .setFullyQualifiedOperationName("testFqn").setAppLanguage("testAppLanguage").build();

    // CHECKSTYLE:ON
  }

  @Test
  void testCache() throws InterruptedException, ExecutionException {
    final SpanStructure testSpan = this.sampleSpanStructure();
    
    List<net.explorviz.landscape.persistence.model.SpanStructure> spanStructuresWithToken = this.repository.getAll(testSpan.getLandscapeToken()).collect().asList().subscribeAsCompletionStage().get();
    
    assertTrue(spanStructuresWithToken.size() == 0);
    assertFalse(this.spanCache.exists(testSpan.getHashCode()));

    this.inputTopic.pipeInput(testSpan.getLandscapeToken(), testSpan);
    
    spanStructuresWithToken = this.repository.getAll(testSpan.getLandscapeToken()).collect().asList().subscribeAsCompletionStage().get();

    assertTrue(spanStructuresWithToken.size() == 1);
    assertTrue(this.spanCache.exists(testSpan.getHashCode()));
  }


}
