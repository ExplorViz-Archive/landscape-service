package net.explorviz.landscape;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import net.explorviz.landscape.flat.LandscapeRecord;
import net.explorviz.landscape.helper.SampleLoader;
import net.explorviz.landscape.peristence.QueryException;
import net.explorviz.landscape.peristence.Repository;
import net.explorviz.landscape.service.usecase.UseCases;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@QuarkusMain
public class Main implements QuarkusApplication {

  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

  public static void main(String... args) {
    Quarkus.run(Main.class, args);
  }


  private final Repository<LandscapeRecord> repo;
  private final UseCases useCases;

  @Inject
  public Main(Repository<LandscapeRecord> repo, UseCases useCases) {
    this.repo = repo;
    this.useCases = useCases;
  }

  @Override
  public int run(String... args) throws Exception {
    //insertSampleData();
    Quarkus.waitForExit();
    return 0;
  }

  private void insertSampleData() throws IOException, QueryException {

    List<LandscapeRecord> records = SampleLoader.loadSampleApplication();
    for (LandscapeRecord record : records) {
      repo.add(record);
    }
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("Added {} records to repository", records.size());
    }
  }
}
