package net.explorviz.landscape;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import net.explorviz.landscape.flat.LandscapeRecord;
import net.explorviz.landscape.helper.SampleLoader;
import net.explorviz.landscape.kafka.RecordPersistingStream;
import net.explorviz.landscape.peristence.QueryException;
import net.explorviz.landscape.peristence.Repository;
import net.explorviz.landscape.service.usecase.UseCases;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
