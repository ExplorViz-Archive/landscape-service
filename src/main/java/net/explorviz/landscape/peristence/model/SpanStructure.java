package net.explorviz.landscape.peristence.model;

import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import java.time.Instant;
import org.apache.commons.lang3.builder.ToStringBuilder;

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

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("landscapeToken", landscapeToken)
        .append("timestamp", timestamp)
        .append("hashCode", hashCode)
        .append("hostName", hostName)
        .append("hostIpAddress", hostIpAddress)
        .append("applicationName", applicationName)
        .append("instanceId", instanceId)
        .append("applicationLanguage", applicationLanguage)
        .append("fullyQualifiedOperationName", fullyQualifiedOperationName)
        .toString();
  }

  public static class Builder {

    private String landscapeToken;
    private long timestamp;
    private String hashCode;
    private String hostName;
    private String hostIpAddress;
    private String applicationName;
    private String instanceId;
    private String applicationLanguage;
    private String fqn;

    public Builder fromAvro(net.explorviz.avro.SpanStructure avro) {
      this.timestamp =
          Instant
              .ofEpochSecond(avro.getTimestamp().getSeconds(), avro.getTimestamp().getNanoAdjust())
              .toEpochMilli();

      this.landscapeToken = avro.getLandscapeToken();
      this.hashCode = avro.getHashCode();
      this.hostName = avro.getHostname();
      this.hostIpAddress = avro.getHostIpAddress();
      this.applicationName = avro.getAppName();
      this.instanceId = avro.getAppInstanceId();
      this.applicationLanguage = avro.getAppLanguage();
      this.fqn = avro.getFullyQualifiedOperationName();
      return this;
    }

    public Builder setLandscapeToken(final String landscapeToken) {
      this.landscapeToken = landscapeToken;
      return this;
    }

    public Builder setTimestamp(final long timestamp) {
      this.timestamp = timestamp;
      return this;
    }

    public Builder setHashCode(final String hashCode) {
      this.hashCode = hashCode;
      return this;
    }

    public Builder setHostName(final String hostName) {
      this.hostName = hostName;
      return this;
    }

    public Builder setHostIpAddress(final String hostIpAddress) {
      this.hostIpAddress = hostIpAddress;
      return this;
    }

    public Builder setApplicationName(final String applicationName) {
      this.applicationName = applicationName;
      return this;
    }

    public Builder setInstanceId(final String instanceId) {
      this.instanceId = instanceId;
      return this;
    }

    public Builder setApplicationLanguage(final String applicationLanguage) {
      this.applicationLanguage = applicationLanguage;
      return this;
    }

    public Builder setFqn(final String fqn) {
      this.fqn = fqn;
      return this;
    }

    public SpanStructure build() {
      return new SpanStructure(landscapeToken, timestamp, hashCode, hostName, hostIpAddress,
          applicationName, instanceId, applicationLanguage, fqn);
    }

  }


}
