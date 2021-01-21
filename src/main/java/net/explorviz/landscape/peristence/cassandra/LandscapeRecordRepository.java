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


  private final DbHelper db;
  private final ValueMapper<LandscapeRecord> mapper;

  /**
   * Create a new repository for accessing {@link LandscapeRecord} object.
   *
   * @param db the backing Casandra db
   */
  public LandscapeRecordRepository(final DbHelper db, final ValueMapper<LandscapeRecord> mapper) {
    this.db = db;
    db.initialize();
    this.mapper = mapper;
  }

  @Override
  public List<LandscapeRecord> getAll(final String token) {
    final String getAll =
        QueryBuilder.selectFrom(DbHelper.KEYSPACE_NAME, DbHelper.RECORDS_TABLE_NAME)
            .all()
            .whereColumn(DbHelper.COL_TOKEN)
            .isEqualTo(QueryBuilder.literal(token))
            .asCql();
    final ResultSet result = this.db.getSession().execute(getAll);
    return result.map(this.mapper::fromRow).all();
  }

  @Override
  public void add(final LandscapeRecord item) throws QueryException {
    final InsertLandscapeRecord insertSpecification = new InsertLandscapeRecord(item, this.mapper);
    this.query(insertSpecification);
  }

  @Override
  public CompletionStage<AsyncResultSet> addAsync(final LandscapeRecord item)
      throws QueryException {
    final InsertLandscapeRecord insertSpecification = new InsertLandscapeRecord(item, this.mapper);
    return this.db.getSession().executeAsync(insertSpecification.toQuery());
  }

  @Override
  public List<LandscapeRecord> query(final Specification spec) throws QueryException {
    return this.db.getSession().execute(spec.toQuery()).map(this.mapper::fromRow).all();
  }

  @Override
  public void deleteAll(final String token) {
    final String deletionQuery =
        QueryBuilder.deleteFrom(DbHelper.KEYSPACE_NAME, DbHelper.RECORDS_TABLE_NAME)
            .whereColumn(DbHelper.COL_TOKEN).isEqualTo(QueryBuilder.literal(token)).asCql();
    this.db.getSession().execute(deletionQuery);
  }



}
