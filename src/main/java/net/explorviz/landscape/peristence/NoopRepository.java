package net.explorviz.landscape.peristence;

import io.quarkus.arc.AlternativePriority;
import java.util.Collections;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import net.explorviz.avro.landscape.flat.LandscapeRecord;

@ApplicationScoped
@AlternativePriority(100)
public class NoopRepository implements Repository<LandscapeRecord> {
  @Override
  public List<LandscapeRecord> getAll(final String landscapeToken) {
    return Collections.emptyList();
  }

  @Override
  public void add(final LandscapeRecord item) {

  }

  @Override
  public List<LandscapeRecord> query(final Specification spec){
    return Collections.emptyList();
  }
}
