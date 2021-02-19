package net.explorviz.landscape.peristence.cassandra.dao;

import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Insert;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import com.datastax.oss.quarkus.runtime.api.reactive.mapper.MutinyMappedReactiveResultSet;
import io.smallrye.mutiny.Uni;
import net.explorviz.landscape.peristence.model.SpanStructure;

@Dao
public interface ReactiveSpanStructureDao {

  @Select
  MutinyMappedReactiveResultSet<SpanStructure> findByToken(String landscapeToken);

  @Insert
  Uni<Void> insert(SpanStructure structure);

}
