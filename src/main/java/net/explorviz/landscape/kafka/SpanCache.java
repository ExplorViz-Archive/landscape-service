package net.explorviz.landscape.kafka;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheStats;
import io.quarkus.scheduler.Scheduled;
import javax.enterprise.context.ApplicationScoped;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Caches whether a span has already been seen, based on its hash code.
 * Does not actually cache Spans, but only the hash codes.
 * Backed by Guava Caches.
 */
@ApplicationScoped
public class SpanCache {

  private static final Logger LOGGER = LoggerFactory.getLogger(SpanCache.class);

  private Cache<String, Boolean> cache;

  private final boolean log;

  /**
   * Creates a new cache.
   *
   * @param maxSize  the maximum number of span id to cache
   * @param logstats if true, logs cache's stats every 10 seconds
   */
  public SpanCache(
      @ConfigProperty(name = "explorviz.landscape.cache.maxsize") int maxSize,
      @ConfigProperty(name = "explorviz.landscape.cache.logstats", defaultValue = "1")
          boolean logstats
  ) {
    this.cache = CacheBuilder.newBuilder()
        .maximumSize(maxSize)
        .recordStats()
        .build();
    this.log = logstats;
  }

  /**
   * Checks whether a hashcode is in the cache, i.e., has already been seen recently.
   *
   * @param fingerprint the hashcode of the span
   * @return {@code true} iff the fingerprint is in the cache.
   */
  public boolean exists(String fingerprint) {
    return cache.getIfPresent(fingerprint) != null;
  }

  /**
   * Put a span's hashcode in the cache.
   * Subsequent calls to {@link #exists(String)} will return {@code true} for this hash code.
   *
   * @param fingerprint the hash code
   */
  public void put(String fingerprint) {
    cache.put(fingerprint, true);
  }


  @Scheduled(every = "10s")
  void logStats() {
    if (log) {
      LOGGER.info(this.toString());
    }
  }


  @Override
  public String toString() {
    CacheStats stats = cache.stats();
    return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
        .append("avg load penalty", stats.averageLoadPenalty())
        .append("hit count", stats.hitCount())
        .append("hit rate", stats.hitRate())
        .toString();
  }


}
