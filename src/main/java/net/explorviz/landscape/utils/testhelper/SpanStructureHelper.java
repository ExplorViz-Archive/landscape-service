package net.explorviz.landscape.utils.testhelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import net.explorviz.landscape.peristence.model.SpanStructure;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

public final class SpanStructureHelper {



  /**
   * Creates a {@link SpanStructure} with completely random attributes.
   *
   * @return a random SpanStructure
   */
  public static SpanStructure randomSpanStructure() {
    SpanStructure.Builder builder = new SpanStructure.Builder();
    builder.setLandscapeToken(RandomStringUtils.randomAlphanumeric(32))
        .setTimestamp(RandomUtils.nextInt(1614591055, 1714591055))
        .setHostIpAddress(randomIp())
        .setHostName(RandomStringUtils.randomAlphabetic(10))
        .setApplicationName(RandomStringUtils.randomAlphabetic(10))
        .setInstanceId(RandomStringUtils.randomNumeric(3))
        .setHashCode(RandomStringUtils.randomAlphanumeric(32))
        .setApplicationLanguage(RandomStringUtils.randomAlphabetic(5))
        .setFqn(randomFqn());

    return builder.build();
  }


  /**
   * Generates multiple span structures with increasing timestamp
   *
   * @param count          the amount to create
   * @param equalToken     if true, all SpanStructures will have the same landscape token
   * @param increasingTime if true, timestamps will be in increasing order
   * @return a list of span structures
   */
  public static List<SpanStructure> randomSpanStructures(int count, boolean equalToken,
                                                         boolean increasingTime) {

    List<SpanStructure> strs = new ArrayList<>(count);
    if (count <= 0) {
      return strs;
    }
    SpanStructure fss = randomSpanStructure();
    strs.add(fss);
    for (int i = 0; i < count - 1; i++) {
      SpanStructure ss = randomSpanStructure();
      if (equalToken) {
        ss.setLandscapeToken(fss.getLandscapeToken());
      }
      if (increasingTime) {
        ss.setTimestamp(fss.getTimestamp() + i + 1);
      }
      strs.add(ss);
    }
    return strs;
  }

  private static String randomIp() {
    String[] parts = new String[4];
    IntStream.rangeClosed(0, 3).forEach(i -> parts[i] = RandomStringUtils.randomNumeric(1, 4));
    return String.join(".", parts);
  }

  private static String randomFqn() {
    String[] parts = new String[3];
    IntStream.rangeClosed(0, 2).forEach(i -> parts[i] = RandomStringUtils.randomAlphabetic(1, 10));
    return String.join(".", parts);
  }

}
