package net.explorviz.landscape.peristence.cassandra;

import com.datastax.oss.quarkus.runtime.api.session.QuarkusCqlSession;
import com.datastax.oss.quarkus.test.CassandraTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import java.util.List;
import javax.inject.Inject;
import net.explorviz.landscape.KafkaTestResource;
import net.explorviz.landscape.peristence.model.SpanStructure;
import net.explorviz.landscape.testhelper.SpanStructureHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link ReactiveSpanStructureRepositoryImpl}.
 */
@QuarkusTest
@QuarkusTestResource(CassandraTestResource.class)
@QuarkusTestResource(KafkaTestResource.class)
@TestProfile(CassandraTestProfile.class)
class ReactiveSpanStructureRepositoryTest {


  private final ReactiveSpanStructureRepositoryImpl repository;

  private final QuarkusCqlSession session;


  @Inject
  public ReactiveSpanStructureRepositoryTest(
      final ReactiveSpanStructureRepositoryImpl repository, QuarkusCqlSession session) {
    this.repository = repository;
    this.session = session;
  }

  /**
   * Insert a new SpanStructure into the database.
   */
  @Test
  void insertNewRetrieve() {
    SpanStructure ss = SpanStructureHelper.randomSpanStructure();
    System.out.println(ss);
    repository.add(ss).await().indefinitely();

    // Retrieve
    SpanStructure got =
        repository.getAll(ss.getLandscapeToken()).collectItems().first().await().indefinitely();

    Assertions.assertEquals(ss, got);
  }

  /**
   * Insert an entity into the database that already exists, i.e., has identical attributes.
   */
  @Test
  void insertDuplicate() {
    SpanStructure ss = SpanStructureHelper.randomSpanStructure();
    System.out.println(ss);
    repository.add(ss).await().indefinitely();
    repository.add(ss).await().indefinitely();

    // Retrieve
    SpanStructure got =
        repository.getAll(ss.getLandscapeToken()).collectItems().first().await().indefinitely();

    Assertions.assertEquals(ss, got);
  }

  /**
   * Insert an entity into the database with the same primary key of an existing entity.
   */
  @Test
  void update() {
    SpanStructure ss = SpanStructureHelper.randomSpanStructure();
    System.out.println(ss);
    repository.add(ss).await().indefinitely();

    ss.setApplicationLanguage("New App name");
    repository.add(ss).await().indefinitely();

    // Retrieve
    SpanStructure got =
        repository.getAll(ss.getLandscapeToken()).collectItems().first().await().indefinitely();

    Assertions.assertEquals(ss, got);
  }

  /**
   * Find all SpanStructures given a landscape token that does not exist in the database.
   */
  @Test
  void getByUnknownToken() {
    SpanStructure got =
        repository.getAll("unknown").collectItems().first().await().indefinitely();
    Assertions.assertNull(got);
  }

  /**
   * Find all SpanStructures given a landscape token in a specific time range.
   */
  @Test
  void getByTokenBetween() {
    List<SpanStructure> spanstrs = SpanStructureHelper.randomSpanStructures(20, true, true);
    spanstrs.forEach(s -> repository.add(s).await().indefinitely());

    // Retrieve all but the first an the last in the list
    String tok = spanstrs.get(0).getLandscapeToken();
    long startTs = spanstrs.get(1).getTimestamp();
    long endTs = spanstrs.get(18).getTimestamp();

    List<SpanStructure> got =
        repository.getBetween(tok, startTs, endTs).collectItems().asList().await().indefinitely();

    Assertions.assertEquals(18, got.size());


  }

  /**
   * Find all SpanStructures given a landscape token in a specific time range
   * that does not contain any entities.
   */
  @Test
  void getByTokenBetweenEmpty() {
    List<SpanStructure> spanstrs = SpanStructureHelper.randomSpanStructures(20, true, true);
    spanstrs.forEach(s -> repository.add(s).await().indefinitely());

    // Retrieve all but the first an the last in the list
    String tok = spanstrs.get(0).getLandscapeToken();
    long startTs = spanstrs.get(19).getTimestamp()+1;
    long endTs = startTs + 20;

    List<SpanStructure> got =
        repository.getBetween(tok, startTs, endTs).collectItems().asList().await().indefinitely();

    Assertions.assertEquals(0, got.size());
  }


  /**
   * Delete all entities with the same token.
   */
  @Test
  void deleteByToken() {
    List<SpanStructure> spanstrs = SpanStructureHelper.randomSpanStructures(20, true, true);
    spanstrs.forEach(s -> repository.add(s).await().indefinitely());

    // Retrieve all but the first an the last in the list
    String tok = spanstrs.get(0).getLandscapeToken();
    repository.deleteAll(tok).await().indefinitely();

    List<SpanStructure> got =
        repository.getAll(tok).collectItems().asList().await().indefinitely();

    Assertions.assertEquals(0, got.size());
  }


}
