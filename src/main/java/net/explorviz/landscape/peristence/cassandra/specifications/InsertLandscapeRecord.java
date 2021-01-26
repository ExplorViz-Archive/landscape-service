package net.explorviz.landscape.peristence.cassandra.specifications;

import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import net.explorviz.avro.landscape.flat.LandscapeRecord;
import net.explorviz.landscape.peristence.QueryException;
import net.explorviz.landscape.peristence.cassandra.CassandraSpecification;
import net.explorviz.landscape.peristence.cassandra.DbHelper;
import net.explorviz.landscape.peristence.cassandra.mapper.ValueMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Specification to insert a {@link LandscapeRecord} into the database.
 */
public class InsertLandscapeRecord implements CassandraSpecification {

  private static final Logger LOGGER = LoggerFactory.getLogger(InsertLandscapeRecord.class);

  private final LandscapeRecord record;
  private SimpleStatement statement;
  private final ValueMapper<LandscapeRecord> mapper;


  /**
   * Create a new insertion specification for a given record. Sanitizes the input beforehand.
   *
   * @param record the record to insert
   */
  public InsertLandscapeRecord(final LandscapeRecord record,
      final ValueMapper<LandscapeRecord> mapper) {
    this.record = record;
    this.mapper = mapper;
  }

  private void sanitize() throws QueryException {
    if (this.record.getLandscapeToken() == null || this.record.getLandscapeToken().isEmpty()) {
      throw new QueryException("Given record has no landscape token assigned");
    }
    if (this.record.getTimestamp() <= 0) {
      this.record.setTimestamp(System.currentTimeMillis());
      if (LOGGER.isWarnEnabled()) {
        LOGGER.warn("Record to insert had no timestamp assigned, setting timestamp to now ({})",
            this.record.getTimestamp());
      }
    }
  }

  /**
   * Creates and returns the statement.
   *
   * @throws JsonProcessingException if the record could not be serialized into json
   */
  private SimpleStatement createStatement() throws JsonProcessingException {

    return QueryBuilder.insertInto(DbHelper.KEYSPACE_NAME, DbHelper.RECORDS_TABLE_NAME)
        .values(this.mapper.toMap(this.record))
        .build();
  }

  @Override
  public String toQuery() throws QueryException {
    this.sanitize();
    // Initialize lazy
    if (this.statement == null) {
      try {
        this.statement = this.createStatement();
      } catch (final JsonProcessingException e) {
        throw new QueryException("Could not serialize entity to JSON", e);
      }
    }
    return this.statement.getQuery();
  }
}
