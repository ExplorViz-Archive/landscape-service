package net.explorviz.landscape.service.assemble.impl;

import net.explorviz.landscape.service.assemble.LandscapeAssemblyException;

/**
 * Thrown if trying to generate a landscape out of 0 records.
 */
public class NoRecordsException extends LandscapeAssemblyException {

  public NoRecordsException() {
    this("At least one records must be given");
  }

  private NoRecordsException(final String message) {
    super(message);
  }
}
