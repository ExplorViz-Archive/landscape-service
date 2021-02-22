package net.explorviz.landscape.peristence.model;

import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import java.time.Instant;

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
  private String hashCode;

  private String hostName;
  private String hostIpAddress;

  private String applicationName;
  private String instanceId;
  private String applicationLanguage;

  @CqlName("method_fqn")
  private String fullyQualifiedOperationName;


  public SpanStructure(final String landscapeToken, final long timestamp, final String hashCode,
                       final String hostName, final String hostIpAddress,
                       final String applicationName,
                       final String instanceId, final String applicationLanguage,
                       final String fullyQualifiedOperationName) {
    this.landscapeToken = landscapeToken;
    this.timestamp = timestamp;
    this.hashCode = hashCode;
    this.hostName = hostName;
    this.hostIpAddress = hostIpAddress;
    this.applicationName = applicationName;
    this.instanceId = instanceId;
    this.applicationLanguage = applicationLanguage;
    this.fullyQualifiedOperationName = fullyQualifiedOperationName;
  }

  public SpanStructure() { /* Object-Mapper required */ }

  public String getLandscapeToken() {
    return landscapeToken;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public String getHashCode() {
    return hashCode;
  }

  public String getHostName() {
    return hostName;
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

  public String getApplicationLanguage() {
    return applicationLanguage;
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

  public void setHashCode(final String hashCode) {
    this.hashCode = hashCode;
  }

  public void setHostName(final String hostName) {
    this.hostName = hostName;
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

  public void setApplicationLanguage(final String applicationLanguage) {
    this.applicationLanguage = applicationLanguage;
  }

  public void setFullyQualifiedOperationName(final String fullyQualifiedOperationName) {
    this.fullyQualifiedOperationName = fullyQualifiedOperationName;
  }

  public static SpanStructure fromAvro(net.explorviz.avro.SpanStructure avro) {
    long timestamp =
        Instant.ofEpochSecond(avro.getTimestamp().getSeconds(), avro.getTimestamp().getNanoAdjust())
            .toEpochMilli();
    return new SpanStructure(
        avro.getLandscapeToken(),
        timestamp,
        avro.getHashCode(),
        avro.getHostname(),
        avro.getHostIpAddress(),
        avro.getAppName(),
        avro.getAppInstanceId(),
        avro.getAppLanguage(),
        avro.getFullyQualifiedOperationName());
  }
}
