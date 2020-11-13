package net.explorviz.landscape.events;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import net.explorviz.avro.TokenEvent;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ApplicationScoped
public class TokenEventConsumer {

  private static final Logger LOGGER = LoggerFactory.getLogger(TokenEventConsumer.class);

  @Inject
  public TokenEventConsumer() {
  }

  @Incoming("token-events")
  public void process(TokenEvent event) {
    LOGGER.info("Received event {}", event);
    switch (event.getType()) {
      case CREATED:

        break;
      case DELETED:

        break;
      default:
        // Irrelevant event, do nothing
        break;
    }
  }


}
