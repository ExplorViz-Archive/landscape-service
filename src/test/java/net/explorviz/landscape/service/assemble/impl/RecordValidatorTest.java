package net.explorviz.landscape.service.assemble.impl;



import net.explorviz.avro.landscape.flat.Application;
import net.explorviz.avro.landscape.flat.LandscapeRecord;
import net.explorviz.avro.landscape.flat.Node;
import net.explorviz.avro.landscape.model.Method;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RecordValidatorTest {

  private LandscapeRecord validRecord;
  private RecordValidator validator;

  @BeforeEach
  void setUp() {
    final Node n = new Node("0.0.0.0", "localhost");
    final Application app = new Application("sample app", "1234", "java");
    final String pkg = "foo.bar";
    final String clazz = "Foo";
    final Method method = new Method("method", "1234");
    this.validRecord =
        new LandscapeRecord("tok", System.currentTimeMillis(), n, app, pkg, clazz, method.getName(),
            method.getHashCode());

    this.validator = new RecordValidator();
  }

  @Test
  void valid() {
    Assertions.assertDoesNotThrow(() -> this.validator.validate(this.validRecord));
  }

  @Test
  void noToken() {
    final LandscapeRecord nullToken =
        LandscapeRecord.newBuilder(this.validRecord).build();
    nullToken.setLandscapeToken(null);

    final LandscapeRecord emptyToken =
        LandscapeRecord.newBuilder(this.validRecord).setLandscapeToken("").build();

    for (final LandscapeRecord r : new LandscapeRecord[] {nullToken, emptyToken}) {
      Assertions.assertThrows(InvalidRecordException.class, () -> this.validator.validate(r));
    }

  }

  @Test
  void invalidNode() {
    final Node.Builder nodeBuilder = Node.newBuilder(this.validRecord.getNode());

    final LandscapeRecord nullNode = LandscapeRecord.newBuilder(this.validRecord).build();
    nullNode.setNode(null);

    final LandscapeRecord emptyHostName =
        LandscapeRecord.newBuilder(this.validRecord).setNode(nodeBuilder.setHostName("").build())
            .build();

    final LandscapeRecord emptyIpAddress =
        LandscapeRecord.newBuilder(this.validRecord).setNode(nodeBuilder.setIpAddress("").build())
            .build();

    for (final LandscapeRecord r : new LandscapeRecord[] {nullNode, emptyHostName,
        emptyIpAddress}) {
      Assertions.assertThrows(InvalidRecordException.class, () -> this.validator.validate(r));
    }
  }

  @Test
  void invalidApp() {
    final Application.Builder appBuilder =
        Application.newBuilder(this.validRecord.getApplication());
    final LandscapeRecord nullApp =
        LandscapeRecord.newBuilder(this.validRecord).build();
    nullApp.setApplication(null);


    final LandscapeRecord emptyAppName =
        LandscapeRecord.newBuilder(this.validRecord).setApplication(appBuilder.setName("").build())
            .build();

    final LandscapeRecord emptyLanguage =
        LandscapeRecord.newBuilder(this.validRecord)
            .setApplication(appBuilder.setLanguage("").build())
            .build();

    for (final LandscapeRecord r : new LandscapeRecord[] {nullApp, emptyAppName, emptyLanguage}) {
      Assertions.assertThrows(InvalidRecordException.class, () -> this.validator.validate(r));
    }
  }

  @Test
  void invalidPackage() {
    final LandscapeRecord emptyPkg =
        LandscapeRecord.newBuilder(this.validRecord).setPackage$("").build();
    for (final LandscapeRecord r : new LandscapeRecord[] {emptyPkg}) {
      Assertions.assertThrows(InvalidRecordException.class, () -> this.validator.validate(r));
    }
  }

  @Test
  void invalidClazz() {
    final LandscapeRecord emptyCls =
        LandscapeRecord.newBuilder(this.validRecord).setClass$("").build();
    for (final LandscapeRecord r : new LandscapeRecord[] {emptyCls}) {
      Assertions.assertThrows(InvalidRecordException.class, () -> this.validator.validate(r));
    }
  }



}
