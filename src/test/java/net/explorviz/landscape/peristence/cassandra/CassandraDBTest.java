package net.explorviz.landscape.peristence.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Base class for all test using an in-memory cassandra database.
 */
public class CassandraDBTest {

  protected CassandraDB db;
  protected CqlSession sess;




  @BeforeAll
  static void beforeAll() throws IOException, InterruptedException {
    EmbeddedCassandraServerHelper.startEmbeddedCassandra();
  }

  @BeforeEach
  void setUpDb() {
    sess = EmbeddedCassandraServerHelper.getSession();
    db = new CassandraDB(sess);
  }

  @AfterEach
  void tearDown() {
    EmbeddedCassandraServerHelper.cleanEmbeddedCassandra();
  }



}
