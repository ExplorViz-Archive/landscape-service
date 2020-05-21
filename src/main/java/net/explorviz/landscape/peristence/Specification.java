package net.explorviz.landscape.peristence;

/**
 * Specification to retrieve a specific subset of objects from a {@link Repository}.
 * Implementations provide the exact query.
 */
public interface Specification {

  /**
   * Creates a query that expresses the specification.
   *
   * @throws QueryException if the query could not be created
   */
  String toQuery() throws QueryException;

}
