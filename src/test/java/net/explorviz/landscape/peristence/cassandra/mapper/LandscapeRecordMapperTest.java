package net.explorviz.landscape.peristence.cassandra.mapper;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.querybuilder.term.Term;
import com.datastax.oss.driver.internal.querybuilder.DefaultLiteral;
import java.util.Map;
import net.explorviz.avro.landscape.flat.Application;
import net.explorviz.avro.landscape.flat.LandscapeRecord;
import net.explorviz.avro.landscape.flat.Node;
import net.explorviz.landscape.peristence.cassandra.CassandraTest;
import net.explorviz.landscape.peristence.cassandra.DbHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

class LandscapeRecordMapperTest extends CassandraTest {

  private LandscapeRecord sampleRecord;

  private LandscapeRecordMapper mapper;

  @Mock
  private Row mockRow;

  @BeforeEach
  void setUp() {
    this.db.initialize();
    // Setup a sample LandscapeRecord object to test with
    final Node node = new Node("0.0.0.0", "localhost");
    final Application app = new Application("SampleApplication", 1234L, "java");
    final String package$ = "net.explorviz.test";
    final String class$ = "SampleClass";
    final String method = "sampleMethod()";
    this.sampleRecord = LandscapeRecord.newBuilder()
        .setLandscapeToken("tok")
        .setTimestamp(System.currentTimeMillis())
        .setNode(node)
        .setApplication(app)
        .setPackage$(package$)
        .setClass$(class$)
        .setMethod(method)
        .setHashCode("12345")
        .build();

    this.mapper = new LandscapeRecordMapper(this.db);
  }

  @Test
  void toMap() {
    final Map<String, Term> map = this.mapper.toMap(this.sampleRecord);

    final DefaultLiteral<Long> timestamp = (DefaultLiteral<Long>) map.get(DbHelper.COL_TIMESTAMP);
    final DefaultLiteral<String> token = (DefaultLiteral<String>) map.get(DbHelper.COL_TOKEN);
    final DefaultLiteral<String> methodName = (DefaultLiteral<String>) map.get(DbHelper.COL_METHOD);

    final DefaultLiteral<String> packageName =
        (DefaultLiteral<String>) map.get(DbHelper.COL_PACKAGE);
    final DefaultLiteral<String> className = (DefaultLiteral<String>) map.get(DbHelper.COL_CLASS);
    final DefaultLiteral<Node> node = (DefaultLiteral<Node>) map.get(DbHelper.COL_NODE);
    final DefaultLiteral<String> hashCode = (DefaultLiteral<String>) map.get(DbHelper.COL_HASHCODE);
    final DefaultLiteral<Application> application =
        (DefaultLiteral<Application>) map.get(DbHelper.COL_APPLICATION);


    Assertions.assertEquals(this.sampleRecord.getLandscapeToken(), token.getValue());
    Assertions.assertEquals(this.sampleRecord.getTimestamp(), timestamp.getValue());
    Assertions.assertEquals(this.sampleRecord.getPackage$(), packageName.getValue());
    Assertions.assertEquals(this.sampleRecord.getMethod(), methodName.getValue());
    Assertions.assertEquals(this.sampleRecord.getClass$(), className.getValue());
    Assertions.assertEquals(this.sampleRecord.getNode(), node.getValue());
    Assertions.assertEquals(this.sampleRecord.getHashCode(), hashCode.getValue());
    Assertions.assertEquals(this.sampleRecord.getApplication(), application.getValue());
  }


  @Test
  void fromRow() {
    // Tests the conversion but not the codecs...
    this.mockRow = Mockito.mock(Row.class);
    Mockito.when(this.mockRow.getString(DbHelper.COL_TOKEN))
        .thenReturn(this.sampleRecord.getLandscapeToken());
    Mockito.when(this.mockRow.getLong(DbHelper.COL_TIMESTAMP))
        .thenReturn(this.sampleRecord.getTimestamp());
    Mockito.when(this.mockRow.getString(DbHelper.COL_METHOD))
        .thenReturn(this.sampleRecord.getMethod());
    Mockito.when(this.mockRow.getString(DbHelper.COL_PACKAGE))
        .thenReturn(this.sampleRecord.getPackage$());
    Mockito.when(this.mockRow.getString(DbHelper.COL_CLASS))
        .thenReturn(this.sampleRecord.getClass$());
    Mockito.when(this.mockRow.get(DbHelper.COL_NODE, Node.class))
        .thenReturn(this.sampleRecord.getNode());
    Mockito.when(this.mockRow.getString(DbHelper.COL_HASHCODE))
        .thenReturn(this.sampleRecord.getHashCode());
    Mockito.when(this.mockRow.get(DbHelper.COL_APPLICATION, Application.class))
        .thenReturn(this.sampleRecord.getApplication());


    final LandscapeRecord got = this.mapper.fromRow(this.mockRow);
    Assertions.assertEquals(this.sampleRecord, got);

  }

}
