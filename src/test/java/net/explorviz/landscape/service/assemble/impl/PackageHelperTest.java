package net.explorviz.landscape.service.assemble.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.explorviz.landscape.model.Application;
import net.explorviz.landscape.model.Landscape;
import net.explorviz.landscape.model.Node;
import net.explorviz.landscape.model.Package;
import net.explorviz.landscape.service.assemble.LandscapeAssemblyException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PackageHelperTest {

  private Landscape landscape;
  private Node nodeA;
  private Application app;
  private Node nodeB;


  @BeforeEach
  void setUp() {
    Package root = PackageHelper.toHierarchy("net.example.foo".split("\\."));
    Package examplePkg = root.getSubPackages().get(0);
    examplePkg.getSubPackages().add(new Package("bar", new ArrayList<>(), new ArrayList<>()));
    app = new Application("App", "java", "pid", Collections.singletonList(root));

    nodeA = new Node("1.2.3.4", "host1", Collections.singletonList(app));
    nodeB = new Node("4.5.6.7", "host2", Collections.emptyList());
    landscape = new Landscape("tok", Arrays.asList(nodeA, nodeB));
  }

  @Test
  void lowestPackageIndexFull() {
    // Completely known package path
    String[] pkgs = "net.example.bar".split("\\.");
    int got = PackageHelper.lowestPackageIndex(app, pkgs);
    Assertions.assertEquals(3, got);
  }

  @Test
  void lowestPackageIndexPart() {
    // Partial known package path
    String[] pkgs = "net.example.new".split("\\.");
    int got = PackageHelper.lowestPackageIndex(app, pkgs);
    Assertions.assertEquals(2, got);
  }

  @Test
  void lowestPackageIndexNone() {
    // Unknown package path from root on
    String[] pkgs = "org.foo".split("\\.");
    int got = PackageHelper.lowestPackageIndex(app, pkgs);
    Assertions.assertEquals(0, got);
  }

  @Test
  void toHierarchy() {
    String[] branch = "net.example.foo.bar".split("\\.");
    Package p = PackageHelper.toHierarchy(branch);

    Assertions.assertEquals(branch[0], p.getName());
    List<Package> current = p.getSubPackages();
    for (int i = 1; i < branch.length; i++) {
      Assertions.assertEquals(1, current.size());
      Assertions.assertEquals(branch[i], current.get(0).getName());
      current = current.get(0).getSubPackages();
    }
  }

  @Test
  void fromPathExisting() throws LandscapeAssemblyException {
    String[] toLeaf = "net.example.bar".split("\\.");
    String[] partial = "net.example".split("\\.");
    List<String[]> cases = Arrays.asList(partial, toLeaf);

    for (String[] tt : cases) {
      Package got = PackageHelper.fromPath(app, tt);
      Assertions.assertEquals(tt[tt.length - 1], got.getName());
    }
  }

  @Test
  void fromPathNonExisting() throws LandscapeAssemblyException {
    String[] unknownRoot = "org.something.bar".split("\\.");
    String[] tooLong = "net.example.bar.foo.bar2".split("\\.");
    String[] unknownMiddle = "net.example2.bar".split("\\.");
    List<String[]> cases = Arrays.asList(unknownRoot, tooLong, unknownMiddle);

    for (String[] tt : cases) {
      Assertions
          .assertThrows(LandscapeAssemblyException.class, () -> PackageHelper.fromPath(app, tt));
    }
  }
}
