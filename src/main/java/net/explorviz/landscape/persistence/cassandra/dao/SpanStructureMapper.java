package net.explorviz.landscape.persistence.cassandra.dao;

import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;
import io.smallrye.mutiny.Uni;

/**
 * TODO.
 */
@Mapper
public interface SpanStructureMapper {

  @DaoFactory
  ReactiveSpanStructureDao spanStructureDaoReactiveSync();

  @DaoFactory
  Uni<ReactiveSpanStructureDao> spanStructureDaoReactiveUni();

}
