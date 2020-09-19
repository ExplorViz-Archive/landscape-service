package net.explorviz.landscape.service.assemble.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.explorviz.landscape.flat.LandscapeRecord;
import net.explorviz.landscape.helper.SampleLoader;
import net.explorviz.landscape.model.Application;
import net.explorviz.landscape.model.Class;
import net.explorviz.landscape.model.Landscape;
import net.explorviz.landscape.model.Method;
import net.explorviz.landscape.model.Node;
import net.explorviz.landscape.model.Package;
import net.explorviz.landscape.service.assemble.LandscapeAssemblyException;
import org.apache.avro.io.Encoder;
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
    RecordValidator validator = new RecordValidator();
    this.assembler = new DefaultLandscapeAssembler(validator);
  }

  @Test
  void assembleFromRecords() throws IOException, LandscapeAssemblyException {
    List<LandscapeRecord> records = SampleLoader.loadSampleApplication();
    String tok = records.get(0).getLandscapeToken();
    List<LandscapeRecord> singleTokenRecords =
        records.stream().filter(r -> tok.equals(r.getLandscapeToken()))
            .collect(Collectors.toList());

    Landscape generated = assembler.assembleFromRecords(singleTokenRecords);

    // Theses stats change if `sampleApplicationRecords.json` is modified!
    Assertions.assertEquals(records.get(0).getLandscapeToken(), generated.getLandscapeToken());
    Assertions.assertEquals(1, generated.getNodes().size());

    Node node = generated.getNodes().stream().findAny().orElseThrow();
    Assertions.assertEquals(nodeName, node.getHostName());
    Assertions.assertEquals(nodeIp, node.getIpAddress());
    Assertions.assertEquals(1, node.getApplications().size());

    Application app = node.getApplications().stream().findAny().orElseThrow();
    Assertions.assertEquals(appName, app.getName());
    //Assertions.assertEquals(appPid, app.getPid());
    Assertions.assertEquals(appLanguage, app.getLanguage());

    // Find classes
    Package netPkg =
        app.getPackages().stream().filter(p -> "net".equals(p.getName())).findAny().orElseThrow();
    Package explorvizPkg =
        netPkg.getSubPackages().stream().filter(p -> "explorviz".equals(p.getName())).findAny()
            .orElseThrow();
    Package sampleAppPkg =
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
    List<LandscapeRecord> records = SampleLoader.loadSampleApplication();
    LandscapeRecord r =
        LandscapeRecord.newBuilder(records.get(0)).setLandscapeToken("test").build();
    records.add(r);
    Assertions.assertThrows(LandscapeAssemblyException.class,
        () -> assembler.assembleFromRecords(records));
  }

  @Test
  void assembleFromEmptyRecords() throws IOException, LandscapeAssemblyException {
    Assertions.assertThrows(LandscapeAssemblyException.class,
        () -> assembler.assembleFromRecords(Collections.emptyList()));
  }



  @Test
  void insertAll() throws IOException, LandscapeAssemblyException {
    List<LandscapeRecord> records = SampleLoader.loadSampleApplication();
    String tok = records.get(0).getLandscapeToken();
    List<LandscapeRecord> singleTokenRecords =
        records.stream().filter(r -> tok.equals(r.getLandscapeToken()))
            .collect(Collectors.toList());


    String hostname = "host";
    String ip = "0.0.0.0";
    String appname = "app";
    String pid = "1";
    List<Class> classes = new ArrayList<>(Arrays.asList(new Class("TestClass",
        new ArrayList<>(Collections.singleton(new Method("method", "1234"))))));

    Package rootPkg1 = new Package("net", new ArrayList<>(), classes);
    List<Application> apps = new ArrayList<>(Collections.singletonList(
        new Application(appname, "java", pid,
            new ArrayList<>(Collections.singletonList(rootPkg1)))));
    List<Node> nodes = new ArrayList<>(
        new ArrayList<>(Collections.singletonList(new Node(ip, hostname, apps))));
    Landscape landscape = new Landscape("tok", nodes);


    // TODO: Use multiple records...

    String newClass = "TestClass";
    String newPkg = "net.test";
    Method newMethod = new Method("method", "1234");
    LandscapeRecord toInsert =
        new LandscapeRecord("tok", 123L, new net.explorviz.landscape.flat.Node(ip, hostname),
            new net.explorviz.landscape.flat.Application(appname, pid, "java"), newPkg, newClass,
            newMethod.getName(), newMethod.getHashCode());

    assembler.insertAll(landscape, Collections.singleton(toInsert));

    // TODO: Check if inserted
    Node foundNode = landscape.getNodes().stream().filter(n -> n.getIpAddress().equals(ip))
        .filter(n -> n.getHostName().equals(hostname))
        .findAny().orElseThrow();
    Application foundApp =
        foundNode.getApplications().stream().filter(a -> a.getName().equals(appname))
            .filter(a -> a.getLanguage().equals("java")).findAny().orElseThrow();

    Package foundPkg =
        foundApp.getPackages().stream().filter(p -> p.getName().equals("net")).findAny()
            .orElseThrow()
            .getSubPackages().stream().filter(p -> p.getName().equals("test")).findAny()
            .orElseThrow();
    Class foundClazz =
        foundPkg.getClasses().stream().filter(c -> c.getName().equals(newClass)).findAny()
            .orElseThrow();
    Assertions.assertTrue(foundClazz.getMethods().stream().anyMatch(m -> m.equals(newMethod)));

  }
}
