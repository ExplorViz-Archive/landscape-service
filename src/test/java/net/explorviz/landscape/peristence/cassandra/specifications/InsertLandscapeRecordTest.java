package net.explorviz.landscape.peristence.cassandra.specifications;

import static org.junit.jupiter.api.Assertions.*;

import net.explorviz.landscape.Application;
import net.explorviz.landscape.LandscapeRecord;
import net.explorviz.landscape.Node;
import net.explorviz.landscape.peristence.QueryException;
import net.explorviz.landscape.peristence.cassandra.CassandraDBTest;
import net.explorviz.landscape.peristence.cassandra.mapper.LandscapeRecordMapper;
import net.explorviz.landscape.peristence.cassandra.mapper.ValueMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InsertLandscapeRecordTest extends CassandraDBTest {

  private LandscapeRecord sampleRecord;

  @BeforeEach
  void setUp() {
    db.initialize();
    // Setup a sample LandscapeRecord object to test with
    Node node = new Node("0.0.0.0", "localhost");
    Application app = new Application("SampleApplication", "java");
    String package$ = "net.explorviz.test";
    String class$ = "SampleClass";
    String method = "sampleMethod()";
    sampleRecord = LandscapeRecord.newBuilder()
        .setId("ID")
        .setNode(node)
        .setApplication(app)
        .setPackage$(package$)
        .setClass$(class$)
        .setMethod(method)
        .build();
  }

  @Test
  void toQuery() throws QueryException {
    ValueMapper<LandscapeRecord> mapper = new LandscapeRecordMapper(this.db);
    InsertLandscapeRecord ilr = new InsertLandscapeRecord(sampleRecord, mapper);
    String query = ilr.toQuery();
    String expected =
        "INSERT INTO explorviz.records (node,package,application,method,id,class) VALUES ({name:'localhost',ip_address:'0.0.0.0'},'net.explorviz.test',{name:'SampleApplication',language:'java'},'sampleMethod()','ID','SampleClass')";
    Assertions.assertEquals(expected, query);

  }
}
