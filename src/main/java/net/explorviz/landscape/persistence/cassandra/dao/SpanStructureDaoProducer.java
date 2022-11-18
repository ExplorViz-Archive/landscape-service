package net.explorviz.landscape.persistence.cassandra.dao;

import com.datastax.oss.quarkus.runtime.api.session.QuarkusCqlSession;
import io.smallrye.mutiny.Uni;
import java.time.Duration;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Produces;

/**
 * TODO.
 */
@ApplicationScoped
public class SpanStructureDaoProducer {

  private static final long CQL_TIMEOUT_SECONDS = 5;

  private final ReactiveSpanStructureDao reactiveSpanStructureDao;

  @Inject
  public SpanStructureDaoProducer(final Uni<QuarkusCqlSession> session) {
    final SpanStructureMapper mapper = new SpanStructureMapperBuilder(session.await().atMost(
        Duration.ofSeconds(CQL_TIMEOUT_SECONDS))).build();
    this.reactiveSpanStructureDao = mapper.reactiveSpanStructureDao();
  }

  @Produces
  @ApplicationScoped
  public ReactiveSpanStructureDao produceReactiveSpanStructureDao() {
    return this.reactiveSpanStructureDao;
  }

}
