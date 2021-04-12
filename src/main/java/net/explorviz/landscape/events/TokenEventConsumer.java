package net.explorviz.landscape.events;

import io.smallrye.mutiny.infrastructure.Infrastructure;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import net.explorviz.avro.EventType;
import net.explorviz.avro.TokenEvent;
import net.explorviz.landscape.service.ReactiveLandscapeService;
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
   * @param event the token-event
   */
  @Incoming("token-events")
  public void process(final TokenEvent event) {
    LOGGER.info("Received event {}", event);
    if (event.getType() == EventType.DELETED) {
      LOGGER.info("Deleting landscape with token {}", event.getToken());
      this.service.deleteLandscape(event.getToken());
    } else if (event.getType() == EventType.CLONED) {
      LOGGER.info("Cloning landscapes for token {}", event.getToken());
      this.service.cloneLandscape(event.getToken(), event.getClonedToken())
          .runSubscriptionOn(Infrastructure.getDefaultWorkerPool())
          .subscribe().with(
            item -> LOGGER.debug("Cloned landscape for {}", item.getLandscapeToken()),
            failure -> LOGGER.error("Failed to duplicate", failure),
            () -> LOGGER.info("Cloned all landscapes for {}", event.getToken()));
    }
  }


}
