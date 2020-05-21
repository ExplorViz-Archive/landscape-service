package net.explorviz.landscape.peristence.cassandra.mapper;

import com.datastax.oss.driver.api.core.type.codec.registry.CodecRegistry;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.term.Term;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import net.explorviz.landscape.LandscapeRecord;
import net.explorviz.landscape.peristence.cassandra.CassandraDB;

@ApplicationScoped
public class LandscapeRecordMapper implements ValueMapper<LandscapeRecord> {

  private CodecRegistry codecRegistry;

  @Inject
  public LandscapeRecordMapper(CassandraDB db) {
    this.codecRegistry = db.getCodecRegistry();
  }

  @Override
  public Map<String, Term> toMap(LandscapeRecord item) {
    Map<String, Term> map = new HashMap<>();
    map.put(CassandraDB.COL_ID, QueryBuilder.literal(item.getId()));
    map.put(CassandraDB.COL_NODE, QueryBuilder.literal(item.getNode(), codecRegistry));
    map.put(CassandraDB.COL_APPLICATION, QueryBuilder.literal(item.getApplication(), codecRegistry));
    map.put(CassandraDB.COL_PACKAGE, QueryBuilder.literal(item.getPackage$()));
    map.put(CassandraDB.COL_CLASS, QueryBuilder.literal(item.getClass$()));
    map.put(CassandraDB.COL_METHOD, QueryBuilder.literal(item.getMethod()));
    return map;
  }

  @Override
  public LandscapeRecord fromMap(Map<String, Term> map) {
    return null;
  }
}
