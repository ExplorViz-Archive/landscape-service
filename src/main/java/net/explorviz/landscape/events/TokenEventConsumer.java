package net.explorviz.landscape.events;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import net.explorviz.avro.EventType;
import net.explorviz.avro.TokenEvent;
import net.explorviz.landscape.service.usecase.LandscapeService;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ApplicationScoped
public class TokenEventConsumer {

  private static final Logger LOGGER = LoggerFactory.getLogger(TokenEventConsumer.class);

  private final LandscapeService service;

  @Inject
  public TokenEventConsumer(LandscapeService service) {
    this.service = service;
  }

  @Incoming("token-events")
  public void process(TokenEvent event) {
    LOGGER.info("Received event {}", event);
    if (event.getType() == EventType.DELETED) {
      LOGGER.info("Deleting landscape with token {}", event.getToken());
      service.deleteLandscape(event.getToken());
    }
  }


}
