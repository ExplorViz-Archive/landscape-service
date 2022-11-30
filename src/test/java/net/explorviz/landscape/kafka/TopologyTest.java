package net.explorviz.landscape.kafka;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import io.quarkus.test.junit.QuarkusTest;
import java.util.Properties;
import javax.inject.Inject;
import net.explorviz.avro.SpanStructure;
import net.explorviz.landscape.service.cassandra.ReactiveLandscapeService;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
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

  private ReadOnlyKeyValueStore<String, Integer> spanKeyValueStore;

  @ConfigProperty(name = "explorviz.kafka-streams.topics.in")
  /* default */ String inTopic;

  @Inject
  Topology topology;

  @Inject
  SpecificAvroSerde<SpanStructure> spanStructureSerDe; // NOCS

  @Inject
  ReactiveLandscapeService reactiveLandscapeService;

  @BeforeEach
  void setUp() {
    final Properties config = new Properties();
    config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
    config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class.getName());
    config.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://registry:1234");

    this.driver = new TopologyTestDriver(this.topology, config);

    this.inputTopic = this.driver.createInputTopic(this.inTopic, Serdes.String().serializer(),
        this.spanStructureSerDe.serializer());

    this.spanKeyValueStore = this.driver.getKeyValueStore("cachedSpans");
  }

  @AfterEach
  void tearDown() {
    this.spanStructureSerDe.close();
    this.driver.close();
  }

  private SpanStructure sampleSpanStructure() {

    // CHECKSTYLE:OFF

    return SpanStructure.newBuilder().setSpanId("testSpanId")
        .setLandscapeToken("testLandscapeToken").setTimestampInEpochMilli(123L)
        .setHashCode("testHashcode").setHostname("testHost").setHostIpAddress("testIp")
        .setAppName("testAppName").setAppInstanceId("testAppInstanceId")
        .setFullyQualifiedOperationName("testFqn").setAppLanguage("testAppLanguage").build();

    // CHECKSTYLE:ON
  }

  @Test
  void testSingleSpanInCache() {
    final SpanStructure testSpan = this.sampleSpanStructure();
    this.inputTopic.pipeInput(testSpan.getLandscapeToken(), testSpan);

    int numberOfRecordsInStore = 0;

    for (KeyValueIterator<String, Integer> it = spanKeyValueStore.all(); it.hasNext(); ) {
      KeyValue<String, Integer> keyValue = it.next();
      numberOfRecordsInStore++;
    }

    assertEquals(1, numberOfRecordsInStore);
    assertEquals(1, spanKeyValueStore.get(testSpan.getHashCode()));
  }

  @Test
  void testSameHashCodeOnlyOnceInCash() {
    final SpanStructure testSpan = this.sampleSpanStructure();

    for(int i = 0; i <= 100; i++) {
      this.inputTopic.pipeInput(testSpan.getLandscapeToken(), testSpan);
    }

    int numberOfRecordsInStore = 0;

    for (KeyValueIterator<String, Integer> it = spanKeyValueStore.all(); it.hasNext(); ) {
      KeyValue<String, Integer> keyValue = it.next();
      numberOfRecordsInStore++;
    }

    assertEquals(1, numberOfRecordsInStore);
    assertEquals(1, spanKeyValueStore.get(testSpan.getHashCode()));
  }

  @Test
  void testMultipleHashCodesOnlyOnceInCash() {
    final SpanStructure testSpan1 = this.sampleSpanStructure();

    final SpanStructure testSpan2 = this.sampleSpanStructure();
    testSpan2.setHashCode("testSpan2");

    final SpanStructure testSpan3 = this.sampleSpanStructure();
    testSpan3.setHashCode("testSpan3");

    final SpanStructure testSpan4 = this.sampleSpanStructure();
    testSpan4.setHashCode("testSpan4");

    final SpanStructure testSpan11 = this.sampleSpanStructure();

    for(int i = 0; i <= 100; i++) {
      this.inputTopic.pipeInput(testSpan1.getLandscapeToken(), testSpan1);
      this.inputTopic.pipeInput(testSpan1.getLandscapeToken(), testSpan2);
      this.inputTopic.pipeInput(testSpan1.getLandscapeToken(), testSpan3);
      this.inputTopic.pipeInput(testSpan1.getLandscapeToken(), testSpan4);
      this.inputTopic.pipeInput(testSpan1.getLandscapeToken(), testSpan11);
    }

    int numberOfRecordsInStore = 0;

    for (KeyValueIterator<String, Integer> it = spanKeyValueStore.all(); it.hasNext(); ) {
      KeyValue<String, Integer> keyValue = it.next();
      numberOfRecordsInStore++;
    }

    assertEquals(4, numberOfRecordsInStore);
    assertEquals(1, spanKeyValueStore.get(testSpan1.getHashCode()));
    assertEquals(1, spanKeyValueStore.get(testSpan2.getHashCode()));
    assertEquals(1, spanKeyValueStore.get(testSpan3.getHashCode()));
    assertEquals(1, spanKeyValueStore.get(testSpan4.getHashCode()));
  }


}
