package net.explorviz.landscape.service.assemble.impl;

import javax.enterprise.context.ApplicationScoped;
import net.explorviz.avro.landscape.flat.Application;
import net.explorviz.avro.landscape.flat.LandscapeRecord;
import net.explorviz.avro.landscape.flat.Node;

/**
 * Checks the integrity of landscape records.
 */
@ApplicationScoped
public class RecordValidator {

  public void validate(final LandscapeRecord record) {
    this.validateToken(record.getLandscapeToken());
    this.validateNode(record.getNode());
    this.validateApplication(record.getApplication());
    this.validatePackage(record.getPackage$());
    this.validateClass(record.getClass$());
  }

  private void validateToken(final String token) {
    if (token == null || token.length() == 0) {
      throw new InvalidRecordException("Record has no token");
    }
  }

  private void validateNode(final Node node) {
    if (node == null) {
      throw new InvalidRecordException("Record has no node");
    }

    if (node.getHostName() == null || node.getHostName().length() == 0) {
      throw new InvalidRecordException("Nodes must have a name");
    }
    if (node.getIpAddress() == null || node.getIpAddress().length() == 0) {
      throw new InvalidRecordException("Nodes must have an IP address");
    }
  }

  private void validateApplication(final Application app) {
    if (app == null) {
      throw new InvalidRecordException("Record has no application");
    }

    if (app.getName() == null || app.getName().length() == 0) {
      throw new InvalidRecordException("Applications must have a name");
    }
    if (app.getLanguage() == null || app.getLanguage().length() == 0) {
      throw new InvalidRecordException("Applications must have a language");
    }
  }

  private void validatePackage(final String pkg) {
    if (pkg == null || pkg.length() == 0) {
      throw new InvalidRecordException("A package is required");
    }
  }

  private void validateClass(final String clazz) {
    if (clazz == null || clazz.length() == 0) {
      throw new InvalidRecordException("A class is required");
    }
  }

}
