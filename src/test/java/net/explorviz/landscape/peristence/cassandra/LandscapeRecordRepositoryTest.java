package net.explorviz.landscape.peristence.cassandra;

import static org.junit.jupiter.api.Assertions.*;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import java.io.IOException;
import java.util.List;
import net.explorviz.landscape.LandscapeRecord;
import net.explorviz.landscape.helper.SampleLoader;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link LandscapeRecordRepository}. The test are run against an in-memory
 * Cassandra databse.
 */
class LandscapeRecordRepositoryTest {

  private CassandraDB db;
  private CqlSession sess;

  @BeforeAll
  static void beforeAll() throws IOException, InterruptedException {
    // Start emebedded cassandra instance for testing
    EmbeddedCassandraServerHelper.startEmbeddedCassandra();
  }

  @BeforeEach
  void setUp() {
    sess = EmbeddedCassandraServerHelper.getSession();
    db = new CassandraDB(sess);

  }

  @AfterEach
  void tearDown() {
    // Clean the cassandra server after each test
    EmbeddedCassandraServerHelper.cleanEmbeddedCassandra();
  }

  private void fillSampleData() throws IOException {
    List<LandscapeRecord> record = SampleLoader.load();
    //sess.execute()
  }

  @Test
  void getAll() {
  }

  @Test
  void add() {
  }

  @Test
  void remove() {
  }

  @Test
  void query() {
  }
}
