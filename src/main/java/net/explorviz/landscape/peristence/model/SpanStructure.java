package net.explorviz.landscape.peristence.model;

import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;

/**
 * Entity that holds structural information.
 * Encodes a single branch in the landscape model graph, were the root
 * is the landscapeToken and the leaf the operation name.
 */
@Entity
public class SpanStructure {

  @PartitionKey
  private String landscapeToken;

  @ClusteringColumn
  private long timestamp;

  @ClusteringColumn
  private long hashCode;

  private String hostname;
  private String hostIpAddress;

  private String applicationName;
  private String instanceId;
  private String appLanguage;

  private String fullyQualifiedOperationName;


  public SpanStructure(final String landscapeToken, final long timestamp, final long hashCode,
                       final String hostname, final String hostIpAddress,
                       final String applicationName,
                       final String instanceId, final String appLanguage,
                       final String fullyQualifiedOperationName) {
    this.landscapeToken = landscapeToken;
    this.timestamp = timestamp;
    this.hashCode = hashCode;
    this.hostname = hostname;
    this.hostIpAddress = hostIpAddress;
    this.applicationName = applicationName;
    this.instanceId = instanceId;
    this.appLanguage = appLanguage;
    this.fullyQualifiedOperationName = fullyQualifiedOperationName;
  }

  public SpanStructure() { /* Object-Mapper required */ }

  public String getLandscapeToken() {
    return landscapeToken;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public long getHashCode() {
    return hashCode;
  }

  public String getHostname() {
    return hostname;
  }

  public String getHostIpAddress() {
    return hostIpAddress;
  }

  public String getApplicationName() {
    return applicationName;
  }

  public String getInstanceId() {
    return instanceId;
  }

  public String getAppLanguage() {
    return appLanguage;
  }

  public String getFullyQualifiedOperationName() {
    return fullyQualifiedOperationName;
  }
}
