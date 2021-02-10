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
    final Package root = PackageHelper.toHierarchy("net.example.foo".split("\\."));
    final Package examplePkg = root.getSubPackages().get(0);
    examplePkg.getSubPackages().add(new Package("bar", new ArrayList<>(), new ArrayList<>()));
    this.app =
        new Application("App", "java", 1L, new ArrayList<>(Collections.singletonList(root)));
    this.nodeA = new Node("1.2.3.4", "host1", Collections.singletonList(this.app));
    this.nodeB = new Node("host2", "4.5.6.7", Collections.emptyList());
    this.landscape = new Landscape("tok", Arrays.asList(this.nodeA, this.nodeB));
  }

  @Test
  void findNodeExisting() {
    final Optional<Node> got =
        AssemblyUtils.findNode(this.landscape, this.nodeA.getHostName(), this.nodeA.getIpAddress());
    Assertions.assertTrue(got.isPresent());
    Assertions.assertEquals(this.nodeA, got.get());
  }

  @Test
  void findNodeNonExisting() {
    final Optional<Node> got =
        AssemblyUtils.findNode(this.landscape, this.nodeA.getHostName(), this.nodeB.getIpAddress());
    Assertions.assertFalse(got.isPresent());
  }

  @Test
  void findApplicationExisting() {
    final Application toFind =
        new Application(this.app.getName(), this.app.getLanguage(), this.app.getInstanceId(),
            new ArrayList<>());
    final Optional<Application> got = AssemblyUtils
        .findApplication(this.nodeA, toFind.getName(), toFind.getInstanceId());
    Assertions.assertTrue(got.isPresent());
    Assertions.assertEquals(this.app, got.get());
  }



  @Test
  void findClazzExisting() {
    final List<Class> classes =
        Arrays.asList(new Class("A", new ArrayList<>()), new Class("B", new ArrayList<>()));
    final Package p = new Package("foo", new ArrayList<>(), classes);

    final Optional<Class> got = AssemblyUtils.findClazz(p, "A");
    Assertions.assertTrue(got.isPresent());
  }

  @Test
  void findClazzNonExisting() {
    final List<Class> classes =
        Arrays.asList(new Class("A", new ArrayList<>()), new Class("B", new ArrayList<>()));
    final Package p = new Package("foo", new ArrayList<>(), classes);

    final Optional<Class> got = AssemblyUtils.findClazz(p, "C");
    Assertions.assertFalse(got.isPresent());
  }
}
