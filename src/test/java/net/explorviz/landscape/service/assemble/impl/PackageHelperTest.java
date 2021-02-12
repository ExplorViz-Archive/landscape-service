package net.explorviz.landscape.service.assemble.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.explorviz.avro.landscape.model.Application;
import net.explorviz.avro.landscape.model.Landscape;
import net.explorviz.avro.landscape.model.Node;
import net.explorviz.avro.landscape.model.Package;
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
    final Package root = PackageHelper.toHierarchy("net.example.foo".split("\\."));
    final Package examplePkg = root.getSubPackages().get(0);
    examplePkg.getSubPackages().add(new Package("bar", new ArrayList<>(), new ArrayList<>()));
    this.app = new Application("App", "java", "1", Collections.singletonList(root));

    this.nodeA = new Node("1.2.3.4", "host1", Collections.singletonList(this.app));
    this.nodeB = new Node("4.5.6.7", "host2", Collections.emptyList());
    this.landscape = new Landscape("tok", Arrays.asList(this.nodeA, this.nodeB));
  }

  @Test
  void lowestPackageIndexFull() {
    // Completely known package path
    final String[] pkgs = "net.example.bar".split("\\.");
    final int got = PackageHelper.lowestPackageIndex(this.app, pkgs);
    Assertions.assertEquals(3, got);
  }

  @Test
  void lowestPackageIndexPart() {
    // Partial known package path
    final String[] pkgs = "net.example.new".split("\\.");
    final int got = PackageHelper.lowestPackageIndex(this.app, pkgs);
    Assertions.assertEquals(2, got);
  }

  @Test
  void lowestPackageIndexNone() {
    // Unknown package path from root on
    final String[] pkgs = "org.foo".split("\\.");
    final int got = PackageHelper.lowestPackageIndex(this.app, pkgs);
    Assertions.assertEquals(0, got);
  }

  @Test
  void toHierarchy() {
    final String[] branch = "net.example.foo.bar".split("\\.");
    final Package p = PackageHelper.toHierarchy(branch);

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
    final String[] toLeaf = "net.example.bar".split("\\.");
    final String[] partial = "net.example".split("\\.");
    final List<String[]> cases = Arrays.asList(partial, toLeaf);

    for (final String[] tt : cases) {
      final Package got = PackageHelper.fromPath(this.app, tt);
      Assertions.assertEquals(tt[tt.length - 1], got.getName());
    }
  }

  @Test
  void fromPathNonExisting() throws LandscapeAssemblyException {
    final String[] unknownRoot = "org.something.bar".split("\\.");
    final String[] tooLong = "net.example.bar.foo.bar2".split("\\.");
    final String[] unknownMiddle = "net.example2.bar".split("\\.");
    final List<String[]> cases = Arrays.asList(unknownRoot, tooLong, unknownMiddle);

    for (final String[] tt : cases) {
      Assertions
          .assertThrows(LandscapeAssemblyException.class,
              () -> PackageHelper.fromPath(this.app, tt));
    }
  }
}
