package net.explorviz.landscape.peristence.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.core.type.codec.registry.CodecRegistry;
import com.datastax.oss.driver.api.core.type.codec.registry.MutableCodecRegistry;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import com.datastax.oss.driver.api.querybuilder.schema.CreateIndex;
import com.datastax.oss.driver.api.querybuilder.schema.CreateKeyspace;
import com.datastax.oss.driver.api.querybuilder.schema.CreateTable;
import com.datastax.oss.driver.api.querybuilder.schema.CreateType;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.explorviz.landscape.flat.LandscapeRecord;
import net.explorviz.landscape.peristence.cassandra.mapper.ApplicationCodec;
import net.explorviz.landscape.peristence.cassandra.mapper.NodeCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper class for accessing the Cassandra database.
 * Next to providing access to the CqlSession, this class contain utility methods to initialize
 * the database.
 * This database's state by default is uninitialized. To create the necessary keyspace and tables,
 * call
 * {@link #initialize()} prior to using it.
 */
@Singleton
public class DBHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(DBHelper.class);

  public static final String KEYSPACE_NAME = "explorviz";
  public static final String RECORDS_TABLE_NAME = "records";

  public static final String COL_NODE_NAME = "name";
  public static final String COL_NODE_IP_ADDRESS = "ip_address";

  public static final String COL_APP_NAME = "name";
  public static final String COL_APP_LANGUAGE = "language";
  public static final String COL_APP_PID = "pid";


  public static final String COL_TIMESTAMP = "timestamp";
  public static final String COL_TOKEN = "landscape_token";
  public static final String COL_PACKAGE = "package";
  public static final String COL_CLASS = "class";
  public static final String COL_METHOD = "method";
  public static final String COL_HASHCODE = "hash_code";
  public static final String COL_NODE = "node";
  public static final String COL_APPLICATION = "application";


  private final CqlSession dbSession;

  /**
   * @param session the CqlSession
   */
  @Inject
  public DBHelper(CqlSession session) {
    this.dbSession = session;
  }

  public CqlSession getSession() {
    return dbSession;
  }

  /**
   * Initializes the database by creating necessary schemata.
   * This is a no-op if the database is already initalized;
   */
  public void initialize() {
    createKeySpace();
    createLandscapeRecordTable();
    registerCodecs();
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

  public CodecRegistry getCodecRegistry() {
    return dbSession.getContext().getCodecRegistry();
  }

  /**
   * Creates the table "records" which holds all {@link LandscapeRecord} objects.
   * No-op if this table already exists.
   */
  private void createLandscapeRecordTable() {

    CreateType createNodeUdt = SchemaBuilder
        .createType(KEYSPACE_NAME, COL_NODE)
        .ifNotExists()
        .withField(COL_NODE_NAME, DataTypes.TEXT)
        .withField(COL_NODE_IP_ADDRESS, DataTypes.TEXT);

    CreateType createApplicationUdt = SchemaBuilder
        .createType(KEYSPACE_NAME, COL_APPLICATION)
        .ifNotExists()
        .withField(COL_APP_NAME, DataTypes.TEXT)
        .withField(COL_APP_PID, DataTypes.TEXT)
        .withField(COL_APP_LANGUAGE, DataTypes.TEXT);

    CreateTable createTable = SchemaBuilder
        .createTable(KEYSPACE_NAME, RECORDS_TABLE_NAME)
        .ifNotExists()
        .withPartitionKey(COL_TOKEN, DataTypes.TEXT)
        .withClusteringColumn(COL_NODE, SchemaBuilder.udt(COL_NODE, true))
        .withClusteringColumn(COL_APPLICATION, SchemaBuilder.udt(COL_APPLICATION, true))
        .withClusteringColumn(COL_PACKAGE, DataTypes.TEXT)
        .withClusteringColumn(COL_CLASS, DataTypes.TEXT)
        .withClusteringColumn(COL_METHOD, DataTypes.TEXT)
        .withClusteringColumn(COL_HASHCODE, DataTypes.TEXT)
        .withColumn(COL_TIMESTAMP, DataTypes.BIGINT);


    // Create index on timestamps for efficient querying
    CreateIndex createTSIndex = SchemaBuilder.createIndex("timestamp_index")
        .ifNotExists()
        .onTable(KEYSPACE_NAME, RECORDS_TABLE_NAME)
        .andColumn(COL_TIMESTAMP);


    dbSession.execute(createNodeUdt.asCql());
    dbSession.execute(createApplicationUdt.asCql());
    dbSession.execute(createTable.asCql());
    dbSession.execute(createTSIndex.asCql());

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("Created records table and associated types");
    }

  }

  private void registerCodecs() {
    CodecRegistry codecRegistry = getCodecRegistry();

    // Register Node coded
    UserDefinedType nodeUdt =
        dbSession.getMetadata().getKeyspace(KEYSPACE_NAME)
            .flatMap(ks -> ks.getUserDefinedType(COL_NODE))
            .orElseThrow(IllegalStateException::new);
    TypeCodec<UdtValue> nodeUdtCodec = codecRegistry.codecFor(nodeUdt);
    NodeCodec nodeCodec = new NodeCodec(nodeUdtCodec);
    ((MutableCodecRegistry) codecRegistry).register(nodeCodec);

    // Register Application codec
    UserDefinedType applicationUdt = dbSession.getMetadata().getKeyspace(KEYSPACE_NAME)
        .flatMap(ks -> ks.getUserDefinedType(COL_APPLICATION))
        .orElseThrow(IllegalStateException::new);
    TypeCodec<UdtValue> appUdtCodec = codecRegistry.codecFor(applicationUdt);
    ApplicationCodec applicationCodec = new ApplicationCodec(appUdtCodec);
    ((MutableCodecRegistry) codecRegistry).register(applicationCodec);

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("Registered codecs");
    }
  }

}
