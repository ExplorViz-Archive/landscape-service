package net.explorviz.landscape.service.assemble;

/**
 * Thrown if a landscape could not be assembled.
 */
public class LandscapeAssemblyException extends Exception {

  public LandscapeAssemblyException() {}

  public LandscapeAssemblyException(final String message) {
    super(message);
  }

  public LandscapeAssemblyException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public LandscapeAssemblyException(final Throwable cause) {
    super(cause);
  }

  public LandscapeAssemblyException(final String message, final Throwable cause,
      final boolean enableSuppression,
      final boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
