package net.explorviz.landscape.peristence.cassandra;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.datastax.oss.quarkus.runtime.api.session.QuarkusCqlSession;
import com.datastax.oss.quarkus.test.CassandraTestResource;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import net.explorviz.landscape.persistence.model.SpanStructure;
import net.explorviz.landscape.service.cassandra.ReactiveSpanStructureService;

/**
 * Tests for the {@link ReactiveSpanStructureService}.
 */
@QuarkusTest
@QuarkusTestResource(CassandraTestResource.class)
@QuarkusTestResource(KafkaTestResource.class)
class ReactiveSpanStructureServiceTest {
	
  @Inject QuarkusCqlSession session;

  @Inject
  ReactiveSpanStructureService reactiveSpanStructureService;
  
  @BeforeEach
  void truncateTables() {
    session.execute("TRUNCATE explorviz.span_structure");
  }

  /**
   * Insert a new SpanStructure into the database.
   */
  @Test
  void insertNewRetrieve() {
    final SpanStructure ss = SpanStructureHelper.randomSpanStructure();
    UniAssertSubscriber<Void> uniSubscriber = this.reactiveSpanStructureService.add(ss).subscribe().withSubscriber(UniAssertSubscriber.create());

    uniSubscriber.awaitItem().assertCompleted().assertItem(null);
    
    // Retrieve
    UniAssertSubscriber<List<SpanStructure>> uniListSubscriber =
        this.reactiveSpanStructureService.findByToken(ss.getLandscapeToken()).collect().asList().subscribe().withSubscriber(UniAssertSubscriber.create());

    uniListSubscriber.awaitItem().assertCompleted().assertItem(Arrays.asList(ss));
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
    this.reactiveSpanStructureService.update(ss).await().indefinitely();

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
 * @throws InterruptedException 
   */
  @Test
  void deleteByToken() {
    final List<SpanStructure> spanstrs = SpanStructureHelper.randomSpanStructures(1000, true, true);
    spanstrs.forEach(s -> this.reactiveSpanStructureService.add(s).await().indefinitely());

    final String tok = spanstrs.get(0).getLandscapeToken();
    Uni<Void> uni = this.reactiveSpanStructureService.deleteByToken(tok);
    
    UniAssertSubscriber<Void> subscriber = uni.subscribe().withSubscriber(UniAssertSubscriber.create());

	subscriber.awaitItem().assertCompleted().assertItem(null);
        
    Uni<List<SpanStructure>> uniFindList = this.reactiveSpanStructureService.findByToken(tok).collect().asList();

	UniAssertSubscriber<List<SpanStructure>> uniFindListAssertSubscriber = uniFindList.subscribe()
			.withSubscriber(UniAssertSubscriber.create());

	uniFindListAssertSubscriber.awaitItem().assertCompleted().assertItem(new ArrayList<>());
    
  }
  
  /**
   * Delete all entities with the same token.
 * @throws InterruptedException 
   */
  @Test
  void deleteByTokenAwaitIndef() {
    final List<SpanStructure> spanstrs = SpanStructureHelper.randomSpanStructures(1000, true, true);
    spanstrs.forEach(s -> this.reactiveSpanStructureService.add(s).await().indefinitely());

    final String tok = spanstrs.get(0).getLandscapeToken();
    this.reactiveSpanStructureService.deleteByToken(tok).await().indefinitely();   
   
        
    List<SpanStructure> uniFindList = this.reactiveSpanStructureService.findByToken(tok).collect().asList().await().indefinitely();

	Assertions.assertEquals(new ArrayList<>(), uniFindList);
    
  }


}
