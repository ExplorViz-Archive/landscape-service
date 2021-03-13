package net.explorviz.landscape.peristence.cassandra;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import net.explorviz.landscape.peristence.SpanStructureRepositoy;
import net.explorviz.landscape.peristence.cassandra.dao.ReactiveSpanStructureDao;
import net.explorviz.landscape.peristence.model.SpanStructure;

/**
 * TODO.
 */
@ApplicationScoped
public class ReactiveSpanStructureRepositoryImpl implements SpanStructureRepositoy {


  private final ReactiveSpanStructureDao dao;

  @Inject
  public ReactiveSpanStructureRepositoryImpl(
      final ReactiveSpanStructureDao dao) {
    this.dao = dao;

  }

  @Override
  public Multi<SpanStructure> getAll(final String landscapeToken) {
    return this.dao.findByToken(landscapeToken);
  }

  @Override
  public Multi<SpanStructure> getBetween(final String landscapeToken, final long tsFrom,
      final long tsTo) {
    return this.dao.findBetweenInterval(landscapeToken, tsFrom, tsTo);
  }

  @Override
  public Uni<Void> add(final SpanStructure item) {
    // TODO: Validate?
    return this.dao.insert(item);
  }

  @Override
  public Uni<Void> deleteAll(final String token) {
    return this.dao.deleteByToken(token);
  }
}
