package net.explorviz.landscape;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import net.explorviz.landscape.helper.SampleLoader;

@QuarkusMain
public class Main implements QuarkusApplication {

  public static void main(String... args) {
    Quarkus.run(Main.class, args);
  }

  @Override
  public int run(String... args) throws Exception {

    System.out.println(SampleLoader.load());
    Quarkus.waitForExit();
    return 0;
  }
}
