package net.explorviz.landscape.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.explorviz.landscape.model.Application;
import net.explorviz.landscape.model.Clazz;
import net.explorviz.landscape.model.Landscape;
import net.explorviz.landscape.model.Node;
import net.explorviz.landscape.model.Package;
import net.explorviz.landscape.service.LandscapeAssemblyException;
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
    examplePkg.getSubPackages().add(new Package("bar"));
    app = new Application("App", "java", "pid", Collections.singletonList(root));

    nodeA = new Node("host1", "1.2.3.4", Collections.singleton(app));
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
    Application toFind = new Application(app.getName(), app.getLanguage(), app.getPid());
    Optional<Application> got = AssemblyUtils
        .findApplication(nodeA, toFind.getPid(), toFind.getName(), toFind.getLanguage());
    Assertions.assertTrue(got.isPresent());
    Assertions.assertEquals(app, got.get());
  }



  @Test
  void findClazzExisting() {
    List<Clazz> classes = Arrays.asList(new Clazz("A"), new Clazz("B"));
    Package p = new Package("foo", new ArrayList<>(), classes);

    Optional<Clazz> got = AssemblyUtils.findClazz(p, "A");
    Assertions.assertTrue(got.isPresent());
  }

  @Test
  void findClazzNonExisting() {
    List<Clazz> classes = Arrays.asList(new Clazz("A"), new Clazz("B"));
    Package p = new Package("foo", new ArrayList<>(), classes);

    Optional<Clazz> got = AssemblyUtils.findClazz(p, "C");
    Assertions.assertFalse(got.isPresent());
  }
}