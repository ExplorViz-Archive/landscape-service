package net.explorviz.landscape.peristence;

/**
 * Thrown if a query could not be created or executed.
 */
public class QueryException extends Exception {
  public QueryException() {
  }

  public QueryException(String message) {
    super(message);
  }

  public QueryException(String message, Throwable cause) {
    super(message, cause);
  }

  public QueryException(Throwable cause) {
    super(cause);
  }

  public QueryException(String message, Throwable cause, boolean enableSuppression,
                        boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}

