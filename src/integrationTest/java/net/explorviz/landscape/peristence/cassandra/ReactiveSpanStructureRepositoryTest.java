package net.explorviz.landscape.peristence.cassandra;

import com.datastax.oss.quarkus.test.CassandraTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import java.util.List;
import javax.inject.Inject;
import net.explorviz.landscape.persistence.model.SpanStructure;
import net.explorviz.landscape.service.cassandra.ReactiveSpanStructureService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link ReactiveSpanStructureService}.
 */
@QuarkusTest
@QuarkusTestResource(CassandraTestResource.class)
@QuarkusTestResource(KafkaTestResource.class)
class ReactiveSpanStructureRepositoryTest {

  private final ReactiveSpanStructureService reactiveSpanStructureService;

  @Inject
  public ReactiveSpanStructureRepositoryTest(final ReactiveSpanStructureService reactiveSpanStructureService) {
    this.reactiveSpanStructureService = reactiveSpanStructureService;
  }

  /**
   * Insert a new SpanStructure into the database.
   */
  @Test
  void insertNewRetrieve() {
    final SpanStructure ss = SpanStructureHelper.randomSpanStructure();
    System.out.println(ss);
    this.reactiveSpanStructureService.add(ss).await().indefinitely();

    // Retrieve
    final SpanStructure got =
        this.reactiveSpanStructureService.findByToken(ss.getLandscapeToken()).collect().first().await().indefinitely();

    Assertions.assertEquals(ss, got);
  }

  /**
   * Insert an entity into the database that already exists, i.e., has identical attributes.
   */
  @Test
  void insertDuplicate() {
    final SpanStructure ss = SpanStructureHelper.randomSpanStructure();
    System.out.println(ss);
    this.reactiveSpanStructureService.add(ss).await().indefinitely();
    this.reactiveSpanStructureService.add(ss).await().indefinitely();

    // Retrieve
    final SpanStructure got =
        this.reactiveSpanStructureService.findByToken(ss.getLandscapeToken()).collect().first().await().indefinitely();

    Assertions.assertEquals(ss, got);
  }

  /**
   * Insert an entity into the database with the same primary key of an existing entity.
   */
  @Test
  void update() {
    final SpanStructure ss = SpanStructureHelper.randomSpanStructure();
    System.out.println(ss);
    this.reactiveSpanStructureService.add(ss).await().indefinitely();

    ss.setApplicationLanguage("New App name");
    this.reactiveSpanStructureService.add(ss).await().indefinitely();

    // Retrieve
    final SpanStructure got =
        this.reactiveSpanStructureService.findByToken(ss.getLandscapeToken()).collect().first().await().indefinitely();

    Assertions.assertEquals(ss, got);
  }

  /**
   * Find all SpanStructures given a landscape token that does not exist in the database.
   */
  @Test
  void getByUnknownToken() {
    final SpanStructure got =
        this.reactiveSpanStructureService.findByToken("unknown").collect().first().await().indefinitely();
    Assertions.assertNull(got);
  }

  /**
   * Find all SpanStructures given a landscape token in a specific time range.
   */
  @Test
  void getByTokenBetween() {
    final List<SpanStructure> spanstrs = SpanStructureHelper.randomSpanStructures(20, true, true);
    spanstrs.forEach(s -> this.reactiveSpanStructureService.add(s).await().indefinitely());

    // Retrieve all but the first an the last in the list
    final String tok = spanstrs.get(0).getLandscapeToken();
    final long startTs = spanstrs.get(1).getTimestamp();
    final long endTs = spanstrs.get(18).getTimestamp();

    final List<SpanStructure> got =
        this.reactiveSpanStructureService.findBetweenInterval(tok, startTs, endTs).collect().asList().await().indefinitely();

    Assertions.assertEquals(18, got.size());


  }

  /**
   * Find all SpanStructures given a landscape token in a specific time range that does not contain
   * any entities.
   */
  @Test
  void getByTokenBetweenEmpty() {
    final List<SpanStructure> spanstrs = SpanStructureHelper.randomSpanStructures(20, true, true);
    spanstrs.forEach(s -> this.reactiveSpanStructureService.add(s).await().indefinitely());

    // Retrieve all but the first an the last in the list
    final String tok = spanstrs.get(0).getLandscapeToken();
    final long startTs = spanstrs.get(19).getTimestamp() + 1;
    final long endTs = startTs + 20;

    final List<SpanStructure> got =
        this.reactiveSpanStructureService.findBetweenInterval(tok, startTs, endTs).collect().asList().await().indefinitely();

    Assertions.assertEquals(0, got.size());
  }


  /**
   * Delete all entities with the same token.
   */
  @Test
  void deleteByToken() {
    final List<SpanStructure> spanstrs = SpanStructureHelper.randomSpanStructures(20, true, true);
    spanstrs.forEach(s -> this.reactiveSpanStructureService.add(s).await().indefinitely());

    // Retrieve all but the first an the last in the list
    final String tok = spanstrs.get(0).getLandscapeToken();
    this.reactiveSpanStructureService.deleteByToken(tok).await().indefinitely();

    final List<SpanStructure> got =
        this.reactiveSpanStructureService.findByToken(tok).collect().asList().await().indefinitely();

    Assertions.assertEquals(0, got.size());
  }


}
