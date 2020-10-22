package net.explorviz.landscape.service.assemble.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.explorviz.avro.landscape.model.Application;
import net.explorviz.avro.landscape.model.Class;
import net.explorviz.avro.landscape.model.Landscape;
import net.explorviz.avro.landscape.model.Node;
import net.explorviz.avro.landscape.model.Package;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AssemblyUtilsTest {

  private Landscape landscape;
  private Node nodeA;
  private Application app;
  private Node nodeB;


  @BeforeEach
  void setUp() {
    Package root = PackageHelper.toHierarchy("net.example.foo".split("\\."));
    Package examplePkg = root.getSubPackages().get(0);
    examplePkg.getSubPackages().add(new Package("bar", new ArrayList<>(), new ArrayList<>()));
    app = new Application("App", "java", "pid", new ArrayList<>(Collections.singletonList(root)));
    nodeA = new Node("1.2.3.4", "host1", Collections.singletonList(app));
    nodeB = new Node("host2", "4.5.6.7", Collections.emptyList());
    landscape = new Landscape("tok", Arrays.asList(nodeA, nodeB));
  }

  @Test
  void findNodeExisting() {
    Optional<Node> got =
        AssemblyUtils.findNode(landscape, nodeA.getHostName(), nodeA.getIpAddress());
    Assertions.assertTrue(got.isPresent());
    Assertions.assertEquals(nodeA, got.get());
  }

  @Test
  void findNodeNonExisting() {
    Optional<Node> got =
        AssemblyUtils.findNode(landscape, nodeA.getHostName(), nodeB.getIpAddress());
    Assertions.assertFalse(got.isPresent());
  }

  @Test
  void findApplicationExisting() {
    Application toFind =
        new Application(app.getName(), app.getLanguage(), app.getPid(), new ArrayList<>());
    Optional<Application> got = AssemblyUtils
        .findApplication(nodeA, toFind.getPid());
    Assertions.assertTrue(got.isPresent());
    Assertions.assertEquals(app, got.get());
  }



  @Test
  void findClazzExisting() {
    List<Class> classes =
        Arrays.asList(new Class("A", new ArrayList<>()), new Class("B", new ArrayList<>()));
    Package p = new Package("foo", new ArrayList<>(), classes);

    Optional<Class> got = AssemblyUtils.findClazz(p, "A");
    Assertions.assertTrue(got.isPresent());
  }

  @Test
  void findClazzNonExisting() {
    List<Class> classes =
        Arrays.asList(new Class("A", new ArrayList<>()), new Class("B", new ArrayList<>()));
    Package p = new Package("foo", new ArrayList<>(), classes);

    Optional<Class> got = AssemblyUtils.findClazz(p, "C");
    Assertions.assertFalse(got.isPresent());
  }
}
