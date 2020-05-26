package net.explorviz.landscape.peristence.cassandra.specifications;

import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.relation.Relation;
import net.explorviz.landscape.peristence.QueryException;
import net.explorviz.landscape.peristence.cassandra.CassandraSpecification;
import net.explorviz.landscape.peristence.cassandra.DBHelper;

/**
 * Query specification for finding records that fall between a pait timestamp bounds.
 */
public class FindRecordsBetweenTimestamps implements CassandraSpecification {

  private long from;
  private long to;

  private SimpleStatement statement;

  /**
   * Creats a new query to find all records discovered between to given timestamps
   * @param from the lower timestamp bound (inclusive)
   * @param to the upper timestamp bound (inclusive)
   */
  public FindRecordsBetweenTimestamps(long from, long to) {
    this.from = from;
    this.to = to;
    statement = QueryBuilder.selectFrom(DBHelper.KEYSPACE_NAME, DBHelper.RECORDS_TABLE_NAME)
        .all()
        .where(Relation.column(DBHelper.COL_TIMESTAMP).isGreaterThanOrEqualTo(QueryBuilder.literal(this.from)))
        .where(Relation.column(DBHelper.COL_TIMESTAMP).isLessThanOrEqualTo(QueryBuilder.literal(this.to)))
        .build();
  }

  @Override
  public String toQuery() throws QueryException {
    return statement.getQuery();
  }
}
