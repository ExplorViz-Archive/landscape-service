package net.explorviz.landscape.peristence.cassandra;

import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.core.type.codec.registry.CodecRegistry;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import com.datastax.oss.driver.api.querybuilder.schema.CreateKeyspace;
import com.datastax.oss.driver.api.querybuilder.schema.CreateTable;
import com.datastax.oss.quarkus.runtime.api.session.QuarkusCqlSession;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.explorviz.avro.landscape.flat.LandscapeRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper class for accessing the Cassandra database. Next to providing access to the CqlSession,
 * this class contain utility methods to initialize the database. This database's state by default
 * is uninitialized. To create the necessary keyspace and tables, call {@link #initialize()} prior
 * to using it.
 */
@Singleton
public class DbHelper {

  public static final String KEYSPACE_NAME = "explorviz";
  public static final String SSTRUCTURE_TABLE_NAME = "span_structure";


  public static final String COL_TOKEN = "landscape_token";
  public static final String COL_TIMESTAMP = "timestamp";
  public static final String COL_HASHCODE = "hash_code";

  public static final String COL_HOST_NAME = "host_name"; // NOCS
  public static final String COL_HOST_IP = "host_ip_address";

  public static final String COL_APP_NAME = "application_name"; // NOCS
  public static final String COL_APP_LANGUAGE = "application_language";
  public static final String COL_APP_INSTANCE_ID = "instance_id";

  public static final String COL_FQN = "method_fqn";



  private static final Logger LOGGER = LoggerFactory.getLogger(DbHelper.class);

  private final QuarkusCqlSession dbSession;

  @Inject
  public DbHelper(final QuarkusCqlSession session) {
    this.dbSession = session;
  }

  public QuarkusCqlSession getSession() {
    return this.dbSession;
  }

  /**
   * Initializes the database by creating necessary schemata. This is a no-op if the database is
   * already initalized;
   */
  public void initialize() {
    this.createKeySpace();
    this.createLandscapeRecordTable();
  }

  /**
   * Creates a keyspace name "explorviz". No-op if this keyspace already exists.
   */
  private void createKeySpace() {
    final CreateKeyspace createKs = SchemaBuilder
        .createKeyspace(KEYSPACE_NAME)
        .ifNotExists()
        .withSimpleStrategy(1)
        .withDurableWrites(true);
    this.dbSession.execute(createKs.build());
  }

  public CodecRegistry getCodecRegistry() {
    return this.dbSession.getContext().getCodecRegistry();
  }

  /**
   * Creates the table "records" which holds all {@link LandscapeRecord} objects. No-op if this
   * table already exists.
   */
  private void createLandscapeRecordTable() {


    final CreateTable createTable = SchemaBuilder
        .createTable(KEYSPACE_NAME, SSTRUCTURE_TABLE_NAME)
        .ifNotExists()
        .withPartitionKey(COL_TOKEN, DataTypes.TEXT)
        .withClusteringColumn(COL_TIMESTAMP, DataTypes.BIGINT)
        .withClusteringColumn(COL_HASHCODE, DataTypes.TEXT)
        .withColumn(COL_HOST_NAME, DataTypes.TEXT)
        .withColumn(COL_HOST_IP, DataTypes.TEXT)
        .withColumn(COL_APP_NAME, DataTypes.TEXT)
        .withColumn(COL_APP_INSTANCE_ID, DataTypes.TEXT)
        .withColumn(COL_APP_LANGUAGE, DataTypes.TEXT)
        .withColumn(COL_FQN, DataTypes.TEXT);


    this.dbSession.execute(createTable.asCql());


    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("Created spans structure table");
    }

  }


}
