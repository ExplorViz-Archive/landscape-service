package net.explorviz.landscape.persistence.cassandra.dao;

import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Delete;
import com.datastax.oss.driver.api.mapper.annotations.Insert;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import com.datastax.oss.driver.api.mapper.annotations.Update;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import net.explorviz.landscape.persistence.model.SpanStructure;

/**
 * TODO.
 */
@Dao
public interface ReactiveSpanStructureDao {

  @Select
  Multi<SpanStructure> findByToken(String landscapeToken);

  @Select(customWhereClause = "landscape_token = :landscapeToken AND "
      + "timestamp >= :fromTs AND timestamp <= :toTs")
  Multi<SpanStructure> findBetweenInterval(String landscapeToken, long fromTs, long toTs);

  @Insert(ifNotExists = true)
  Uni<Void> insertAsync(SpanStructure structure);

  @Update(ifExists = true)
  Uni<Void> update(SpanStructure structure);

  @Delete(entityClass = SpanStructure.class)
  Uni<Void> deleteByTokenAsync(String landscapeToken);

}
