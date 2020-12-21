package net.explorviz.landscape.peristence.cassandra;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import net.explorviz.avro.landscape.flat.Application;
import net.explorviz.avro.landscape.flat.LandscapeRecord;
import net.explorviz.avro.landscape.flat.Node;
import net.explorviz.landscape.helper.SampleLoader;
import net.explorviz.landscape.peristence.QueryException;
import net.explorviz.landscape.peristence.cassandra.mapper.LandscapeRecordMapper;
import net.explorviz.landscape.peristence.cassandra.specifications.InsertLandscapeRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link LandscapeRecordRepository}. The test are run against an in-memory
 * Cassandra database.
 */
class LandscapeRecordRepositoryTest extends CassandraTest {


  private LandscapeRecordMapper mapper;

  private LandscapeRecordRepository repository;

  @BeforeEach
  void setUp() {
    this.db.initialize();
    mapper = new LandscapeRecordMapper(this.db);
    this.repository = new LandscapeRecordRepository(this.db, this.mapper);
  }


  @Test
  void getAll() throws IOException, QueryException {
    List<LandscapeRecord> records = SampleLoader.loadSampleApplication();
    for (LandscapeRecord r : records) {
      InsertLandscapeRecord s = new InsertLandscapeRecord(r, mapper);
      sess.execute(s.toQuery());
    }
    final String token = records.get(0).getLandscapeToken();


    // Use hashsets to ignore order when comparing
    HashSet<LandscapeRecord> forToken =
        records.stream().filter(r -> token.equals(r.getLandscapeToken()))
            .collect(Collectors.toCollection(HashSet::new));
    HashSet<LandscapeRecord> got = new HashSet<>(repository.getAll(token));

    Assertions.assertEquals(forToken, got);
  }

  @Test
  void addNew() throws IOException, QueryException {
    List<LandscapeRecord> records = SampleLoader.loadSampleApplication();
    for (LandscapeRecord r : records) {
      InsertLandscapeRecord s = new InsertLandscapeRecord(r, mapper);
      sess.execute(s.toQuery());
    }
    Node node = new Node("0.0.0.0", "localhost");
    Application app = new Application("SampleApplication", "1234",  "java");
    String package$ = "net.explorviz.test";
    String class$ = "SampleClass";
    String method = "sampleMethod()";
    LandscapeRecord toAdd = LandscapeRecord.newBuilder()
        .setLandscapeToken("test_token")
        .setNode(node)
        .setApplication(app)
        .setTimestamp(1590231993321L)
        .setPackage$(package$)
        .setClass$(class$)
        .setMethod(method)
        .setHashCode("1234")
        .build();

    repository.add(toAdd);

    // Find
    List<LandscapeRecord> rec = repository.getAll(toAdd.getLandscapeToken());
    Assertions.assertEquals(1, rec.size());
    Assertions.assertEquals(toAdd, rec.get(0));
  }

  @Test
  void addNewAsync() throws IOException, QueryException {
    List<LandscapeRecord> records = SampleLoader.loadSampleApplication();

    for (LandscapeRecord r : records) {
      InsertLandscapeRecord s = new InsertLandscapeRecord(r, mapper);
      sess.execute(s.toQuery());
    }

    Node node = new Node("0.0.0.0", "localhost");
    Application app = new Application("SampleApplication", "1234",  "java");
    String package$ = "net.explorviz.test";
    String class$ = "SampleClass";
    String method = "sampleMethod()";
    LandscapeRecord toAdd = LandscapeRecord.newBuilder()
        .setLandscapeToken("test_token")
        .setNode(node)
        .setApplication(app)
        .setTimestamp(1590231993321L)
        .setPackage$(package$)
        .setClass$(class$)
        .setMethod(method)
        .setHashCode("1234")
        .build();

    repository.addAsync(toAdd).toCompletableFuture().join();

    // Find
    List<LandscapeRecord> rec = repository.getAll(toAdd.getLandscapeToken());
    Assertions.assertEquals(1, rec.size());
    Assertions.assertEquals(toAdd, rec.get(0));
  }

  @Test
  void addWithoutToken() {
    Node node = new Node("0.0.0.0", "localhost");
    Application app = new Application("SampleApplication", "1234", "java");
    String package$ = "net.explorviz.test";
    String class$ = "SampleClass";
    String method = "sampleMethod()";
    LandscapeRecord toAdd = LandscapeRecord.newBuilder()
        .setLandscapeToken("")
        .setNode(node)
        .setApplication(app)
        .setTimestamp(1590231993321L)
        .setPackage$(package$)
        .setClass$(class$)
        .setMethod(method)
        .setHashCode("12345")
        .build();

    Assertions.assertThrows(QueryException.class, () -> repository.add(toAdd));
  }

  @Test
  void addExisting() throws IOException, QueryException {
    List<LandscapeRecord> records = SampleLoader.loadSampleApplication();
    for (LandscapeRecord r : records) {
      InsertLandscapeRecord s = new InsertLandscapeRecord(r, mapper);
      sess.execute(s.toQuery());
    }

    String token = records.get(0).getLandscapeToken();

    // Should not add another record with same specs
    repository.add(records.get(0));
    int got = repository.getAll(token).size();
    long want = records.stream().filter(r -> token.equals(r.getLandscapeToken())).count();
    Assertions.assertEquals(want, got);
  }


  @Test
  void deleteById() throws IOException, QueryException {

    List<LandscapeRecord> records = SampleLoader.loadSampleApplication();
    final String token = records.get(0).getLandscapeToken();
    for (LandscapeRecord r : records) {
      InsertLandscapeRecord s = new InsertLandscapeRecord(r, mapper);
      sess.execute(s.toQuery());
    }
    repository.deleteAll(token);

    Assertions.assertEquals(0, repository.getAll(token).size());

  }

  @Test
  void deleteByUnknownId() throws IOException, QueryException {

    List<LandscapeRecord> records = SampleLoader.loadSampleApplication();
    final String token = "unknown";
    for (LandscapeRecord r : records) {
      InsertLandscapeRecord s = new InsertLandscapeRecord(r, mapper);
      sess.execute(s.toQuery());
    }
    repository.deleteAll(token);
  }
}
