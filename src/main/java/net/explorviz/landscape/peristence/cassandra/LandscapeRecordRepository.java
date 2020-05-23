package net.explorviz.landscape.peristence.cassandra;

import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Statement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import java.util.List;
import net.explorviz.landscape.LandscapeRecord;
import net.explorviz.landscape.peristence.QueryException;
import net.explorviz.landscape.peristence.Repository;
import net.explorviz.landscape.peristence.Specification;
import net.explorviz.landscape.peristence.cassandra.mapper.LandscapeRecordMapper;
import net.explorviz.landscape.peristence.cassandra.mapper.ValueMapper;
import net.explorviz.landscape.peristence.cassandra.specifications.InsertLandscapeRecord;

/**
 * Cassandra-backed repository to access and save {@link LandscapeRecord} entities.
 */
public class LandscapeRecordRepository implements Repository<LandscapeRecord> {


  private DBHelper db;
  private ValueMapper<LandscapeRecord> mapper;

  /**
   * Create a new repository for accessing {@link LandscapeRecord} object.
   *
   * @param db the backing Casandra db
   */
  public LandscapeRecordRepository(DBHelper db, ValueMapper<LandscapeRecord> mapper) {
    this.db = db;
    this.mapper = mapper;
  }

  @Override
  public List<LandscapeRecord> getAll(String token) {
    String getAll =
        QueryBuilder.selectFrom(DBHelper.KEYSPACE_NAME, DBHelper.RECORDS_TABLE_NAME)
            .all()
            .whereColumn(DBHelper.COL_TOKEN)
            .isEqualTo(QueryBuilder.literal(token))
            .asCql();
    ResultSet result = db.getSession().execute(getAll);
    return result.map(r -> mapper.fromRow(r)).all();
  }

  @Override
  public void add(LandscapeRecord item) throws QueryException {
    InsertLandscapeRecord insertSpecification = new InsertLandscapeRecord(item, mapper);
    query(insertSpecification);
  }


  @Override
  public List<LandscapeRecord> query(Specification spec) throws QueryException {
    return db.getSession().execute(spec.toQuery()).map(r -> mapper.fromRow(r)).all();
  }
}
