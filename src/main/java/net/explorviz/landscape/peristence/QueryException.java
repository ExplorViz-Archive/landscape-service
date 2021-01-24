package net.explorviz.landscape.peristence;

/**
 * Thrown if a query could not be created or executed.
 */
public class QueryException extends Exception {

  private static final long serialVersionUID = 3092777195562183891L; // NOPMD

  public QueryException() {
    super();
  }

  public QueryException(final String message) {
    super(message);
  }

  public QueryException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public QueryException(final Throwable cause) {
    super(cause);
  }

  public QueryException(final String message, final Throwable cause,
      final boolean enableSuppression,
      final boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}

