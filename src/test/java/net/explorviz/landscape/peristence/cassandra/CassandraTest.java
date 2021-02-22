package net.explorviz.landscape.peristence.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.quarkus.runtime.api.session.QuarkusCqlSession;
import java.io.IOException;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

/**
 * Base class for all test using an in-memory cassandra database.
 */
public class CassandraTest {

  protected DbHelper db;
  protected QuarkusCqlSession sess;



  @BeforeAll
  static void beforeAll() throws IOException, InterruptedException {
    EmbeddedCassandraServerHelper.startEmbeddedCassandra();
  }

  @BeforeEach
  void setUpDb() {
    this.sess = (QuarkusCqlSession) EmbeddedCassandraServerHelper.getSession();

    this.db = new DbHelper(this.sess);
  }

  @AfterEach
  void tearDown() {
    EmbeddedCassandraServerHelper.cleanDataEmbeddedCassandra(DbHelper.KEYSPACE_NAME);
  }



}
