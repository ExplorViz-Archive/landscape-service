package net.explorviz.landscape.model;

import java.util.Collection;

/**
 * Root of a landscape model.
 */
public class Landscape {

  private final String token;
  private final Collection<Node> nodes;

  public Landscape(String token, Collection<Node> nodes) {
    this.token = token;
    this.nodes = nodes;
  }

  public String getToken() {
    return token;
  }

  public Collection<Node> getNodes() {
    return nodes;
  }
}
