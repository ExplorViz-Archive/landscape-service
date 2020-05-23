package net.explorviz.landscape.peristence.cassandra;

import com.datastax.oss.driver.api.core.cql.ResultSet;
import io.quarkus.test.junit.QuarkusTest;
import java.io.IOException;
import java.util.List;
import net.explorviz.landscape.LandscapeRecord;
import net.explorviz.landscape.helper.SampleLoader;
import net.explorviz.landscape.peristence.QueryException;
import net.explorviz.landscape.peristence.cassandra.mapper.LandscapeRecordMapper;
import net.explorviz.landscape.peristence.cassandra.specifications.InsertLandscapeRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link LandscapeRecordRepository}. The test are run against an in-memory
 * Cassandra databse.
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

  private int fillSampleData() throws IOException {
    List<LandscapeRecord> record = SampleLoader.load();
    for (LandscapeRecord r : record) {
      InsertLandscapeRecord s = new InsertLandscapeRecord(r, mapper);
      try {
        sess.execute(s.toQuery());
      } catch (QueryException e) {
        e.printStackTrace();
      }
    }
    return record.size();
  }

  @Test
  void getAll() throws IOException {
    int inserted = fillSampleData();
    List<LandscapeRecord> got = repository.getAll();
    Assertions.assertEquals(inserted, got.size());
  }

  @Test
  void add() {
  }


  @Test
  void query() {
  }

  @Test
  void testGetAll() {
  }

  @Test
  void testAdd() {
  }

  @Test
  void testQuery() {
  }
}
