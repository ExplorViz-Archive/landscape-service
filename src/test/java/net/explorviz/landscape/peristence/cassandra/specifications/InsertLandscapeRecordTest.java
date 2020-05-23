package net.explorviz.landscape.peristence.cassandra.specifications;

import net.explorviz.landscape.Application;
import net.explorviz.landscape.LandscapeRecord;
import net.explorviz.landscape.Node;
import net.explorviz.landscape.peristence.QueryException;
import net.explorviz.landscape.peristence.cassandra.CassandraTest;
import net.explorviz.landscape.peristence.cassandra.mapper.LandscapeRecordMapper;
import net.explorviz.landscape.peristence.cassandra.mapper.ValueMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InsertLandscapeRecordTest extends CassandraTest {

  private LandscapeRecord sampleRecord;

  @BeforeEach
  void setUp() {
    db.initialize();
    // Setup a sample LandscapeRecord object to test with

  }

  @Test
  void valid() throws QueryException {
    Node node = new Node("0.0.0.0", "localhost");
    Application app = new Application("SampleApplication", "java");
    String package$ = "net.explorviz.test";
    String class$ = "SampleClass";
    String method = "sampleMethod()";
    sampleRecord = LandscapeRecord.newBuilder()
        .setLandscapeToken("tok")
        .setTimestamp(1590231993321L)
        .setNode(node)
        .setApplication(app)
        .setPackage$(package$)
        .setClass$(class$)
        .setMethod(method)
        .build();
    ValueMapper<LandscapeRecord> mapper = new LandscapeRecordMapper(this.db);
    InsertLandscapeRecord ilr = new InsertLandscapeRecord(sampleRecord, mapper);
    String query = ilr.toQuery();
    String expected =
        "INSERT INTO explorviz.records (node,package,application,method,landscape_token,class,timestamp) VALUES ({name:'localhost',ip_address:'0.0.0.0'},'net.explorviz.test',{name:'SampleApplication',language:'java'},'sampleMethod()','tok','SampleClass',1590231993321)";
    Assertions.assertEquals(expected, query);
  }

  @Test
  void noToken() throws QueryException {
    Node node = new Node("0.0.0.0", "localhost");
    Application app = new Application("SampleApplication", "java");
    String package$ = "net.explorviz.test";
    String class$ = "SampleClass";
    String method = "sampleMethod()";
    sampleRecord = LandscapeRecord.newBuilder()
        .setLandscapeToken("")
        .setNode(node)
        .setApplication(app)
        .setTimestamp(1590231993321L)
        .setPackage$(package$)
        .setClass$(class$)
        .setMethod(method)
        .build();
    ValueMapper<LandscapeRecord> mapper = new LandscapeRecordMapper(this.db);
    InsertLandscapeRecord ilr = new InsertLandscapeRecord(sampleRecord, mapper);

    Assertions.assertThrows(QueryException.class, ilr::toQuery);

  }

}
