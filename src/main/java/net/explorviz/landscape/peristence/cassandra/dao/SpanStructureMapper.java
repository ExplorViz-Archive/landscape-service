package net.explorviz.landscape.peristence.cassandra.dao;

import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;

@Mapper
public interface SpanStructureMapper {


  @DaoFactory
  ReactiveSpanStructureDao reactiveSpanStructureDao();

}
