package net.explorviz.landscape.service.assemble.impl;

import java.util.Optional;
import net.explorviz.landscape.model.Application;
import net.explorviz.landscape.model.Class;
import net.explorviz.landscape.model.Landscape;
import net.explorviz.landscape.model.Node;
import net.explorviz.landscape.model.Package;

public final class AssemblyUtils {

  private AssemblyUtils() { /* Utility Class */ }

  /**
   * Searches for a {@link Node} in a landscape.
   *
   * @param landscape the landscape
   * @param hostName  the host name of the node to find
   * @param ipAddress the ip address of the node to find
   * @return an optional that contains the node if it is included in the landscape, and is empty
   *     otherwise
   */
  public static Optional<Node> findNode(Landscape landscape, String hostName, String ipAddress) {
    for (Node n : landscape.getNodes()) {
      if (n.getHostName().equals(hostName) && n.getIpAddress().equals(ipAddress)) {
        return Optional.of(n);
      }
    }
    return Optional.empty();
  }

  /**
   * Searches for an {@link Application} in a node.
   *
   * @param node the node
   * @param pid  the PID of the application to search for
   * @return an optional that contains the app if it is included in the node, and is empty
   *     otherwise
   */
  public static Optional<Application> findApplication(Node node, String pid) {
    for (Application a : node.getApplications()) {

      if (a.getPid().equals(pid)) {
        return Optional.of(a);
      }
    }
    return Optional.empty();
  }

  /**
   * Searches fo a {@link Class} in a package.
   *
   * @param pkg       the package to search in
   * @param className the name of the class to search for
   * @return an optional that contains the class if it is included in the package, and is empty
   *     *     otherwise
   */
  public static Optional<Class> findClazz(Package pkg, String className) {
    return pkg.getClasses().stream().filter(c -> c.getName().equals(className)).findAny();
  }


}
