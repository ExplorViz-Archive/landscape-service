package net.explorviz.landscape.kafka;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import io.quarkus.test.junit.QuarkusTest;
import java.util.Properties;
import javax.inject.Inject;
import net.explorviz.avro.Span;
import net.explorviz.landscape.service.HashHelper;
import net.explorviz.landscape.service.cassandra.ReactiveSpanStructureService;
import org.apache.commons.lang3.RandomStringUtils;
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


@QuarkusTest
class TopologyTest {

  //private static final Logger LOGGER = LoggerFactory.getLogger(TopologyTest.class);

  private TopologyTestDriver driver;

  private TestInputTopic<String, Span> inputTopic;

  private ReadOnlyKeyValueStore<String, Integer> spanKeyValueStore;

  @ConfigProperty(name = "explorviz.kafka-streams.topics.in")
  /* default */ String inTopic;

  @Inject
  Topology topology;

  @Inject
  SpecificAvroSerde<Span> spanStructureSerDe; // NOCS

  @Inject
  ReactiveSpanStructureService reactiveSpanStructureService;

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

  private Span sampleSpanStructure() {

    // CHECKSTYLE:OFF

    return Span.newBuilder().setLandscapeToken(RandomStringUtils.random(8, true, true))
        .setSpanId(RandomStringUtils.random(8, true, true))
        .setParentSpanId(RandomStringUtils.random(8, true, true))
        .setTraceId(RandomStringUtils.random(8, true, false)).setStartTimeEpochMilli(123L)
        .setEndTimeEpochMilli(123L)
        .setFullyQualifiedOperationName(RandomStringUtils.random(8, true, true))
        .setHostname(RandomStringUtils.randomAlphabetic(10))
        .setHostIpAddress(RandomStringUtils.random(8, true, true))
        .setAppName(RandomStringUtils.randomAlphabetic(10))
        .setAppInstanceId(RandomStringUtils.randomNumeric(3))
        .setAppLanguage(RandomStringUtils.randomAlphabetic(5)).build();

    // CHECKSTYLE:ON
  }

  @Test
  void testSingleSpanInCache() {
    final Span testSpan = this.sampleSpanStructure();
    this.inputTopic.pipeInput(testSpan.getLandscapeToken(), testSpan);

    int numberOfRecordsInStore = 0;

    for (KeyValueIterator<String, Integer> it = spanKeyValueStore.all(); it.hasNext(); ) {
      KeyValue<String, Integer> keyValue = it.next();
      numberOfRecordsInStore++;
    }

    assertEquals(1, numberOfRecordsInStore);
    assertEquals(1, spanKeyValueStore.get(HashHelper.createHash(testSpan)));
  }

  @Test
  void testSameHashCodeOnlyOnceInCash() {
    final Span testSpan = this.sampleSpanStructure();

    for (int i = 0; i <= 100; i++) {
      this.inputTopic.pipeInput(testSpan.getLandscapeToken(), testSpan);
    }

    int numberOfRecordsInStore = 0;

    for (KeyValueIterator<String, Integer> it = spanKeyValueStore.all(); it.hasNext(); ) {
      KeyValue<String, Integer> keyValue = it.next();
      numberOfRecordsInStore++;
    }

    assertEquals(1, numberOfRecordsInStore);
    assertEquals(1, spanKeyValueStore.get(HashHelper.createHash(testSpan)));
  }

  @Test
  void testMultipleHashCodesOnlyOnceInCash() {
    final Span testSpan1 = this.sampleSpanStructure();

    final Span testSpan2 = this.sampleSpanStructure();

    final Span testSpan3 = this.sampleSpanStructure();

    final Span testSpan4 = this.sampleSpanStructure();

    for (int i = 0; i <= 100; i++) {
      this.inputTopic.pipeInput(testSpan1.getLandscapeToken(), testSpan1);
      this.inputTopic.pipeInput(testSpan1.getLandscapeToken(), testSpan2);
      this.inputTopic.pipeInput(testSpan1.getLandscapeToken(), testSpan3);
      this.inputTopic.pipeInput(testSpan1.getLandscapeToken(), testSpan4);
    }

    int numberOfRecordsInStore = 0;

    for (KeyValueIterator<String, Integer> it = spanKeyValueStore.all(); it.hasNext(); ) {
      KeyValue<String, Integer> keyValue = it.next();
      numberOfRecordsInStore++;
    }

    assertEquals(4, numberOfRecordsInStore);
    assertEquals(1, spanKeyValueStore.get(HashHelper.createHash(testSpan1)));
    assertEquals(1, spanKeyValueStore.get(HashHelper.createHash(testSpan2)));
    assertEquals(1, spanKeyValueStore.get(HashHelper.createHash(testSpan3)));
    assertEquals(1, spanKeyValueStore.get(HashHelper.createHash(testSpan4)));
  }


}
