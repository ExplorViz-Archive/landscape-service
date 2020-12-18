package net.explorviz.landscape.kafka;

import java.util.Properties;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import net.explorviz.avro.landscape.flat.LandscapeRecord;
import net.explorviz.landscape.peristence.QueryException;
import net.explorviz.landscape.peristence.Repository;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class RecordPersistingStream {

  private static final Logger LOGGER = LoggerFactory.getLogger(RecordPersistingStream.class);

  private final KafkaHelper kHelper;

  private final Topology topology;

  private final Properties props;

  private final Repository<LandscapeRecord> recordRepo;

  private final KafkaStreams stream;


  @Inject
  public RecordPersistingStream(final KafkaHelper kHelper, Repository<LandscapeRecord> repository) {
    this.kHelper = kHelper;
    this.recordRepo = repository;
    this.topology = this.buildTopology();
    this.props = kHelper.newDefaultStreamProperties();
    this.stream = new KafkaStreams(this.topology, this.props);
  }

  public KafkaStreams getStream() {
    return stream;
  }

  private Topology buildTopology() {
    final StreamsBuilder builder = new StreamsBuilder();

    final KStream<String, LandscapeRecord> recordStream =
        builder.stream(this.kHelper.getTopicRecords(), Consumed
            .with(Serdes.String(), this.kHelper.getAvroValueSerde()));

    recordStream.foreach((k, rec) -> {
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
