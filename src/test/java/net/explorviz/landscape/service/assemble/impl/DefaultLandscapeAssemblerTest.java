package net.explorviz.landscape.service.assemble.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.explorviz.avro.landscape.flat.LandscapeRecord;
import net.explorviz.avro.landscape.model.Application;
import net.explorviz.avro.landscape.model.Class;
import net.explorviz.avro.landscape.model.Landscape;
import net.explorviz.avro.landscape.model.Method;
import net.explorviz.avro.landscape.model.Node;
import net.explorviz.avro.landscape.model.Package;
import net.explorviz.landscape.helper.SampleLoader;
import net.explorviz.landscape.service.assemble.LandscapeAssemblyException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultLandscapeAssemblerTest {


  private DefaultLandscapeAssembler assembler;

  // Stats of the "simple" sample records (samplerecordss/sampleApplicationRecords.json)
  // If `sampleApplicationRecords.json` is modified this values need to be adjusted accordingly
  private final String token = "samplelandscape";
  private final String nodeIp = "1.2.3.4";
  private final String nodeName = "testhost";
  private final String appName = "UNKNOWN-APPLICATION";
  private final String appLanguage = "java";



  @BeforeEach
  void setUp() {
    final RecordValidator validator = new RecordValidator();
    this.assembler = new DefaultLandscapeAssembler(validator);
  }

  @Test
  void assembleFromRecords() throws IOException, LandscapeAssemblyException {
    final List<LandscapeRecord> records = SampleLoader.loadSampleApplication();
    final String tok = records.get(0).getLandscapeToken();
    final List<LandscapeRecord> singleTokenRecords =
        records.stream().filter(r -> tok.equals(r.getLandscapeToken()))
            .collect(Collectors.toList());

    final Landscape generated = this.assembler.assembleFromRecords(singleTokenRecords);

    // Theses stats change if `sampleApplicationRecords.json` is modified!
    Assertions.assertEquals(records.get(0).getLandscapeToken(), generated.getLandscapeToken());
    Assertions.assertEquals(1, generated.getNodes().size());

    final Node node = generated.getNodes().stream().findAny().orElseThrow();
    Assertions.assertEquals(this.nodeName, node.getHostName());
    Assertions.assertEquals(this.nodeIp, node.getIpAddress());
    Assertions.assertEquals(1, node.getApplications().size());

    final Application app = node.getApplications().stream().findAny().orElseThrow();
    Assertions.assertEquals(this.appName, app.getName());
    // Assertions.assertEquals(appPid, app.getPid());
    Assertions.assertEquals(this.appLanguage, app.getLanguage());

    // Find classes
    final Package netPkg =
        app.getPackages().stream().filter(p -> "net".equals(p.getName())).findAny().orElseThrow();
    final Package explorvizPkg =
        netPkg.getSubPackages().stream().filter(p -> "explorviz".equals(p.getName())).findAny()
            .orElseThrow();
    final Package sampleAppPkg =
        explorvizPkg.getSubPackages().stream().filter(p -> "sampleApplication".equals(p.getName()))
            .findAny()
            .orElseThrow();

    Assertions.assertTrue(sampleAppPkg.getClasses().stream()
        .anyMatch(c -> "Main$ApplicationTask".equals(c.getName())));
    Assertions.assertTrue(sampleAppPkg.getClasses().stream()
        .anyMatch(c -> "Main$DatabaseTask".equals(c.getName())));
  }

  @Test
  void assembleFromRecordsMultipleTokens() throws IOException, LandscapeAssemblyException {
    final List<LandscapeRecord> records = SampleLoader.loadSampleApplication();
    final LandscapeRecord r =
        LandscapeRecord.newBuilder(records.get(0)).setLandscapeToken("test").build();
    records.add(r);
    Assertions.assertThrows(LandscapeAssemblyException.class,
        () -> this.assembler.assembleFromRecords(records));
  }

  @Test
  void assembleFromEmptyRecords() throws IOException, LandscapeAssemblyException {
    Assertions.assertThrows(LandscapeAssemblyException.class,
        () -> this.assembler.assembleFromRecords(Collections.emptyList()));
  }



  @Test
  void insertAll() throws IOException, LandscapeAssemblyException {
    final List<LandscapeRecord> records = SampleLoader.loadSampleApplication();
    final String tok = records.get(0).getLandscapeToken();
    final List<LandscapeRecord> singleTokenRecords =
        records.stream().filter(r -> tok.equals(r.getLandscapeToken()))
            .collect(Collectors.toList());


    final String hostname = "host";
    final String ip = "0.0.0.0";
    final String appname = "app";
    final String instanceId = "1";
    final List<Class> classes = new ArrayList<>(Arrays.asList(new Class("TestClass",
        new ArrayList<>(Collections.singleton(new Method("method", "1234"))))));

    final Package rootPkg1 = new Package("net", new ArrayList<>(), classes);
    final List<Application> apps = new ArrayList<>(Collections.singletonList(
        new Application(appname, "java", instanceId,
            new ArrayList<>(Collections.singletonList(rootPkg1)))));
    final List<Node> nodes = new ArrayList<>(
        new ArrayList<>(Collections.singletonList(new Node(ip, hostname, apps))));
    final Landscape landscape = new Landscape("tok", nodes);


    // TODO: Use multiple records...

    final String newClass = "TestClass";
    final String newPkg = "net.test";
    final Method newMethod = new Method("method", "1234");
    final LandscapeRecord toInsert =
        new LandscapeRecord("tok", 123L, new net.explorviz.avro.landscape.flat.Node(ip, hostname),
            new net.explorviz.avro.landscape.flat.Application(appname, instanceId, "java"), newPkg,
            newClass,
            newMethod.getName(), newMethod.getHashCode());


    this.assembler.insertAll(landscape, Collections.singleton(toInsert));

    // TODO: Check if inserted
    final Node foundNode = landscape.getNodes().stream().filter(n -> n.getIpAddress().equals(ip))
        .filter(n -> n.getHostName().equals(hostname))
        .findAny().orElseThrow();
    final Application foundApp =
        foundNode.getApplications().stream().filter(a -> a.getName().equals(appname))
            .filter(a -> a.getLanguage().equals("java")).findAny().orElseThrow();

    final Package foundPkg =
        foundApp.getPackages().stream().filter(p -> p.getName().equals("net")).findAny()
            .orElseThrow()
            .getSubPackages().stream().filter(p -> p.getName().equals("test")).findAny()
            .orElseThrow();
    final Class foundClazz =
        foundPkg.getClasses().stream().filter(c -> c.getName().equals(newClass)).findAny()
            .orElseThrow();
    Assertions.assertTrue(foundClazz.getMethods().stream().anyMatch(m -> m.equals(newMethod)));

  }
}
