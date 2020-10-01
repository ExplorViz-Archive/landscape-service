package net.explorviz.landscape.peristence.cassandra.specifications;

import net.explorviz.avro.landscape.flat.Application;
import net.explorviz.avro.landscape.flat.LandscapeRecord;
import net.explorviz.avro.landscape.flat.Node;
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
    Application app = new Application("SampleApplication", "1234", "java");
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
        .setHashCode("1234")
        .build();
    ValueMapper<LandscapeRecord> mapper = new LandscapeRecordMapper(this.db);
    InsertLandscapeRecord ilr = new InsertLandscapeRecord(sampleRecord, mapper);
    String query = ilr.toQuery();
    String expected =
        "INSERT INTO explorviz.records (node,package,hash_code,application,method,landscape_token,class,timestamp) VALUES ({name:'localhost',ip_address:'0.0.0.0'},'net.explorviz.test','1234',{name:'SampleApplication',pid:'1234',language:'java'},'sampleMethod()','tok','SampleClass',1590231993321)";
    Assertions.assertEquals(expected, query);
  }

  @Test
  void noToken() throws QueryException {
    Node node = new Node("0.0.0.0", "localhost");
    Application app = new Application("SampleApplication", "1234", "java");
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
        .setHashCode("12345")
        .build();
    ValueMapper<LandscapeRecord> mapper = new LandscapeRecordMapper(this.db);
    InsertLandscapeRecord ilr = new InsertLandscapeRecord(sampleRecord, mapper);

    Assertions.assertThrows(QueryException.class, ilr::toQuery);

  }

}
