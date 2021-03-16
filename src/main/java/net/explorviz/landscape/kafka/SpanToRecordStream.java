package net.explorviz.landscape.kafka;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import java.util.Properties;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import net.explorviz.avro.SpanStructure;
import net.explorviz.landscape.peristence.SpanStructureRepositoy;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KafkaStreams.State;
import org.apache.kafka.streams.KafkaStreams.StateListener;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Kafka stream processors to convert structural runtime information to landscape records, which are
 * afterwards persisted in a Apache Cassandra Database.
 */
@ApplicationScoped
public class SpanToRecordStream {

  private static final Logger LOGGER = LoggerFactory.getLogger(SpanToRecordStream.class);

  private final KafkaHelper kafkaHelper;

  private KafkaStreams streams;

  private final SpanStructureRepositoy repository;

  private final Properties props;

  private final Topology topology;

  @Inject
  public SpanToRecordStream(final KafkaHelper kafkaHelper,
      final SpanStructureRepositoy repository) {
    this.kafkaHelper = kafkaHelper;
    this.repository = repository;
    this.topology = this.buildTopology();
    this.props = kafkaHelper.newDefaultStreamProperties();
  }

  /* default */ void onStart(@Observes final StartupEvent event) { // NOPMD
    this.streams = new KafkaStreams(this.topology, this.props);
    this.streams.cleanUp();
    this.streams.setStateListener(new ErrorStateListener());

    this.streams.start();
  }

  /* default */ void onStop(@Observes final ShutdownEvent event) { // NOPMD
    this.streams.close();
  }

  public KafkaStreams getStream() {
    return this.streams;
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
          // System.out.println("Span: " + rec.getLandscapeToken());
          this.repository.add(rec).subscribeAsCompletionStage();
        });

    return builder.build();
  }

  private static class ErrorStateListener implements StateListener {

    @Override
    public void onChange(final State newState, final State oldState) {
      if (newState.equals(State.ERROR)) {

        if (LOGGER.isErrorEnabled()) {
          LOGGER.error(
              "Kafka Streams thread died. "
                  + "Are Kafka topic initialized? Quarkus application will shut down.");
        }
        Quarkus.asyncExit(-1);
      }

    }
  }

}
