package net.explorviz.landscape.kafka;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.quarkus.scheduler.Scheduled;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import net.explorviz.avro.SpanStructure;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Builds a KafkaStream transformer that filters out already saved spans.
 */
@ApplicationScoped
public class SpanFilterTransformer implements
    Transformer<String, SpanStructure, KeyValue<String, SpanStructure>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SpanFilterTransformer.class);

  // Logged and reset every n seconds
  private final AtomicInteger lastReceivedTotalSpans = new AtomicInteger(0);
  private final AtomicInteger lastReceivedUnknownSpans = new AtomicInteger(0);
  private final AtomicInteger lastReceivedCachedSpans = new AtomicInteger(0);

  private KeyValueStore<String, Integer> alreadySavedSpans;

  @Inject
  /* default */ MeterRegistry registry; // NOCS

  Counter counter;

  @Override
  public void init(final ProcessorContext context) {

    this.alreadySavedSpans = (KeyValueStore<String, Integer>) context.getStateStore("cachedSpans");
    counter = this.registry.counter("alex-test",
        List.of(Tag.of("task_id", context.taskId().toString()),
            Tag.of("partition_id", String.valueOf(context.taskId().partition()))));
  }

  @Override
  public KeyValue<String, SpanStructure> transform(final String key, final SpanStructure value) {
    this.lastReceivedTotalSpans.incrementAndGet();
    counter.increment();
    if (alreadySavedSpans.get(value.getHashCode()) == null) {
      this.lastReceivedUnknownSpans.incrementAndGet();
      alreadySavedSpans.put(value.getHashCode(), 1);
      return KeyValue.pair(key, value);
    } else {
      this.lastReceivedCachedSpans.incrementAndGet();
      // As documentation states, this will remove the record from the returned stream
      return null;
    }
  }

  @Override
  public void close() {
    // Do nothing
  }

  @Scheduled(every = "{explorviz.log.span.interval}")
    // NOPMD
  void logStatus() { // NOPMD
    final int totalSpans = this.lastReceivedTotalSpans.getAndSet(0);
    final int cachedSpans = this.lastReceivedCachedSpans.getAndSet(0);
    final int newSpans = this.lastReceivedUnknownSpans.getAndSet(0);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "Received {} " + "spans: {} cached / already saved spans, " + "{} unknown / saved spans.",
          totalSpans, cachedSpans, newSpans);
    }
  }

}
