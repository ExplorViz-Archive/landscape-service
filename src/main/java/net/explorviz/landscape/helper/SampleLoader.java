package net.explorviz.landscape.helper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import net.explorviz.landscape.LandscapeRecord;

/**
 * Loads sample {@link net.explorviz.landscape.LandscapeRecord} objects out of a json file located
 * in the resources directory. Used for testing/debugging only.
 */
public final class SampleLoader {

  private static String SAMPLEFILE_NAME = "samplerecords.json";

  private SampleLoader() {/* Utility */}


  /**
   * Loads a list of {@link LandscapeRecord} from file {@code samplerecords.json}.
   *
   * @return the list of records
   * @throws IOException if the list could not be loaded
   */
  public static List<LandscapeRecord> load() throws IOException {
    InputStream recordInputStream =
        SampleLoader.class.getClassLoader().getResourceAsStream(SAMPLEFILE_NAME);

    ObjectMapper mapper = new JsonMapper();
    return mapper.readValue(recordInputStream, new TypeReference<List<LandscapeRecord>>() {
    });
  }



}
