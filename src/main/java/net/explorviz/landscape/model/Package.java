package net.explorviz.landscape.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Abstraction of a package/namespace.
 */
public class Package {

  private final String name;
  private final List<Package> subPackages;
  private final Collection<Clazz> classes;

  public Package(String name, List<Package> subPackages,
                 List<Clazz> classes) {
    this.name = name;
    this.subPackages = subPackages;
    this.classes = classes;
  }


  public Package(String name) {
    this(name, new ArrayList<>(), new ArrayList<>());
  }

  public String getName() {
    return name;
  }

  public List<Package> getSubPackages() {
    return subPackages;
  }

  public Collection<Clazz> getClasses() {
    return classes;
  }
}
