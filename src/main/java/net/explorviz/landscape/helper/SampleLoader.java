package net.explorviz.landscape.helper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import net.explorviz.avro.landscape.flat.LandscapeRecord;

/**
 * Loads sample {@link LandscapeRecord} objects out of a json file located in the resources
 * directory. Used for testing/debugging only.
 */
public final class SampleLoader {

  private static final String SIMPLE = "samples/sampleApplicationRecords.json";

  private SampleLoader() {
    /* Utility */
  }

  /**
   * Loads a list of {@link LandscapeRecord} from file {@code sampleApplicationRecords.json}. This
   * file contains the (unique) records generated by instrumenting the sample application.
   *
   * @return the list of records
   * @throws IOException if the list could not be loaded
   */
  public static List<LandscapeRecord> loadSampleApplication() throws IOException {
    final InputStream recordInputStream = // NOPMD
        Thread.currentThread().getContextClassLoader().getResourceAsStream(SIMPLE);

    final ObjectMapper mapper = new JsonMapper();
    return mapper.readValue(recordInputStream, new TypeReference<List<LandscapeRecord>>() {});
  }

}
