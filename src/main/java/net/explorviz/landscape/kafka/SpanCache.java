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
 * Caches whether a span has already been seen, based on its hash code. Does not actually cache
 * Spans, but only the hash codes. Backed by Guava Caches.
 */
@ApplicationScoped
public class SpanCache {

  private static final Logger LOGGER = LoggerFactory.getLogger(SpanCache.class);

  private final Cache<String, Boolean> cache;

  private final boolean log;

  /**
   * Creates a new cache.
   *
   * @param maxSize the maximum number of span id to cache
   * @param logstats if true, logs cache's stats every 10 seconds
   */
  public SpanCache(@ConfigProperty(name = "explorviz.landscape.cache.maxsize") final int maxSize,
      @ConfigProperty(name = "explorviz.landscape.cache.logstats") final boolean logstats) {
    this.cache = CacheBuilder.newBuilder().maximumSize(maxSize).recordStats().build();
    this.log = logstats;
  }

  /**
   * Checks whether a hashcode is in the cache, i.e., has already been seen recently.
   *
   * @param hashCode the hashcode of the span
   * @return {@code true} iff the fingerprint is in the cache.
   */
  public boolean exists(final String hashCode) {
    return this.cache.getIfPresent(hashCode) != null;
  }

  /**
   * Put a span's hashcode in the cache. Subsequent calls to {@link #exists(String)} will return
   * {@code true} for this hash code.
   *
   * @param hashCode the hash code
   */
  public void put(final String hashCode) {
    this.cache.put(hashCode, true);
  }

  /**
   * Emits a status log about the cache's performance every 10s. Must be package-private.
   */
  @Scheduled(every = "10s")
  // NOPMD
  /* default */void logStats() {
    if (this.log && LOGGER.isTraceEnabled()) {
      LOGGER.trace(this.toString());
    }
  }

  @Override
  public String toString() {
    final CacheStats stats = this.cache.stats();
    return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
        .append("avg load penalty", stats.averageLoadPenalty())
        .append("hit count", stats.hitCount()).append("hit rate", stats.hitRate()).toString();
  }

}
