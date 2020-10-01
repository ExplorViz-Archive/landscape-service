package net.explorviz.landscape;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import javax.inject.Inject;
import net.explorviz.landscape.kafka.RecordPersistingStream;

@QuarkusMain
public class Main implements QuarkusApplication {

  public static void main(String... args) {
    Quarkus.run(Main.class, args);
  }



  private final RecordPersistingStream stream;

  @Inject
  public Main(RecordPersistingStream stream) {
    this.stream = stream;
  }

  @Override
  public int run(String... args) throws Exception {
    this.stream.getStream().cleanUp();
    this.stream.getStream().start();

    Runtime.getRuntime().addShutdownHook(new Thread(() -> this.stream.getStream().cleanUp()));
    Quarkus.waitForExit();
    return 0;
  }


}
