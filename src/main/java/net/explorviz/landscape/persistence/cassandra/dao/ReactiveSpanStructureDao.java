package net.explorviz.landscape.persistence.cassandra.dao;

import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Delete;
import com.datastax.oss.driver.api.mapper.annotations.Insert;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import com.datastax.oss.quarkus.runtime.api.reactive.mapper.MutinyMappedReactiveResultSet;
import io.smallrye.mutiny.Uni;
import net.explorviz.landscape.persistence.model.SpanStructure;

/**
 * TODO.
 */
@Dao
public interface ReactiveSpanStructureDao {

  @Select
  MutinyMappedReactiveResultSet<SpanStructure> findByToken(String landscapeToken);

  @Select(customWhereClause = "landscape_token = :landscapeToken and "
      + "timestamp >= :fromTs and timestamp <= :toTs", allowFiltering = true)
  MutinyMappedReactiveResultSet<SpanStructure> findBetweenInterval(String landscapeToken,
      long fromTs, long toTs);

  @Insert(ifNotExists = true)
  Uni<Void> insert(SpanStructure structure);

  @Delete(entityClass = SpanStructure.class,
      customWhereClause = "landscape_token = :landscapeToken")
  Uni<Void> deleteByToken(String landscapeToken);

}
