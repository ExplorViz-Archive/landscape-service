package net.explorviz.landscape.peristence.cassandra.dao;

import com.datastax.oss.quarkus.runtime.api.session.QuarkusCqlSession;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Produces;

/**
 * TODO.
 */
@ApplicationScoped
public class SpanStructureDaoProducer {

  private final ReactiveSpanStructureDao reactiveSpanStructureDao;

  @Inject
  public SpanStructureDaoProducer(final QuarkusCqlSession session) {
    final SpanStructureMapper mapper = new SpanStructureMapperBuilder(session).build();
    this.reactiveSpanStructureDao = mapper.reactiveSpanStructureDao();
  }

  @Produces
  @ApplicationScoped
  public ReactiveSpanStructureDao produceReactiveSpanStructureDao() {
    return this.reactiveSpanStructureDao;
  }

}
