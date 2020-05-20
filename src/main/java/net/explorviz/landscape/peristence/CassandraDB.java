package net.explorviz.landscape.peristence;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import com.datastax.oss.driver.api.querybuilder.schema.CreateKeyspace;
import com.datastax.oss.driver.api.querybuilder.schema.CreateTable;
import com.datastax.oss.driver.api.querybuilder.schema.CreateType;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.explorviz.landscape.LandscapeRecord;

/**
 * Wrapper class for accessing the Cassandra database.
 * Next to providing access to the CqlSession, this class contain utility methods to initialize
 * the database.
 * This database's state by default is uninitialized. To create the necessary keyspace and tables,
 * call
 * {@link #initialize()} prior to using it.
 */
@Singleton
public class CassandraDB {

  public static final String KEYSPACE_NAME = "explorviz";
  public static final String RECORDS_TABLE_NAME = "records";

  private CqlSession dbSession;

  /**
   * @param session
   */
  @Inject
  public CassandraDB(CqlSession session) {
    this.dbSession = session;
  }

  public CqlSession getDbSession() {
    return dbSession;
  }

  /**
   * Initializes the database by creating necessary schemata.
   * This is a no-op if the database is already initalized;
   */
  public void initialize() {
    createKeySpace();
    createLandscapeRecordTable();
  }

  /**
   * Creates a keyspace name "explorviz".
   * No-op if this keyspace already exists.
   */
  private void createKeySpace() {
    CreateKeyspace createKs = SchemaBuilder
        .createKeyspace(KEYSPACE_NAME)
        .ifNotExists()
        .withSimpleStrategy(1)
        .withDurableWrites(true);
    dbSession.execute(createKs.build());
  }

  /**
   * Creates the table "records" which holds all {@link LandscapeRecord} objects.
   * No-op if this table already exists.
   */
  private void createLandscapeRecordTable() {

    final String hostUDT = "host";
    final String applicationUDT = "application";

    CreateType createHostUdt = SchemaBuilder
        .createType(KEYSPACE_NAME, hostUDT)
        .ifNotExists()
        .withField("name", DataTypes.TEXT)
        .withField("ip_address", DataTypes.TEXT);
    CreateType createApplicationUdt = SchemaBuilder
        .createType(KEYSPACE_NAME, applicationUDT)
        .ifNotExists()
        .withField("name", DataTypes.TEXT)
        .withField("language", DataTypes.TEXT);

    CreateTable createTable = SchemaBuilder
        .createTable(KEYSPACE_NAME, RECORDS_TABLE_NAME)
        .ifNotExists()
        .withPartitionKey("id", DataTypes.TEXT)
        .withColumn(hostUDT, SchemaBuilder.udt(hostUDT, true))
        .withColumn(applicationUDT, SchemaBuilder.udt(applicationUDT, true))
        .withColumn("package", DataTypes.TEXT)
        .withColumn("class", DataTypes.TEXT)
        .withColumn("method", DataTypes.TEXT);

    dbSession.execute(createHostUdt.asCql());
    dbSession.execute(createApplicationUdt.asCql());
    dbSession.execute(createTable.asCql());
  }

}
