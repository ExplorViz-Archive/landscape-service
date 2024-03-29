package net.explorviz.landscape.events;

import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.smallrye.reactive.messaging.kafka.Record;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import net.explorviz.avro.EventType;
import net.explorviz.avro.TokenEvent;
import net.explorviz.landscape.service.cassandra.ReactiveLandscapeService;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wait for and reacts to token-events dispatches by the User-Service. Such events are read from a
 * corresponding Kafka topic and contain information about changes to tokens (e.g. deletions).
 */
@ApplicationScoped
public class TokenEventConsumer {

  private static final Logger LOGGER = LoggerFactory.getLogger(TokenEventConsumer.class);

  private final ReactiveLandscapeService service;

  @Inject
  public TokenEventConsumer(final ReactiveLandscapeService service) {
    this.service = service;
  }

  /**
   * Processes token-events in a background, called by reactive messaging framework. If a token was
   * deleted, all corresponding entries are removed from the database.
   *
   * @param record the token-event
   */
  @Incoming("token-events")
  public void process(final Record<String, TokenEvent> record) {

    final String tokenValueKey = record.key();
    final TokenEvent tokenEvent = record.value();

    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Received event for token value {} with event {}", tokenValueKey, tokenEvent);
    }

    // tombstone record
    if (tokenEvent == null) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Deleting landscape for token value{}", tokenValueKey);
      }
      this.service.deleteLandscape(tokenValueKey);
    } else if (tokenEvent.getType() == EventType.CLONED) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Cloning landscapes for token {}", tokenEvent.getToken());
      }
      this.service.cloneLandscape(tokenEvent.getToken().getValue(), tokenEvent.getClonedToken())
          .runSubscriptionOn(Infrastructure.getDefaultWorkerPool()).subscribe()
          .with(item -> LOGGER.trace("Cloned landscape for {}", item.getLandscapeToken()),
              failure -> LOGGER.error("Failed to duplicate", failure),
              () -> LOGGER.trace("Cloned all landscapes for {}", tokenEvent.getToken()));
    }
  }

}
