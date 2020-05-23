package net.explorviz.landscape.peristence.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import java.io.IOException;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

/**
 * Base class for all test using an in-memory cassandra database.
 */
public class CassandraTest {

  protected DBHelper db;
  protected CqlSession sess;




  @BeforeAll
  static void beforeAll() throws IOException, InterruptedException {
    EmbeddedCassandraServerHelper.startEmbeddedCassandra();
  }

  @BeforeEach
  void setUpDb() {
    sess = EmbeddedCassandraServerHelper.getSession();
    db = new DBHelper(sess);
  }

  @AfterEach
  void tearDown() {
      EmbeddedCassandraServerHelper.cleanDataEmbeddedCassandra(DBHelper.KEYSPACE_NAME);
  }



}
