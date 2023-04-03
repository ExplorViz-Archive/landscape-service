package net.explorviz.landscape.service.cassandra;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import net.explorviz.landscape.persistence.cassandra.dao.ReactiveSpanStructureDao;
import net.explorviz.landscape.persistence.model.SpanStructure;

/**
 * Service that leverages the reactive DAO {{@link ReactiveSpanStructureDao}}.
 */
@ApplicationScoped
public class ReactiveSpanStructureService {

  @Inject
  /* default */ Uni<ReactiveSpanStructureDao> spanStructureDao; // NOCS

  public Uni<Void> add(final SpanStructure spanStructure) {
    return this.spanStructureDao.flatMap(dao -> dao.insertAsync(spanStructure));
  }

  public Uni<Void> update(final SpanStructure spanStructure) {
    return this.spanStructureDao.flatMap(dao -> dao.update(spanStructure));
  }

  public Multi<SpanStructure> findBetweenInterval(final String landscapeToken, final long fromTs,
      final long toTs) {
    return this.spanStructureDao.toMulti()
        .flatMap(dao -> dao.findBetweenInterval(landscapeToken, fromTs, toTs));
  }

  public Multi<SpanStructure> findByToken(final String landscapeToken) {
    return this.spanStructureDao.toMulti().flatMap(dao -> dao.findByToken(landscapeToken));
  }

  public Uni<Void> deleteByToken(final String landscapeToken) {
    return this.spanStructureDao.flatMap(dao -> dao.deleteByTokenAsync(landscapeToken));
  }
}
