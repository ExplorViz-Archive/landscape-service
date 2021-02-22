package net.explorviz.landscape.peristence.cassandra.specifications;

import net.explorviz.avro.landscape.flat.Application;
import net.explorviz.avro.landscape.flat.LandscapeRecord;
import net.explorviz.avro.landscape.flat.Node;
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
    this.db.initialize();
    // Setup a sample LandscapeRecord object to test with

  }

  @Test
  void noToken() throws QueryException {
    final Node node = new Node("0.0.0.0", "localhost");
    final Application app = new Application("SampleApplication", 1234L, "java");
    final String package$ = "net.explorviz.test";
    final String class$ = "SampleClass";
    final String method = "sampleMethod()";
    this.sampleRecord = LandscapeRecord.newBuilder()
        .setLandscapeToken("")
        .setNode(node)
        .setApplication(app)
        .setTimestamp(1590231993321L)
        .setPackage$(package$)
        .setClass$(class$)
        .setMethod(method)
        .setHashCode("12345")
        .build();
    final ValueMapper<LandscapeRecord> mapper = new LandscapeRecordMapper(this.db);
    final InsertLandscapeRecord ilr = new InsertLandscapeRecord(this.sampleRecord, mapper);

    Assertions.assertThrows(QueryException.class, ilr::toQuery);

  }

}
