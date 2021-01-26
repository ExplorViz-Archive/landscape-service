package net.explorviz.landscape.peristence.cassandra.mapper;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.querybuilder.term.Term;
import java.util.Map;

/**
 * Base class for all Cassandra row mappers.
 *
 * @param <T> the type to be mapped
 */
public interface ValueMapper<T> {

  Map<String, Term> toMap(T item);

  T fromRow(Row row);

}
