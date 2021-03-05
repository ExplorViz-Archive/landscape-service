package net.explorviz.landscape.kafka;

import java.util.Properties;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import net.explorviz.avro.SpanStructure;
import net.explorviz.landscape.peristence.SpanStructureRepositoy;
import net.explorviz.landscape.service.converter.SpanToRecordConverter;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;


/**
 * Kafka stream processors to convert structural runtime information to landscape records, which are
 * afterwards persisted in a Apache Cassandra Database.
 */
@ApplicationScoped
public class SpanToRecordStream {

  private final KafkaHelper kafkaHelper;

  private final KafkaStreams stream;


  private final SpanStructureRepositoy repository;


  @Inject
  public SpanToRecordStream(final KafkaHelper kafkaHelper,
                            final SpanToRecordConverter converter,
                            final SpanStructureRepositoy repository) {
    this.kafkaHelper = kafkaHelper;
    this.repository = repository;
    final Topology topology = buildTopology();
    final Properties props = kafkaHelper.newDefaultStreamProperties();
    this.stream = new KafkaStreams(topology, props);
  }

  public KafkaStreams getStream() {
    return this.stream;
  }

  private Topology buildTopology() {
    final StreamsBuilder builder = new StreamsBuilder();

    // Span Structure stream
    final KStream<String, SpanStructure> spanStream =
        builder.stream(this.kafkaHelper.getTopicSpanStructure(), Consumed
            .with(Serdes.String(), this.kafkaHelper.getAvroValueSerde()));

    // TODO: How to handle failures in dao? Use insert(...).onFailure() to handle
    spanStream
        .mapValues(avro -> new net.explorviz.landscape.peristence.model.SpanStructure.Builder()
            .fromAvro(avro).build())
        .foreach((k, rec) -> {
          System.out.println("Span: " + rec.getLandscapeToken());
          this.repository.add(rec).subscribeAsCompletionStage();
        });

    return builder.build();
  }



}
