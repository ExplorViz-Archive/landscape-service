package net.explorviz.landscape.peristence.cassandra.specifications;

import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import javax.inject.Inject;
import net.explorviz.landscape.LandscapeRecord;
import net.explorviz.landscape.peristence.QueryException;
import net.explorviz.landscape.peristence.cassandra.CassandraDB;
import net.explorviz.landscape.peristence.cassandra.CassandraSpecification;
import net.explorviz.landscape.peristence.cassandra.mapper.LandscapeRecordMapper;
import net.explorviz.landscape.peristence.cassandra.mapper.ValueMapper;

/**
 * Specification to insert a {@link LandscapeRecord} into the datasbe
 */
public class InsertLandscapeRecord implements CassandraSpecification {

  private LandscapeRecord record;
  private SimpleStatement statement;
  private ValueMapper<LandscapeRecord> mapper;


  /**
   * Create a new insertion specification for a given record
   *
   * @param record the record to insert
   */
  public InsertLandscapeRecord(LandscapeRecord record, ValueMapper<LandscapeRecord> mapper) {
    this.record = record;
    this.mapper = mapper;
  }

  /**
   * Creates and returns the statement.
   *
   * @throws JsonProcessingException if the record could not be serialized into json
   */
  private SimpleStatement createStatement() throws JsonProcessingException {

    return QueryBuilder.insertInto(CassandraDB.KEYSPACE_NAME, CassandraDB.RECORDS_TABLE_NAME)
        .values(mapper.toMap(this.record))
        .build();
  }

  @Override
  public String toQuery() throws QueryException {
    // Initialize lazy
    if (this.statement == null) {
      try {
        this.statement = createStatement();
      } catch (JsonProcessingException e) {
        throw new QueryException("Could not serialize entity to JSON", e);
      }
    }
    return this.statement.getQuery();
  }
}
