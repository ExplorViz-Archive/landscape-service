package net.explorviz.landscape.testhelper;

import java.util.stream.IntStream;
import net.explorviz.landscape.peristence.model.SpanStructure;
import org.apache.commons.lang3.RandomStringUtils;

public final class SpanStructureHelper {



  /**
   * Creates a {@link SpanStructure} with completely random attributes.
   *
   * @return a random SpanStructure
   */
  public static SpanStructure randomSpanStructure() {
    SpanStructure.Builder builder = new SpanStructure.Builder();
    builder.setLandscapeToken(RandomStringUtils.randomAlphanumeric(32))
        .setTimestamp(System.currentTimeMillis())
        .setHostIpAddress(randomIp())
        .setHostName(RandomStringUtils.randomAlphabetic(10))
        .setApplicationName(RandomStringUtils.randomAlphabetic(10))
        .setInstanceId(RandomStringUtils.randomNumeric(3))
        .setHashCode(RandomStringUtils.randomAlphanumeric(32))
        .setApplicationLanguage(RandomStringUtils.randomAlphabetic(5))
        .setFqn(randomFqn());

    return builder.build();
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
