package net.explorviz.landscape.peristence;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import net.explorviz.landscape.peristence.model.SpanStructure;

/**
 * Manages persistent access to a {@link SpanStructure} entities in
 * a reactive manner.
 */
public interface SpanStructureRepositoy {

  /**
   * Returns all for a given landscape token.
   */
  Multi<SpanStructure> getAll(String landscapeToken);

  /**
   * Get all for a given landscape token with timestamp in a given interval.
   *
   * @param landscapeToken the token
   * @param tsFrom         lower bound timestamp, in epoch millis
   * @param tsTo           upper bound timestamp, in epoch millis
   */
  Multi<SpanStructure> getBetween(String landscapeToken, long tsFrom, long tsTo);

  /**
   * Inserts an item into the repository.
   */
  Uni<Void> add(SpanStructure item);


  /**
   * Delete all records for a given token.
   *
   * @param token the landscape token
   */
  Uni<Void> deleteAll(String token);

}
