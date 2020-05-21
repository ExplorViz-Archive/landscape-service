package net.explorviz.landscape.peristence.cassandra.mapper;

import com.datastax.oss.driver.api.querybuilder.Literal;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.term.Term;
import com.datastax.oss.driver.internal.querybuilder.DefaultLiteral;
import java.util.Map;
import net.explorviz.landscape.Application;
import net.explorviz.landscape.LandscapeRecord;
import net.explorviz.landscape.Node;
import net.explorviz.landscape.peristence.cassandra.CassandraDB;
import net.explorviz.landscape.peristence.cassandra.CassandraDBTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LandscapeRecordMapperTest extends CassandraDBTest {

  private LandscapeRecord sampleRecord;
  LandscapeRecordMapper mapper;

  @BeforeEach
  void setUp() {
    db.initialize();
    // Setup a sample LandscapeRecord object to test with
    Node node = new Node("0.0.0.0", "localhost");
    Application app = new Application("SampleApplication", "java");
    String package$ = "net.explorviz.test";
    String class$ = "SampleClass";
    String method ="sampleMethod()";
    sampleRecord = LandscapeRecord.newBuilder()
        .setId("ID")
        .setNode(node)
        .setApplication(app)
        .setPackage$(package$)
        .setClass$(class$)
        .setMethod(method)
        .build();

    mapper = new LandscapeRecordMapper(this.db);
  }

  @Test
  void toMap() {
    Map<String, Term> map = mapper.toMap(sampleRecord);
    DefaultLiteral<String> methodName = (DefaultLiteral<String>) map.get(CassandraDB.COL_METHOD);
    DefaultLiteral<String> packageName = (DefaultLiteral<String>) map.get(CassandraDB.COL_PACKAGE);
    DefaultLiteral<String> className = (DefaultLiteral<String>) map.get(CassandraDB.COL_CLASS);
    DefaultLiteral<Node> node = (DefaultLiteral<Node>) map.get(CassandraDB.COL_NODE);
    DefaultLiteral<Application> application = (DefaultLiteral<Application>) map.get(CassandraDB.COL_APPLICATION);
    DefaultLiteral<String> id = (DefaultLiteral<String>) map.get(CassandraDB.COL_ID);

    Assertions.assertEquals(sampleRecord.getId(), id.getValue());
    Assertions.assertEquals(sampleRecord.getPackage$(), packageName.getValue());
    Assertions.assertEquals(sampleRecord.getMethod(), methodName.getValue());
    Assertions.assertEquals(sampleRecord.getClass$(), className.getValue());
    Assertions.assertEquals(sampleRecord.getNode(), node.getValue());
    Assertions.assertEquals(sampleRecord.getApplication(), application.getValue());
  }
}
