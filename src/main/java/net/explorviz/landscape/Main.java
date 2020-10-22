package net.explorviz.landscape;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import javax.inject.Inject;
import net.explorviz.landscape.kafka.RecordPersistingStream;
import net.explorviz.landscape.peristence.cassandra.DBHelper;

@QuarkusMain
public class Main implements QuarkusApplication {

  public static void main(String... args) {
    Quarkus.run(Main.class, args);
  }


  private final DBHelper dbHelper;
  private final RecordPersistingStream stream;

  @Inject
  public Main(RecordPersistingStream stream, DBHelper dbHelper) {
    this.stream = stream;
    this.dbHelper = dbHelper;
  }

  @Override
  public int run(String... args) throws Exception {

    dbHelper.initialize();

    this.stream.getStream().cleanUp();
    this.stream.getStream().start();


    Runtime.getRuntime().addShutdownHook(new Thread(() -> this.stream.getStream().cleanUp()));
    Quarkus.waitForExit();
    return 0;
  }


}
