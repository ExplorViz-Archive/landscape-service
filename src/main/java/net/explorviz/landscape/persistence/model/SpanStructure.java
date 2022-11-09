package net.explorviz.landscape.persistence.model;

import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import java.time.Instant;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Entity that holds structural information. Encodes a single branch in the landscape model graph,
 * were the root is the landscapeToken and the leaf the operation name.
 */
@Entity
public class SpanStructure {

  @PartitionKey
  private String landscapeToken;

  @ClusteringColumn
  private String hashCode; // NOPMD

  private long timestamp;

  private String hostName;
  private String hostIpAddress;

  private String applicationName;
  private String instanceId;
  private String applicationLanguage;

  @CqlName("method_fqn")
  private String fullyQualifiedOperationName;

  public SpanStructure(final String landscapeToken, final long timestamp, final String hashCode,
      final String hostName, final String hostIpAddress, final String applicationName,
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

  public SpanStructure() {
    /* Object-Mapper required */
  }

  public String getLandscapeToken() {
    return this.landscapeToken;
  }

  public long getTimestamp() {
    return this.timestamp;
  }

  public String getHashCode() {
    return this.hashCode;
  }

  public String getHostName() {
    return this.hostName;
  }

  public String getHostIpAddress() {
    return this.hostIpAddress;
  }

  public String getApplicationName() {
    return this.applicationName;
  }

  public String getInstanceId() {
    return this.instanceId;
  }

  public String getApplicationLanguage() {
    return this.applicationLanguage;
  }

  public String getFullyQualifiedOperationName() {
    return this.fullyQualifiedOperationName;
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
    return new ToStringBuilder(this).append("landscapeToken", this.landscapeToken)
        .append("timestamp", this.timestamp).append("hashCode", this.hashCode)
        .append("hostName", this.hostName).append("hostIpAddress", this.hostIpAddress)
        .append("applicationName", this.applicationName).append("instanceId", this.instanceId)
        .append("applicationLanguage", this.applicationLanguage)
        .append("fullyQualifiedOperationName", this.fullyQualifiedOperationName).toString();
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }

    final SpanStructure that = (SpanStructure) o;

    return new EqualsBuilder().append(this.timestamp, that.timestamp)
        .append(this.landscapeToken, that.landscapeToken).append(this.hashCode, that.hashCode)
        .append(this.hostName, that.hostName).append(this.hostIpAddress, that.hostIpAddress)
        .append(this.applicationName, that.applicationName).append(this.instanceId, that.instanceId)
        .append(this.applicationLanguage, that.applicationLanguage)
        .append(this.fullyQualifiedOperationName, that.fullyQualifiedOperationName).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).append(this.landscapeToken).append(this.timestamp) // NOCS
        .append(this.hashCode).append(this.hostName).append(this.hostIpAddress)
        .append(this.applicationName).append(this.instanceId).append(this.applicationLanguage)
        .append(this.fullyQualifiedOperationName).toHashCode();
  }

  /**
   * Builder for {@link SpanStructure}.
   */
  public static class Builder { // NOPMD

    private String landscapeToken;
    private long timestamp;
    private String hashCode;
    private String hostName;
    private String hostIpAddress;
    private String applicationName;
    private String instanceId;
    private String applicationLanguage;
    private String fqn;

    public Builder fromAvro(final net.explorviz.avro.SpanStructure avro) {
      this.timestamp = Instant
          .ofEpochMilli(avro.getTimestamp().getEpochMillisecondsWithNanoAdjust())
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

    public Builder withLandscapeToken(final String landscapeToken) {
      this.landscapeToken = landscapeToken;
      return this;
    }

    public Builder withTimestamp(final long timestamp) {
      this.timestamp = timestamp;
      return this;
    }

    public Builder withHashCode(final String hashCode) {
      this.hashCode = hashCode;
      return this;
    }

    public Builder withHostName(final String hostName) {
      this.hostName = hostName;
      return this;
    }

    public Builder withHostIpAddress(final String hostIpAddress) {
      this.hostIpAddress = hostIpAddress;
      return this;
    }

    public Builder withApplicationName(final String applicationName) {
      this.applicationName = applicationName;
      return this;
    }

    public Builder withInstanceId(final String instanceId) {
      this.instanceId = instanceId;
      return this;
    }

    public Builder withApplicationLanguage(final String applicationLanguage) {
      this.applicationLanguage = applicationLanguage;
      return this;
    }

    public Builder withFqn(final String fqn) {
      this.fqn = fqn;
      return this;
    }

    public SpanStructure build() {
      return new SpanStructure(this.landscapeToken, this.timestamp, this.hashCode, this.hostName,
          this.hostIpAddress, this.applicationName, this.instanceId, this.applicationLanguage,
          this.fqn);
    }

  }

}
