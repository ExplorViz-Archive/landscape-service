package net.explorviz.landscape.model;


import java.util.ArrayList;
import java.util.Collection;

/**
 * Leafs of the landscape tree.
 */
public class Clazz {

  private final String name;
  private final Collection<String> methods;

  public Clazz(String name, Collection<String> methods) {
    this.name = name;
    this.methods = methods;
  }

  public Clazz(String name) {
    this(name, new ArrayList<>());
  }

  public String getName() {
    return name;
  }

  public Collection<String> getMethods() {
    return methods;
  }
}
