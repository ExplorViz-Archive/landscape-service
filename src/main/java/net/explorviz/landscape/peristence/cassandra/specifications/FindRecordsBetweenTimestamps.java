package net.explorviz.landscape.peristence.cassandra.specifications;

import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.relation.Relation;
import net.explorviz.landscape.peristence.QueryException;
import net.explorviz.landscape.peristence.cassandra.CassandraSpecification;
import net.explorviz.landscape.peristence.cassandra.DbHelper;

/**
 * Query specification for finding records that fall between a pait timestamp bounds.
 */
public class FindRecordsBetweenTimestamps implements CassandraSpecification {

  private final SimpleStatement statement;

  /**
   * Creats a new query to find all records discovered between to given timestamps.
   *
   * @param from the lower timestamp bound (inclusive)
   * @param to the upper timestamp bound (inclusive)
   */
  public FindRecordsBetweenTimestamps(final String token, final long from, final long to) {
    this.statement = QueryBuilder.selectFrom(DbHelper.KEYSPACE_NAME, DbHelper.RECORDS_TABLE_NAME)
        .all()
        .allowFiltering()
        .whereColumn(DbHelper.COL_TOKEN).isEqualTo(QueryBuilder.literal(token))
        .where(Relation.column(DbHelper.COL_TIMESTAMP)
            .isGreaterThanOrEqualTo(QueryBuilder.literal(from)))
        .where(Relation.column(DbHelper.COL_TIMESTAMP)
            .isLessThanOrEqualTo(QueryBuilder.literal(to)))
        .build();
  }

  @Override
  public String toQuery() throws QueryException {
    return this.statement.getQuery();
  }
}
