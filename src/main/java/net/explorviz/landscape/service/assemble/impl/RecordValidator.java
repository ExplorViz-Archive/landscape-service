package net.explorviz.landscape.service.assemble.impl;

import javax.enterprise.context.ApplicationScoped;
import net.explorviz.landscape.Application;
import net.explorviz.landscape.LandscapeRecord;
import net.explorviz.landscape.Node;

@ApplicationScoped
public class RecordValidator {

  public void validate(LandscapeRecord record) throws InvalidRecordException {
    validateToken(record.getLandscapeToken());
    validateNode(record.getNode());
    validateApplication(record.getApplication());
    validatePackage(record.getPackage$());
    validateClass(record.getClass$());
  }


  private void validateToken(String token) throws InvalidRecordException {
    if (token == null || token.length() == 0) {
      throw new InvalidRecordException("Record has no token");
    }
  }

  private void validateNode(Node node) throws InvalidRecordException {
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

  private void validateApplication(Application app) throws InvalidRecordException {
    if (app == null) {
      throw new InvalidRecordException("Record has no application");
    }

    if (app.getName() == null || app.getName().length() == 0) {
      throw new InvalidRecordException("Applications must have a name");
    }
    if (app.getPid() == null || app.getPid().length() == 0) {
      throw new InvalidRecordException("Applications must have a PID");
    }
    if (app.getLanguage() == null || app.getLanguage().length() == 0) {
      throw new InvalidRecordException("Applications must have a language");
    }
  }

  private void validatePackage(String pkg) throws InvalidRecordException {
    if (pkg == null || pkg.length() == 0) {
      throw new InvalidRecordException("A package is required");
    }
  }

  private void validateClass(String clazz) throws InvalidRecordException {
    if (clazz == null || clazz.length() == 0) {
      throw new InvalidRecordException("A class is required");
    }
  }

}
