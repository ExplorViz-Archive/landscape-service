package net.explorviz.landscape;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import javax.inject.Inject;
import net.explorviz.landscape.kafka.SpanToRecordStream;
import net.explorviz.landscape.peristence.cassandra.DbHelper;

/**
 * Entry point for the Quarkus application.
 */
@QuarkusMain
public class Main implements QuarkusApplication {

  private final DbHelper dbHelper;
  private final SpanToRecordStream stream;
  
  @Inject
  public Main(final SpanToRecordStream stream, final DbHelper dbHelper) {
    this.stream = stream;
    this.dbHelper = dbHelper;
  }

  public static void main(final String... args) {
    Quarkus.run(Main.class, args);
  }

  @Override
  public int run(final String... args) throws Exception {

    this.dbHelper.initialize();

    this.stream.getStream().cleanUp();
    this.stream.getStream().start();


    Runtime.getRuntime().addShutdownHook(new Thread(() -> this.stream.getStream().cleanUp()));
    Quarkus.waitForExit();
    return 0;
  }


}
