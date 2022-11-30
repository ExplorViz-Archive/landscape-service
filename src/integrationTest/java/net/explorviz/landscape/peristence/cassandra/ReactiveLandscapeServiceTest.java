package net.explorviz.landscape.peristence.cassandra;

import com.datastax.oss.quarkus.test.CassandraTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import java.util.List;
import javax.inject.Inject;
import net.explorviz.landscape.persistence.model.SpanStructure;
import net.explorviz.landscape.service.cassandra.ReactiveLandscapeServiceImpl;
import net.explorviz.landscape.service.cassandra.ReactiveSpanStructureService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link ReactiveLandscapeServiceImpl} and {@link ReactiveSpanStructureService}.
 */
@QuarkusTest
@QuarkusTestResource(CassandraTestResource.class)
@QuarkusTestResource(KafkaTestResource.class)
class ReactiveLandscapeServiceTest {

  private final ReactiveLandscapeServiceImpl service;

  private final ReactiveSpanStructureService reactiveSpanStructureService;

  @Inject
  public ReactiveLandscapeServiceTest(final ReactiveSpanStructureService reactiveSpanStructureService,
      final ReactiveLandscapeServiceImpl service) {
    this.reactiveSpanStructureService = reactiveSpanStructureService;
    this.service = service;
  }

  @Test
  void cloneToken() {
    final List<SpanStructure> spanstrs = SpanStructureHelper.randomSpanStructures(20, true, true);
    spanstrs.forEach(s -> this.reactiveSpanStructureService.add(s).await().indefinitely());

    final String tok = spanstrs.get(0).getLandscapeToken();
    final String anotherToken = "123abc";

    this.service.cloneLandscape(anotherToken, tok).collect().asList().await().indefinitely();

    final List<SpanStructure> got =
        this.reactiveSpanStructureService.findByToken(anotherToken).collect().asList().await().indefinitely();

    Assertions.assertEquals(20, got.size());
  }

  @Test
  void deleteToken() {
    final List<SpanStructure> spanstrs = SpanStructureHelper.randomSpanStructures(20, true, true);
    spanstrs.forEach(s -> this.reactiveSpanStructureService.add(s).await().indefinitely());

    final String tok = spanstrs.get(0).getLandscapeToken();

    this.service.deleteLandscape(tok).await().indefinitely();

    final List<SpanStructure> got =
        this.reactiveSpanStructureService.findByToken(tok).collect().asList().await().indefinitely();

    Assertions.assertEquals(0, got.size());
  }

}
