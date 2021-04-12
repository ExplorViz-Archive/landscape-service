package net.explorviz.landscape.service;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import net.explorviz.avro.landscape.model.Landscape;
import net.explorviz.landscape.peristence.model.SpanStructure;
import net.explorviz.landscape.service.assemble.LandscapeAssemblyException;

/**
 * Service for building landscape graphs out of a set of flat records.
 */
public interface ReactiveLandscapeService {

  /**
   * Assembles the landscape with the given token using all known records.
   *
   * @param landscapeToken the token of the landscape to assemble
   * @return the landscape assembled out of all records associated to the given token
   */
  default Uni<Landscape> buildLandscape(final String landscapeToken)
      throws LandscapeAssemblyException {
    return this.buildLandscapeBetween(landscapeToken, 0, System.currentTimeMillis());
  }


  /**
   * Assembles the landscape with the given token using all known records that were discovered at
   * and after the given timestamp.
   *
   * @param landscapeToken the token of the landscape to assemble
   * @param fromTimestamp the timestamp of the earliest record to factor into the landscape. All
   *        records that were discovered at or after this timestamp are used to build the landscape
   * @return the landscape assembled out of all records with the given token and matching the time
   *         constraint
   */
  default Uni<Landscape> buildLandscapeFrom(final String landscapeToken, final long fromTimestamp)
      throws LandscapeAssemblyException {
    return this
        .buildLandscapeBetween(landscapeToken, fromTimestamp, System.currentTimeMillis());
  }

  /**
   * Assembles the landscape with the given token using all known records that were discovered at or
   * before the given timestamp.
   *
   * @param landscapeToken the token of the landscape to assemble
   * @param toTimestamp the timestamp of the latest record to factor into the landscape. All records
   *        that were discovered after this timestamp are are ignored.
   * @return the landscape assembled out of all records with the given token and matching the time
   *         constraint
   */
  default Uni<Landscape> buildLandscapeTo(final String landscapeToken, final long toTimestamp)
      throws LandscapeAssemblyException {
    return this.buildLandscapeBetween(landscapeToken, 0, toTimestamp);
  }

  /**
   * Assembles the landscape with the given token using only such records that were discovered
   * between to timestamps.
   *
   * @param landscapeToken the token of the landscape to assemble
   * @param from the timestamp of the earliest record to use. All records discovered before this
   *        timestamp are ignored.
   * @param to the timestamp of the latest record to use. All records discovered after this
   *        timestamp are ignored.
   * @return the landscape assembled out of all records with the given token and matching the time
   *         constraint
   */
  Uni<Landscape> buildLandscapeBetween(String landscapeToken, long from, long to)
      throws LandscapeAssemblyException;


  /**
   * Deletes the complete landscape with the given token, if it exists. If there is no landscape
   * associated to the given token, this operation does nothing.
   *
   * @param landscapeToken the token of the landscape to delete
   * @return
   */
  Uni<Void> deleteLandscape(String landscapeToken);

  Multi<SpanStructure> cloneLandscape(String landscapeToken, String clonedLandscapeToken);

}
