package net.explorviz.landscape.service;

/**
 * Thrown if a landscape could not be assembled
 */
public class LandscapeAssemblyException extends Exception {

  public LandscapeAssemblyException() {
  }

  public LandscapeAssemblyException(String message) {
    super(message);
  }

  public LandscapeAssemblyException(String message, Throwable cause) {
    super(message, cause);
  }

  public LandscapeAssemblyException(Throwable cause) {
    super(cause);
  }

  public LandscapeAssemblyException(String message, Throwable cause, boolean enableSuppression,
                                    boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
