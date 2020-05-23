package net.explorviz.landscape;

import com.datastax.oss.driver.api.core.CqlSession;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import javax.inject.Inject;
import net.explorviz.landscape.helper.SampleLoader;

@QuarkusMain
public class Main implements QuarkusApplication {

  public static void main(String... args) {
    Quarkus.run(Main.class, args);
  }


  private CqlSession session;

  @Inject
  public Main(CqlSession session) {
    this.session = session;
  }

  @Override
  public int run(String... args) throws Exception {

    System.out.println(SampleLoader.loadSampleApplication());
    System.out.println(session);

    Quarkus.waitForExit();
    return 0;
  }
}
