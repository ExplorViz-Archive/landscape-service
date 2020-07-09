package net.explorviz.landscape.service.usecase;

import net.explorviz.landscape.model.Landscape;
import net.explorviz.landscape.peristence.QueryException;
import net.explorviz.landscape.service.assemble.LandscapeAssemblyException;

public interface UseCases {

  /**
   * Assembles the landscape with the given token using all known records.
   *
   * @param landscapeToken the token of the landscape to assemble
   * @return the landscape assembled out of all records associated to the given token
   */
  default Landscape buildLandscape(String landscapeToken)
      throws QueryException, LandscapeAssemblyException {
    return this.BuildLandscapeBetweeen(landscapeToken, 0, System.currentTimeMillis());
  }


  /**
   * Assembles the landscape with the given token using all known records that were discovered
   * at and after the given timestamp.
   *
   * @param landscapeToken the token of the landscape to assemble
   * @param fromTimestamp  the timestamp of the earliest record to factor into the landscape. All
   *                       records that were discovered at or after this timestamp are used to build
   *                       the landscape
   * @return the landscape assembled out of all records with the given token and matching the time
   *     constraint
   */
  default Landscape BuildLandscapeFrom(String landscapeToken, long fromTimestamp)
      throws QueryException, LandscapeAssemblyException {
    return this
        .BuildLandscapeBetweeen(landscapeToken, fromTimestamp, System.currentTimeMillis());
  }

  /**
   * Assembles the landscape with the given token using all known records that were discovered
   * at or before the given timestamp.
   *
   * @param landscapeToken the token of the landscape to assemble
   * @param toTimestamp    the timestamp of the latest record to factor into the landscape. All
   *                       records that were discovered after this timestamp are are ignored.
   * @return the landscape assembled out of all records with the given token and matching the time
   *     constraint
   */
  default Landscape BuildLandscapeTo(String landscapeToken, long toTimestamp)
      throws QueryException, LandscapeAssemblyException {
    return this.BuildLandscapeBetweeen(landscapeToken, 0, toTimestamp);
  }

  /**
   * Assembles the landscape with the given token using only such records that were discovered
   * between to timestamps.
   *
   * @param landscapeToken the token of the landscape to assemble
   * @param from           the timestamp of the earliest record to use. All records discovered
   *                       before this timestamp are ignored.
   * @param to             the timestamp of the latest record to use. All records discovered after
   *                       this timestamp are ignored.
   * @return the landscape assembled out of all records with the given token and matching the time
   *     constraint
   */
  Landscape BuildLandscapeBetweeen(String landscapeToken, long from, long to)
      throws LandscapeAssemblyException, QueryException;

}
