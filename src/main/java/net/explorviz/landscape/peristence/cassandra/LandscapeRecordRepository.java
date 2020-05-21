package net.explorviz.landscape.peristence.cassandra;

import java.util.List;
import net.explorviz.landscape.LandscapeRecord;
import net.explorviz.landscape.peristence.Repository;
import net.explorviz.landscape.peristence.Specification;

/**
 * Cassandra-backed repository to access and save {@link LandscapeRecord} entities.
 */
public class LandscapeRecordRepository implements Repository<LandscapeRecord> {

  @Override
  public List<LandscapeRecord> getAll() {
    return null;
  }

  @Override
  public void add(LandscapeRecord item) {

  }

  @Override
  public void remove(LandscapeRecord item) {

  }

  @Override
  public List<LandscapeRecord> query(Specification spec) {
    return null;
  }
}
