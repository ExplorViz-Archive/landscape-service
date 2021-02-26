package net.explorviz.landscape.peristence.cassandra;

import com.datastax.oss.quarkus.runtime.api.session.QuarkusCqlSession;
import com.datastax.oss.quarkus.test.CassandraTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import javax.inject.Inject;
import net.explorviz.landscape.KafkaTestResource;
import net.explorviz.landscape.peristence.model.SpanStructure;
import net.explorviz.landscape.testhelper.SpanStructureHelper;
import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link net.explorviz.landscape.peristence.SpanStructureRepositoy}. The test are run
 * against an in-memory Cassandra
 * database.
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
  void insertNew() {
    SpanStructure ss = SpanStructureHelper.randomSpanStructure();
    System.out.println(ss);
    repository.add(ss).await().indefinitely();
  }

  /**
   * Insert an entity into the database that already exists, i.e., has identical attributes.
   */
  @Test
  void insertDuplicate() {

  }

  /**
   * Insert an entity into the database with the same primary key of an existing entity.
   */
  @Test
  void update() {

  }

  /**
   * Find all SpanStructures given a landscape token.
   */
  @Test
  void getByToken() {

  }

  /**
   * Find all SpanStructures given a landscape token that does not exist in the database.
   */
  @Test
  void getByUnknownToken() {

  }

  /**
   * Find all SpanStructures given a landscape token in a specific time range.
   */
  @Test
  void getByTokenBetween() {

  }

  /**
   * Find all SpanStructures given a landscape token in a specific time range
   * that does not contain any entities.
   */
  @Test
  void getByTokenBetweenEmpty() {

  }


  /**
   * Delete all entities with the same token.
   */
  @Test
  void deleteByToken() {

  }


}
