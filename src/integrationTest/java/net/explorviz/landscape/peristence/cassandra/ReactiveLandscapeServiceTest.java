package net.explorviz.landscape.peristence.cassandra;

import com.datastax.oss.quarkus.runtime.api.session.QuarkusCqlSession;
import com.datastax.oss.quarkus.test.CassandraTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import java.util.List;
import javax.inject.Inject;
import net.explorviz.landscape.peristence.model.SpanStructure;
import net.explorviz.landscape.service.ReactiveLandscapeService;
import net.explorviz.landscape.service.ReactiveLandscapeServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link ReactiveSpanStructureRepositoryImpl}.
 */
@QuarkusTest
@QuarkusTestResource(CassandraTestResource.class)
@QuarkusTestResource(KafkaTestResource.class)
@TestProfile(CassandraTestProfile.class)
class ReactiveLandscapeServiceTest {

  private final ReactiveLandscapeServiceImpl service;

  private final ReactiveSpanStructureRepositoryImpl repository;

  private final QuarkusCqlSession session;

  @Inject
  public ReactiveLandscapeServiceTest(final ReactiveSpanStructureRepositoryImpl repository,
      final ReactiveLandscapeServiceImpl service, final QuarkusCqlSession session) {
    this.repository = repository;
    this.service = service;
    this.session = session;
  }

  @Test
  void cloneToken() {
    final List<SpanStructure> spanstrs = SpanStructureHelper.randomSpanStructures(20, true, true);
    spanstrs.forEach(s -> this.repository.add(s).await().indefinitely());

    final String tok = spanstrs.get(0).getLandscapeToken();
    final String anotherToken = "123abc";

    this.service.cloneLandscape(anotherToken, tok).collectItems().asList().await().indefinitely();

    final List<SpanStructure> got = this.repository.getAll(anotherToken).collectItems().asList().await().indefinitely();

    Assertions.assertEquals(20, got.size());
  }

}
