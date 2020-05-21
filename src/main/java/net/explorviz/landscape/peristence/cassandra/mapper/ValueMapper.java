package net.explorviz.landscape.peristence.cassandra.mapper;

import com.datastax.oss.driver.api.querybuilder.term.Term;
import java.util.Map;

public interface ValueMapper<T> {

  Map<String, Term> toMap(T item);

  T fromMap(Map<String, Term> map);

}
