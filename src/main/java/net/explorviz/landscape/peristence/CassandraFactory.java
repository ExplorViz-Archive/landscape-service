package net.explorviz.landscape.peristence;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import java.net.InetSocketAddress;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

/**
 * Producer for preconfigured {@link CqlSession}.
 */
public class CassandraFactory {

  private CqlSession session;


  public CassandraFactory() {
    // TODO read from config values
    CqlSessionBuilder builder = CqlSession.builder();
    builder.addContactPoint(new InetSocketAddress("127.0.0.1", 9042));
    builder.withLocalDatacenter("local");
    this.session = builder.build();
  }

  /**
   * Return a ready for use {@link CqlSession}
   */
  @ApplicationScoped
  @Produces
  public CqlSession produceCqlSession() {
    return this.session;
  }


}
