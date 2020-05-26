package net.explorviz.landscape.service.impl;

/**
 * Thrown if a records that is to be inserted into the model is invalid.
 */
public class InvalidRecordException extends Exception {
  public InvalidRecordException(String message) {
    super(message);
  }
}
