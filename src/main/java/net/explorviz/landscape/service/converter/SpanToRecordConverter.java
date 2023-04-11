package net.explorviz.landscape.service.converter;

import java.util.Arrays;
import javax.enterprise.context.ApplicationScoped;
import net.explorviz.avro.landscape.flat.Application;
import net.explorviz.avro.landscape.flat.LandscapeRecord;
import net.explorviz.avro.landscape.flat.Node;
import net.explorviz.landscape.persistence.model.SpanStructure;

/**
 * Maps {@link SpanStructure} objects to {@link LandscapeRecord} objects by extracting the data.
 */
@ApplicationScoped
public class SpanToRecordConverter {

  /**
   * Converts a {@link SpanStructure} to a {@link LandscapeRecord} using the structural information
   * given in the span.
   *
   * @param span the span
   * @return a records containing the structural information of the span
   */
  public LandscapeRecord toRecord(final SpanStructure span) {

    // Create new builder
    final LandscapeRecord.Builder recordBuilder =
        LandscapeRecord.newBuilder().setLandscapeToken(span.getLandscapeToken());

    // Use start time as timestamp
    final long timestamp = span.getTimestamp();

    // set hash code
    recordBuilder.setHashCode(span.getHashCode());

    // Set node and application
    recordBuilder.setTimestamp(timestamp)
        .setNode(new Node(span.getHostIpAddress(), span.getHostName())).setApplication(
            new Application(span.getApplicationName(), span.getInstanceId(),
                span.getApplicationLanguage()));

    /*
     * By definition getFullyQualifiedOperationName().split("."): Last entry is method name, next to
     * last is class name, remaining elements form the package name
     */
    final String[] operationFqnSplit = span.getFullyQualifiedOperationName().split("\\.");

    final String pkgName =
        String.join(".", Arrays.copyOf(operationFqnSplit, operationFqnSplit.length - 2));
    final String className = operationFqnSplit[operationFqnSplit.length - 2];
    final String methodName = operationFqnSplit[operationFqnSplit.length - 1];

    recordBuilder.setPackage$(pkgName);
    recordBuilder.setClass$(className);
    recordBuilder.setMethod(methodName);

    return recordBuilder.build();

  }

}
