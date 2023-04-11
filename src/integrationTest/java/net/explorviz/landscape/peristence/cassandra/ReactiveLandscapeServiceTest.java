package net.explorviz.landscape.peristence.cassandra;

import com.datastax.oss.quarkus.test.CassandraTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import net.explorviz.landscape.persistence.model.SpanStructure;
import net.explorviz.landscape.service.cassandra.ReactiveLandscapeServiceImpl;
import net.explorviz.landscape.service.cassandra.ReactiveSpanStructureService;
import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link ReactiveLandscapeServiceImpl} and {@link ReactiveSpanStructureService}.
 */
@QuarkusTest
@QuarkusTestResource(CassandraTestResource.class)
@QuarkusTestResource(KafkaTestResource.class)
class ReactiveLandscapeServiceTest {

  // private static final Logger LOGGER =
  // LoggerFactory.getLogger(ReactiveLandscapeServiceTest.class);

  @Inject
  ReactiveLandscapeServiceImpl reactiveLandscapeService;

  @Inject
  ReactiveSpanStructureService reactiveSpanStructureService;

  @Test
  void cloneToken() {
    final List<SpanStructure> spanstrs = SpanStructureHelper.randomSpanStructures(20, true, true);

    spanstrs.forEach(s -> this.reactiveSpanStructureService.add(s).subscribe()
        .withSubscriber(UniAssertSubscriber.create()).awaitItem().assertCompleted());

    final String tok = spanstrs.get(0).getLandscapeToken();
    final String anotherToken = "123abc";

    // transform to expected, i.e., set new token
    spanstrs.forEach(s -> s.setLandscapeToken(anotherToken));

    UniAssertSubscriber<List<SpanStructure>> uniCloneAssert =
        this.reactiveLandscapeService.cloneLandscape(anotherToken, tok).collect().asList()
            .subscribe().withSubscriber(UniAssertSubscriber.create());
    uniCloneAssert.awaitItem().assertCompleted().assertItem(spanstrs);

    // TODO check if old and cloned spans are in DB
  }

  @Test
  void deleteToken() {
    final List<SpanStructure> spanstrs = SpanStructureHelper.randomSpanStructures(20, true, true);
    spanstrs.forEach(s -> this.reactiveSpanStructureService.add(s).await().indefinitely());

    final String tok = spanstrs.get(0).getLandscapeToken();

    Uni<Void> uni = this.reactiveLandscapeService.deleteLandscape(tok);

    UniAssertSubscriber<Void> subscriber =
        uni.subscribe().withSubscriber(UniAssertSubscriber.create());

    subscriber.awaitItem().assertCompleted().assertItem(null);

    Uni<List<SpanStructure>> uniFindList =
        this.reactiveSpanStructureService.findByToken(tok).collect().asList();

    UniAssertSubscriber<List<SpanStructure>> uniFindListAssertSubscriber =
        uniFindList.subscribe().withSubscriber(UniAssertSubscriber.create());

    uniFindListAssertSubscriber.awaitItem().assertCompleted().assertItem(new ArrayList<>());
  }

}
