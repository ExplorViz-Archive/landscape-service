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

  @ClusteringColumn(0)
  private long timestamp;

  @ClusteringColumn(1)
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

  public void setLandscapeToken(final String landscapeToken) {
    this.landscapeToken = landscapeToken;
  }

  public void setTimestamp(final long timestamp) {
    this.timestamp = timestamp;
  }

  public void setHashCode(final long hashCode) {
    this.hashCode = hashCode;
  }

  public void setHostname(final String hostname) {
    this.hostname = hostname;
  }

  public void setHostIpAddress(final String hostIpAddress) {
    this.hostIpAddress = hostIpAddress;
  }

  public void setApplicationName(final String applicationName) {
    this.applicationName = applicationName;
  }

  public void setInstanceId(final String instanceId) {
    this.instanceId = instanceId;
  }

  public void setAppLanguage(final String appLanguage) {
    this.appLanguage = appLanguage;
  }

  public void setFullyQualifiedOperationName(final String fullyQualifiedOperationName) {
    this.fullyQualifiedOperationName = fullyQualifiedOperationName;
  }
}
