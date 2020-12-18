package net.explorviz.landscape.peristence;

import com.datastax.oss.driver.api.core.cql.AsyncResultSet;
import java.util.List;
import java.util.concurrent.CompletionStage;

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
   * Inserts an item into the repository.
   */
  void add(T item) throws QueryException;

  /**
   * Inserts an item into the repository asynchronously.
   * @return
   */
  CompletionStage<AsyncResultSet> addAsync(T item) throws QueryException;

  /**
   * Queries the collection for a specific subset
   *
   * @param spec the specification of the items to retrieve
   * @return a (possibly empty) list containing the queried items
   * @throws QueryException if the query could not be executed
   */
  List<T> query(Specification spec) throws QueryException;



}
