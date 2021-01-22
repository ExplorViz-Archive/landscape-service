package net.explorviz.landscape.peristence.cassandra.mapper;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.type.codec.registry.CodecRegistry;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.term.Term;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import net.explorviz.avro.landscape.flat.Application;
import net.explorviz.avro.landscape.flat.LandscapeRecord;
import net.explorviz.avro.landscape.flat.Node;
import net.explorviz.landscape.peristence.cassandra.DbHelper;

/**
 * De/serializes {@link LandscapeRecord}s to/from Apache Cassandra rows.
 */
@ApplicationScoped
public class LandscapeRecordMapper implements ValueMapper<LandscapeRecord> {

  private final CodecRegistry codecRegistry;

  @Inject
  public LandscapeRecordMapper(final DbHelper db) {
    this.codecRegistry = db.getCodecRegistry();
  }

  @Override
  public Map<String, Term> toMap(final LandscapeRecord item) {
    final Map<String, Term> map = new HashMap<>();
    map.put(DbHelper.COL_TOKEN, QueryBuilder.literal(item.getLandscapeToken()));
    map.put(DbHelper.COL_TIMESTAMP, QueryBuilder.literal(item.getTimestamp()));
    map.put(DbHelper.COL_NODE, QueryBuilder.literal(item.getNode(), this.codecRegistry));
    map.put(DbHelper.COL_APPLICATION,
        QueryBuilder.literal(item.getApplication(), this.codecRegistry));
    map.put(DbHelper.COL_PACKAGE, QueryBuilder.literal(item.getPackage$()));
    map.put(DbHelper.COL_CLASS, QueryBuilder.literal(item.getClass$()));
    map.put(DbHelper.COL_METHOD, QueryBuilder.literal(item.getMethod()));
    map.put(DbHelper.COL_HASHCODE, QueryBuilder.literal(item.getHashCode()));
    return map;
  }

  @Override
  public LandscapeRecord fromRow(final Row row) {

    return LandscapeRecord.newBuilder()
        .setLandscapeToken(row.getString(DbHelper.COL_TOKEN))
        .setTimestamp(row.getLong(DbHelper.COL_TIMESTAMP))
        .setNode(row.get(DbHelper.COL_NODE, Node.class))
        .setApplication(row.get(DbHelper.COL_APPLICATION, Application.class))
        .setPackage$(row.getString(DbHelper.COL_PACKAGE))
        .setClass$(row.getString(DbHelper.COL_CLASS))
        .setMethod(row.getString(DbHelper.COL_METHOD))
        .setHashCode(row.getString(DbHelper.COL_HASHCODE))
        .build();

  }
}
