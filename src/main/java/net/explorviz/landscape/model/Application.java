package net.explorviz.landscape.model;

import java.util.Collection;
import java.util.Collections;

/**
 * An application is the abstraction of any process monitored with ExplorViz.
 */
public class Application {

  private final String name;
  private final String language;
  private final String pid;

  private final Collection<Package> packages;

  public Application(String name, String language, String pid,
                     Collection<Package> packages) {
    this.name = name;
    this.language = language;
    this.pid = pid;
    this.packages = packages;
  }

  public Application(String name, String language, String pid) {
    this(name, language, pid, Collections.emptyList());
  }

  public String getName() {
    return name;
  }

  public String getPid() {
    return pid;
  }

  public String getLanguage() {
    return language;
  }

  public Collection<Package> getPackages() {
    return packages;
  }
}
