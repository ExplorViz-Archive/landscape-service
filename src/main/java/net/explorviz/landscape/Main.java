package net.explorviz.landscape;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.kafka.KafkaStreamsMetrics;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import javax.inject.Inject;
import net.explorviz.landscape.kafka.RecordPersistingStream;
import net.explorviz.landscape.peristence.cassandra.DBHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@QuarkusMain
public class Main implements QuarkusApplication {

  private final static Logger LOGGER = LoggerFactory.getLogger(Main.class);

  public static void main(String... args) {
    Quarkus.run(Main.class, args);
  }


  private final DBHelper dbHelper;
  private final RecordPersistingStream stream;

  private final MeterRegistry registry;

  @Inject
  public Main(RecordPersistingStream stream, DBHelper dbHelper, MeterRegistry registry) {
    this.stream = stream;
    this.dbHelper = dbHelper;
    this.registry = registry;
  }

  @Override
  public int run(String... args) throws Exception {

    PrometheusMeterRegistry prometheusMeterRegistry =
        new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);


    dbHelper.initialize();

    this.stream.getStream().cleanUp();
    this.stream.getStream().start();

    KafkaStreamsMetrics ksm = new KafkaStreamsMetrics(this.stream.getStream());

    ksm.bindTo(registry);
    
    Runtime.getRuntime().addShutdownHook(new Thread(() -> this.stream.getStream().cleanUp()));
    System.out.println("#F");
    Quarkus.waitForExit();
    ksm.close();
    return 0;
  }



}
