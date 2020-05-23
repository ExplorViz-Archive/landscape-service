package net.explorviz.landscape.service.impl;

import java.util.Optional;
import net.explorviz.landscape.model.Application;
import net.explorviz.landscape.model.Landscape;
import net.explorviz.landscape.model.Node;

public final class AssemblyUtils {

  private AssemblyUtils() { /* Utility Class */ }

  /**
   * Searches for a {@link Node} in a landscape.
   *
   * @param landscape the landscape
   * @param node      the node to find
   * @return an optional that contains the node if it is included in the landscape, and is empty
   *     otherwise
   */
  public static Optional<Node> findNode(Landscape landscape, Node node) {
    for (Node n : landscape.getNodes()) {
      if (n.getHostName().equals(node.getHostName()) && n.getIpAddress()
          .equals(node.getIpAddress())) {
        return Optional.of(n);
      }
    }
    return Optional.empty();
  }

  /**
   * Searches for an {@link Application} in a node.
   *
   * @param node the node
   * @param app  the app to find
   * @return an optional that contains the app if it is included in the node, and is empty
   *     otherwise
   */
  public static Optional<Application> findApplication(Node node, Application app) {
    for (Application a : node.getApplications()) {
      // TODO: Only check if PID equals. Currently PID is not included in records.
      if (a.getPid().equals(app.getPid()) && a.getName().equals(app.getName()) && a.getLanguage()
          .equals(app.getLanguage())) {
        return Optional.of(a);
      }
    }
    return Optional.empty();
  }

}
