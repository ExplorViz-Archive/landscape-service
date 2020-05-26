package net.explorviz.landscape.service.assemble.impl;

/**
 * Thrown if a records that is to be inserted into the model is invalid.
 */
public class InvalidRecordException extends Exception {
  public InvalidRecordException(String message) {
    super(message);
  }
}
