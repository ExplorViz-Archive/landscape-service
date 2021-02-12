package net.explorviz.landscape.peristence.cassandra;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
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
 * Tests for the {@link LandscapeRecordRepository}. The test are run against an in-memory Cassandra
 * database.
 */
class LandscapeRecordRepositoryTest extends CassandraTest {


  private LandscapeRecordMapper mapper;

  private LandscapeRecordRepository repository;

  @BeforeEach
  void setUp() {
    this.db.initialize();
    this.mapper = new LandscapeRecordMapper(this.db);
    this.repository = new LandscapeRecordRepository(this.db, this.mapper);
  }


  @Test
  void getAll() throws IOException, QueryException {
    final List<LandscapeRecord> records = SampleLoader.loadSampleApplication();
    for (final LandscapeRecord r : records) {
      final InsertLandscapeRecord s = new InsertLandscapeRecord(r, this.mapper);
      this.sess.execute(s.toQuery());
    }
    final String token = records.get(0).getLandscapeToken();


    // Use hashsets to ignore order when comparing
    final HashSet<LandscapeRecord> forToken =
        records.stream().filter(r -> token.equals(r.getLandscapeToken()))
            .collect(Collectors.toCollection(HashSet::new));
    final HashSet<LandscapeRecord> got = new HashSet<>(this.repository.getAll(token));

    Assertions.assertEquals(forToken, got);
  }

  @Test
  void addNew() throws IOException, QueryException {
    final List<LandscapeRecord> records = SampleLoader.loadSampleApplication();
    for (final LandscapeRecord r : records) {
      final InsertLandscapeRecord s = new InsertLandscapeRecord(r, this.mapper);
      this.sess.execute(s.toQuery());
    }
    final Node node = new Node("0.0.0.0", "localhost");
    final Application app = new Application("SampleApplication", "1234", "java");
    final String package$ = "net.explorviz.test";
    final String class$ = "SampleClass";
    final String method = "sampleMethod()";
    final LandscapeRecord toAdd = LandscapeRecord.newBuilder()
        .setLandscapeToken("test_token")
        .setNode(node)
        .setApplication(app)
        .setTimestamp(1590231993321L)
        .setPackage$(package$)
        .setClass$(class$)
        .setMethod(method)
        .setHashCode("1234")
        .build();

    this.repository.add(toAdd);

    // Find
    final List<LandscapeRecord> rec = this.repository.getAll(toAdd.getLandscapeToken());
    Assertions.assertEquals(1, rec.size());
    Assertions.assertEquals(toAdd, rec.get(0));
  }

  @Test
  void addNewAsync() throws IOException, QueryException {
    final List<LandscapeRecord> records = SampleLoader.loadSampleApplication();

    for (final LandscapeRecord r : records) {
      final InsertLandscapeRecord s = new InsertLandscapeRecord(r, this.mapper);
      this.sess.execute(s.toQuery());
    }

    final Node node = new Node("0.0.0.0", "localhost");
    final Application app = new Application("SampleApplication", "1234", "java");
    final String package$ = "net.explorviz.test";
    final String class$ = "SampleClass";
    final String method = "sampleMethod()";
    final LandscapeRecord toAdd = LandscapeRecord.newBuilder()
        .setLandscapeToken("test_token")
        .setNode(node)
        .setApplication(app)
        .setTimestamp(1590231993321L)
        .setPackage$(package$)
        .setClass$(class$)
        .setMethod(method)
        .setHashCode("1234")
        .build();

    this.repository.addAsync(toAdd).toCompletableFuture().join();

    // Find
    final List<LandscapeRecord> rec = this.repository.getAll(toAdd.getLandscapeToken());
    Assertions.assertEquals(1, rec.size());
    Assertions.assertEquals(toAdd, rec.get(0));
  }

  @Test
  void addWithoutToken() {
    final Node node = new Node("0.0.0.0", "localhost");
    final Application app = new Application("SampleApplication", "1234", "java");
    final String package$ = "net.explorviz.test";
    final String class$ = "SampleClass";
    final String method = "sampleMethod()";
    final LandscapeRecord toAdd = LandscapeRecord.newBuilder()
        .setLandscapeToken("")
        .setNode(node)
        .setApplication(app)
        .setTimestamp(1590231993321L)
        .setPackage$(package$)
        .setClass$(class$)
        .setMethod(method)
        .setHashCode("12345")
        .build();

    Assertions.assertThrows(QueryException.class, () -> this.repository.add(toAdd));
  }

  @Test
  void addExisting() throws IOException, QueryException {
    final List<LandscapeRecord> records = SampleLoader.loadSampleApplication();
    for (final LandscapeRecord r : records) {
      final InsertLandscapeRecord s = new InsertLandscapeRecord(r, this.mapper);
      this.sess.execute(s.toQuery());
    }

    final String token = records.get(0).getLandscapeToken();

    // Should not add another record with same specs
    this.repository.add(records.get(0));
    final int got = this.repository.getAll(token).size();
    final long want = records.stream().filter(r -> token.equals(r.getLandscapeToken())).count();
    Assertions.assertEquals(want, got);
  }


  @Test
  void deleteById() throws IOException, QueryException {

    final List<LandscapeRecord> records = SampleLoader.loadSampleApplication();
    final String token = records.get(0).getLandscapeToken();
    for (final LandscapeRecord r : records) {
      final InsertLandscapeRecord s = new InsertLandscapeRecord(r, this.mapper);
      this.sess.execute(s.toQuery());
    }
    this.repository.deleteAll(token);

    Assertions.assertEquals(0, this.repository.getAll(token).size());

  }

  @Test
  void deleteByUnknownId() throws IOException, QueryException {

    final List<LandscapeRecord> records = SampleLoader.loadSampleApplication();
    final String token = "unknown";
    for (final LandscapeRecord r : records) {
      final InsertLandscapeRecord s = new InsertLandscapeRecord(r, this.mapper);
      this.sess.execute(s.toQuery());
    }
    this.repository.deleteAll(token);
  }
}
