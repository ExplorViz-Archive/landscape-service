package net.explorviz.landscape.kafka;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SpanCacheTest {

  private SpanCache cache;

  @BeforeEach
  void setUp() {
    this.cache = new SpanCache(2, false);
  }

  @Test
  void testPut() {
    this.cache.put("test1");
    this.cache.put("test2");

    assertTrue(this.cache.exists("test1"));
    assertTrue(this.cache.exists("test2"));
  }

  @Test
  void testLimit() {
    this.cache.put("test1");
    this.cache.put("test2");
    this.cache.put("test3");

    assertFalse(this.cache.exists("test1"));
    assertTrue(this.cache.exists("test2"));
    assertTrue(this.cache.exists("test3"));
  }



}
