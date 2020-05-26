package net.explorviz.landscape.service.impl;

import net.explorviz.landscape.Application;
import net.explorviz.landscape.LandscapeRecord;
import net.explorviz.landscape.Node;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RecordValidatorTest {

  private LandscapeRecord validRecord;
  private RecordValidator validator;

  @BeforeEach
  void setUp() {
    Node n = new Node("0.0.0.0", "localhost");
    Application app = new Application("sample app", "1234", "java");
    String pkg = "foo.bar";
    String clazz = "Foo";
    String method = "bar";
    validRecord =
        new LandscapeRecord("tok", System.currentTimeMillis(), n, app, pkg, clazz, method);

    validator = new RecordValidator();
  }

  @Test
  void valid() {
    Assertions.assertDoesNotThrow(() -> validator.validate(validRecord));
  }

  @Test
  void noToken() {
    LandscapeRecord nullToken =
        LandscapeRecord.newBuilder(validRecord).build();
    nullToken.setLandscapeToken(null);

    LandscapeRecord emptyToken =
        LandscapeRecord.newBuilder(validRecord).setLandscapeToken("").build();

    for (LandscapeRecord r : new LandscapeRecord[] {nullToken, emptyToken}) {
      Assertions.assertThrows(InvalidRecordException.class, () -> validator.validate(r));
    }

  }

  @Test
  void invalidNode() {
    Node.Builder nodeBuilder = Node.newBuilder(validRecord.getNode());

    LandscapeRecord nullNode = LandscapeRecord.newBuilder(validRecord).build();
    nullNode.node = null;

    LandscapeRecord emptyHostName =
        LandscapeRecord.newBuilder(validRecord).setNode(nodeBuilder.setHostName("").build())
            .build();

    LandscapeRecord emptyIpAddress =
        LandscapeRecord.newBuilder(validRecord).setNode(nodeBuilder.setIpAddress("").build())
            .build();

    for (LandscapeRecord r : new LandscapeRecord[] {nullNode, emptyHostName, emptyIpAddress}) {
      Assertions.assertThrows(InvalidRecordException.class, () -> validator.validate(r));
    }
  }

  @Test
  void invalidApp() {
    Application.Builder appBuilder = Application.newBuilder(validRecord.getApplication());
    LandscapeRecord nullApp =
        LandscapeRecord.newBuilder(validRecord).build();
    nullApp.application = null;


    LandscapeRecord emptyAppName =
        LandscapeRecord.newBuilder(validRecord).setApplication(appBuilder.setName("").build())
            .build();

    LandscapeRecord emptyPid =
        LandscapeRecord.newBuilder(validRecord).setApplication(appBuilder.setPid("").build())
            .build();


    LandscapeRecord emptyLanguage =
        LandscapeRecord.newBuilder(validRecord).setApplication(appBuilder.setLanguage("").build())
            .build();

    for (LandscapeRecord r : new LandscapeRecord[] {nullApp, emptyAppName, emptyPid, emptyLanguage}) {
      Assertions.assertThrows(InvalidRecordException.class, () -> validator.validate(r));
    }
  }

  @Test
  void invalidPackage() {
    LandscapeRecord emptyPkg = LandscapeRecord.newBuilder(validRecord).setPackage$("").build();
    for (LandscapeRecord r : new LandscapeRecord[] {emptyPkg}) {
      Assertions.assertThrows(InvalidRecordException.class, () -> validator.validate(r));
    }
  }

  @Test
  void invalidClazz() {
    LandscapeRecord emptyCls = LandscapeRecord.newBuilder(validRecord).setClass$("").build();
    for (LandscapeRecord r : new LandscapeRecord[] {emptyCls}) {
      Assertions.assertThrows(InvalidRecordException.class, () -> validator.validate(r));
    }
  }



}
