package net.explorviz.landscape.service.converter;

import net.explorviz.avro.landscape.flat.Application;
import net.explorviz.avro.landscape.flat.LandscapeRecord;
import net.explorviz.avro.landscape.flat.Node;
import net.explorviz.landscape.persistence.model.SpanStructure;
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

    final String pkg = "foo.bar";
    final String cls = "Test";
    final String mthd = "test";
    this.span.setFullyQualifiedOperationName(String.join(".", pkg, cls, mthd));

    this.record = LandscapeRecord.newBuilder().setLandscapeToken(this.span.getLandscapeToken())
        .setHashCode(this.span.getHashCode()).setTimestamp(this.span.getTimestamp())
        .setNode(new Node(this.span.getHostIpAddress(), this.span.getHostName())).setApplication(
            new Application(this.span.getApplicationName(), this.span.getInstanceId(),
                this.span.getApplicationLanguage())).setPackage$(pkg).setClass$(cls).setMethod(mthd)
        .setHashCode(this.span.getHashCode()).build();

  }

  @Test
  public void convert() {
    final LandscapeRecord got = this.converter.toRecord(this.span);
    Assertions.assertEquals(got, this.record, "Converted records does not match expected");
  }
}
