package net.explorviz.landscape.peristence.cassandra;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import net.explorviz.landscape.persistence.model.SpanStructure;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class SpanStructureHelper {

  private SpanStructureHelper() {
    // utility class
  }

  /**
   * Creates a {@link SpanStructure} with completely random attributes.
   *
   * @return a random SpanStructure
   */
  public static SpanStructure randomSpanStructure() {
    final SpanStructure.Builder builder = new SpanStructure.Builder();
    builder.withLandscapeToken(RandomStringUtils.randomAlphanumeric(32))
        .withTimestamp(RandomUtils.nextInt(1_614_591_055, 1_714_591_055))
        .withHostIpAddress(randomIp()).withHostName(RandomStringUtils.randomAlphabetic(10))
        .withApplicationName(RandomStringUtils.randomAlphabetic(10))
        .withInstanceId(RandomStringUtils.randomNumeric(3))
        .withHashCode(RandomStringUtils.randomAlphanumeric(32))
        .withApplicationLanguage(RandomStringUtils.randomAlphabetic(5)).withFqn(randomFqn());

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
  public static List<SpanStructure> randomSpanStructures(final int count, final boolean equalToken,
      final boolean increasingTime) {

    final List<SpanStructure> strs = new ArrayList<>(count);
    if (count <= 0) {
      return strs;
    }
    final SpanStructure fss = randomSpanStructure();
    strs.add(fss);
    for (int i = 0; i < count - 1; i++) {
      final SpanStructure ss = randomSpanStructure();
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
    final String[] parts = new String[4];
    IntStream.rangeClosed(0, 3).forEach(i -> parts[i] = RandomStringUtils.randomNumeric(1, 4));
    return String.join(".", parts);
  }

  private static String randomFqn() {
    final String[] parts = new String[3];
    IntStream.rangeClosed(0, 2).forEach(i -> parts[i] = RandomStringUtils.randomAlphabetic(1, 10));
    return String.join(".", parts);
  }

  @Test
  void testRandomSpanStructures() {
    List<SpanStructure> objectInTest = randomSpanStructures(3, true, true);

    String token = objectInTest.get(0).getLandscapeToken();

    for (SpanStructure s : objectInTest) {
      Assertions.assertEquals(token, s.getLandscapeToken());
    }

  }

}
