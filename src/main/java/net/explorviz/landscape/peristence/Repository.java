package net.explorviz.landscape.peristence;

import java.util.List;

/**
 * Manages (usually persistent) access to a collection of objects.
 *
 * @param <T> type of objects the repository manages.
 */
public interface Repository<T> {

  /**
   * Returns all objects in the collection for a given landscape token.
   */
  List<T> getAll(String landscapeToken);

  /**
   * Inserts an item into the repository
   */
  void add(T item) throws QueryException;

  /**
   * Queries the collection for a specific subset
   *
   * @param spec the specification of the items to retrieve
   * @return a (possibly empty) list containing the queried items
   * @throws QueryException if the query could not be executed
   */
  List<T> query(Specification spec) throws QueryException;



}
