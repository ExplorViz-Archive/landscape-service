package net.explorviz.landscape.service.converter;

import net.explorviz.avro.landscape.flat.Application;
import net.explorviz.avro.landscape.flat.LandscapeRecord;
import net.explorviz.avro.landscape.flat.Node;
import net.explorviz.landscape.peristence.model.SpanStructure;
import net.explorviz.landscape.utils.testhelper.SpanStructureHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SpanToRecordConverterTest {

  private SpanToRecordConverter converter;

  private SpanStructure span;
  private LandscapeRecord record;

  @BeforeEach
  void setUp() {
    this.converter = new SpanToRecordConverter();


    this.span = SpanStructureHelper.randomSpanStructure();

    String pkg = "foo.bar";
    String cls = "Test";
    String mthd = "test";
    this.span.setFullyQualifiedOperationName(String.join(".", pkg, cls, mthd));

    this.record = LandscapeRecord.newBuilder()
        .setLandscapeToken(span.getLandscapeToken())
        .setHashCode(span.getHashCode())
        .setTimestamp(span.getTimestamp())
        .setNode(new Node(span.getHostIpAddress(), span.getHostName()))
        .setApplication(new Application(span.getApplicationName(), span.getInstanceId(),
            span.getApplicationLanguage()))
        .setPackage$(pkg)
        .setClass$(cls)
        .setMethod(mthd)
        .setHashCode(span.getHashCode())
        .build();

  }

  @Test
  public void convert() {
    final LandscapeRecord got = this.converter.toRecord(this.span);
    Assertions.assertEquals(got, this.record, "Converted records does not match expected");
  }
}
