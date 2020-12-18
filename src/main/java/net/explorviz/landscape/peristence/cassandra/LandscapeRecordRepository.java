package net.explorviz.landscape.peristence.cassandra;

import com.datastax.oss.driver.api.core.cql.AsyncResultSet;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import java.util.List;
import java.util.concurrent.CompletionStage;
import javax.enterprise.context.ApplicationScoped;
import net.explorviz.avro.landscape.flat.LandscapeRecord;
import net.explorviz.landscape.peristence.QueryException;
import net.explorviz.landscape.peristence.Repository;
import net.explorviz.landscape.peristence.Specification;
import net.explorviz.landscape.peristence.cassandra.mapper.ValueMapper;
import net.explorviz.landscape.peristence.cassandra.specifications.InsertLandscapeRecord;

/**
 * Cassandra-backed repository to access and save {@link LandscapeRecord} entities.
 */
@ApplicationScoped
public class LandscapeRecordRepository implements Repository<LandscapeRecord> {


  private final DBHelper db;
  private final ValueMapper<LandscapeRecord> mapper;

  /**
   * Create a new repository for accessing {@link LandscapeRecord} object.
   *
   * @param db the backing Casandra db
   */
  public LandscapeRecordRepository(DBHelper db, ValueMapper<LandscapeRecord> mapper) {
    this.db = db;
    db.initialize();
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
    return result.map(mapper::fromRow).all();
  }

  @Override
  public void add(LandscapeRecord item) throws QueryException {
    InsertLandscapeRecord insertSpecification = new InsertLandscapeRecord(item, mapper);
    query(insertSpecification);
  }

  @Override
  public CompletionStage<AsyncResultSet> addAsync(LandscapeRecord item) throws QueryException {
    InsertLandscapeRecord insertSpecification = new InsertLandscapeRecord(item, mapper);
    return db.getSession().executeAsync(insertSpecification.toQuery());
  }

  @Override
  public List<LandscapeRecord> query(Specification spec) throws QueryException {
    return db.getSession().execute(spec.toQuery()).map(mapper::fromRow).all();
  }
}
