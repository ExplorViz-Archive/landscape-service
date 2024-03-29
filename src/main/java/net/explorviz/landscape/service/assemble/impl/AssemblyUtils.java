package net.explorviz.landscape.service.assemble.impl;

import java.util.Optional;
import net.explorviz.avro.landscape.model.Application;
import net.explorviz.avro.landscape.model.Class;
import net.explorviz.avro.landscape.model.Landscape;
import net.explorviz.avro.landscape.model.Node;
import net.explorviz.avro.landscape.model.Package;

/**
 * Utility class that provides various methods for finding elements in a landscape graph.
 */
public final class AssemblyUtils {

  private AssemblyUtils() {
    /* Utility Class */
  }

  /**
   * Searches for a {@link Node} in a landscape.
   *
   * @param landscape the landscape
   * @param hostName  the host name of the node to find
   * @param ipAddress the ip address of the node to find
   * @return an optional that contains the node if it is included in the landscape, and is empty
   *     otherwise
   */
  public static Optional<Node> findNode(final Landscape landscape, final String hostName,
      final String ipAddress) {
    for (final Node n : landscape.getNodes()) {
      if (n.getHostName().equals(hostName) && n.getIpAddress().equals(ipAddress)) {
        return Optional.of(n);
      }
    }
    return Optional.empty();
  }

  /**
   * Searches for an {@link Application} in a node.
   *
   * @param node       the node
   * @param instanceId the instance id of the application to search for
   * @return an optional that contains the app if it is included in the node, and is empty otherwise
   */
  public static Optional<Application> findApplication(final Node node, final String name,
      final String instanceId) {
    for (final Application a : node.getApplications()) {

      if (a.getInstanceId().equals(instanceId) && a.getName().equals(name)) {
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
   *     otherwise
   */
  public static Optional<Class> findClazz(final Package pkg, final String className) {
    return pkg.getClasses().stream().filter(c -> c.getName().equals(className)).findAny();
  }

}
