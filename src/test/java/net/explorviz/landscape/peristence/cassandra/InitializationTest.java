package net.explorviz.landscape.peristence.cassandra;

import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import java.util.List;
import java.util.Objects;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class InitializationTest extends CassandraDBTest{

  private static final String GET_ALL_KEYSPACES = "SELECT * FROM system_schema.keyspaces";
  private static final String GET_ALL_TABLES =
      "SELECT * FROM system_schema.tables WHERE keyspace_name = '{}'";

  @Test
  public void testKeyspaceCreated() {
    db.initialize();

    final String keyspaceNameColumn = "keyspace_name";
    ResultSet keyspaces = sess.execute(GET_ALL_KEYSPACES);

    boolean hasExplorVizKeyspace =
        keyspaces.all().stream().map(r -> r.getString(keyspaceNameColumn)).filter(Objects::nonNull)
            .anyMatch(n -> n.equals(CassandraDB.KEYSPACE_NAME));
    Assertions.assertTrue(hasExplorVizKeyspace);

  }

  @Test
  public void testTableCreated() {
    db.initialize();

    ResultSet tables = sess.execute(GET_ALL_TABLES.replace("{}", CassandraDB.KEYSPACE_NAME));
    final String tableColumnName = "table_name";
    List<Row> rows = tables.all();
    Assertions.assertEquals(1, rows.size());
    Assert.assertEquals(rows.get(0).getString(tableColumnName),
        CassandraDB.RECORDS_TABLE_NAME);

  }
}
