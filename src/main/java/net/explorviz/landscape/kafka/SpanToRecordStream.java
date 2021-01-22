package net.explorviz.landscape.kafka;

import java.util.Properties;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import net.explorviz.avro.SpanStructure;
import net.explorviz.avro.landscape.flat.LandscapeRecord;
import net.explorviz.landscape.peristence.QueryException;
import net.explorviz.landscape.peristence.Repository;
import net.explorviz.landscape.service.converter.SpanToRecordConverter;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Kafka stream processors to convert structural runtime information to landscape
 * records, which are afterwards persisted in a Apache Cassandra Database.
 */
@ApplicationScoped
public class SpanToRecordStream {

  private static final Logger LOGGER = LoggerFactory.getLogger(SpanToRecordStream.class);

  private final KafkaHelper kafkaHelper;

  private final Repository<LandscapeRecord> recordRepo;

  private final KafkaStreams stream;

  private final SpanToRecordConverter converter;


  @Inject
  public SpanToRecordStream(final KafkaHelper kafkaHelper,
      final SpanToRecordConverter converter,
      final Repository<LandscapeRecord> repository) {
    this.kafkaHelper = kafkaHelper;
    this.converter = converter;
    this.recordRepo = repository;
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

    // Map to records
    final KStream<String, LandscapeRecord> recordKStream =
        spanStream.map((k, s) -> {
          final LandscapeRecord record = this.converter.toRecord(s);
          return new KeyValue<>(record.getLandscapeToken(), record);
        });


    recordKStream.foreach((k, rec) -> {
      try {
        this.recordRepo.addAsync(rec);
      } catch (final QueryException e) {
        if (LOGGER.isErrorEnabled()) {
          LOGGER.error("Failed to persist an record: {0}", e);
        }
      }
    });

    return builder.build();
  }



}
