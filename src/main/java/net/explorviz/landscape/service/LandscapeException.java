package net.explorviz.landscape.service;

/**
 * Thrown if an error occurred during the landscape building process.
 */
public class LandscapeException extends Exception {
  public LandscapeException() {
  }

  public LandscapeException(String message) {
    super(message);
  }

  public LandscapeException(String message, Throwable cause) {
    super(message, cause);
  }

  public LandscapeException(Throwable cause) {
    super(cause);
  }

  public LandscapeException(String message, Throwable cause, boolean enableSuppression,
                            boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
