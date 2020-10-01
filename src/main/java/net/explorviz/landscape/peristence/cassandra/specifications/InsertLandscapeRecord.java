package net.explorviz.landscape.peristence.cassandra.specifications;

import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import net.explorviz.avro.landscape.flat.LandscapeRecord;
import net.explorviz.landscape.peristence.QueryException;
import net.explorviz.landscape.peristence.cassandra.CassandraSpecification;
import net.explorviz.landscape.peristence.cassandra.DBHelper;
import net.explorviz.landscape.peristence.cassandra.mapper.ValueMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Specification to insert a {@link LandscapeRecord} into the datasbe
 */
public class InsertLandscapeRecord implements CassandraSpecification {

  private static final Logger LOGGER = LoggerFactory.getLogger(InsertLandscapeRecord.class);

  private final LandscapeRecord record;
  private SimpleStatement statement;
  private final ValueMapper<LandscapeRecord> mapper;


  /**
   * Create a new insertion specification for a given record.
   * Sanitizes the input beforehand.
   *
   * @param record the record to insert
   */
  public InsertLandscapeRecord(LandscapeRecord record, ValueMapper<LandscapeRecord> mapper) {
    this.record = record;
    this.mapper = mapper;
  }

  /**
   *
   */
  private void sanitize() throws QueryException {
    if (record.getLandscapeToken() == null || record.getLandscapeToken().isEmpty()) {
      throw new QueryException("Given record has no landscape token assigned");
    }
    if (record.getTimestamp() <= 0) {
      record.setTimestamp(System.currentTimeMillis());
      if (LOGGER.isWarnEnabled()) {
        LOGGER.warn("Record to insert had no timestamp assigned, setting timestamp to now ({})",
            record.getTimestamp());
      }
    }
  }

  /**
   * Creates and returns the statement.
   *
   * @throws JsonProcessingException if the record could not be serialized into json
   */
  private SimpleStatement createStatement() throws JsonProcessingException {

    return QueryBuilder.insertInto(DBHelper.KEYSPACE_NAME, DBHelper.RECORDS_TABLE_NAME)
        .values(mapper.toMap(this.record))
        .build();
  }

  @Override
  public String toQuery() throws QueryException {
    sanitize();
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
