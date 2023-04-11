package net.explorviz.landscape.kafka;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import net.explorviz.avro.Span;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;

/**
 * Builds a KafkaStream transformer that filters out already saved spans.
 */
@ApplicationScoped
public class SpanFilterTransformer implements Transformer<String, Span, KeyValue<String, Span>> {

  private static final String METRIC_TAG_TASK_ID_KEY = "task_id";
  private static final String METRIC_TAG_PARTITION_ID_KEY = "partition_id";

  private static final String METRIC_NAME_RECEIVED_SPANS = "explorviz_total_received_spans";
  private static final String METRIC_NAME_CACHED_SPANS = "explorviz_total_cached_spans";
  private static final String METRIC_NAME_DISCARDED_SPANS = "explorviz_total_discarded_spans";

  @Inject
  /* default */ MeterRegistry registry; // NOCS

  private KeyValueStore<String, Integer> alreadySavedSpans;

  private Counter counterAllSpans;
  private Counter counterCachedSpans;
  private Counter counterDiscardedSpans;

  @Override
  public void init(final ProcessorContext context) {

    this.alreadySavedSpans = context.getStateStore("cachedSpans");

    counterAllSpans = this.registry.counter(METRIC_NAME_RECEIVED_SPANS,
        List.of(Tag.of(METRIC_TAG_TASK_ID_KEY, context.taskId().toString()),
            Tag.of(METRIC_TAG_PARTITION_ID_KEY, String.valueOf(context.taskId().partition()))));

    counterCachedSpans = this.registry.counter(METRIC_NAME_CACHED_SPANS,
        List.of(Tag.of(METRIC_TAG_TASK_ID_KEY, context.taskId().toString()),
            Tag.of(METRIC_TAG_PARTITION_ID_KEY, String.valueOf(context.taskId().partition()))));

    counterDiscardedSpans = this.registry.counter(METRIC_NAME_DISCARDED_SPANS,
        List.of(Tag.of(METRIC_TAG_TASK_ID_KEY, context.taskId().toString()),
            Tag.of(METRIC_TAG_PARTITION_ID_KEY, String.valueOf(context.taskId().partition()))));

    counterCachedSpans.increment(alreadySavedSpans.approximateNumEntries());
  }

  @Override
  public KeyValue<String, Span> transform(final String key, final Span value) {
    counterAllSpans.increment();
    if (alreadySavedSpans.get(value.getHashCode()) == null) {
      alreadySavedSpans.put(value.getHashCode(), 1);
      counterCachedSpans.increment();
      return KeyValue.pair(key, value);
    } else {
      // As Kafka Streams documentation states, this will remove the record from the returned stream
      counterDiscardedSpans.increment();
      return null;
    }
  }

  @Override
  public void close() {
    // Do nothing
  }

}
