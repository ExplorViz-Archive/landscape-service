package net.explorviz.landscape.model;

import java.util.Collection;
import java.util.Collections;

/**
 * A node represent a physical host uniquely identified by its hostname and ip address.
 * Each node can host a set of {@link Application}s.
 */
public class Node {

  private final String hostName;
  private final String ipAddress;

  private final Collection<Application> applications;

  public Node(String hostName, String ipAddress,
              Collection<Application> applications) {
    this.hostName = hostName;
    this.ipAddress = ipAddress;
    this.applications = applications;
  }

  public Node(String hostName, String ipAddress) {
    this(hostName, ipAddress, Collections.emptyList());
  }

  public String getHostName() {
    return hostName;
  }

  public String getIpAddress() {
    return ipAddress;
  }

  public Collection<Application> getApplications() {
    return applications;
  }
}
