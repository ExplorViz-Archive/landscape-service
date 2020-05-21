package net.explorviz.landscape.peristence.cassandra.mapper;

import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.datastax.oss.driver.api.core.type.codec.MappingCodec;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import com.datastax.oss.driver.internal.core.type.DefaultUserDefinedType;
import edu.umd.cs.findbugs.annotations.Nullable;
import net.explorviz.landscape.Node;
import net.explorviz.landscape.peristence.cassandra.CassandraDB;

public class NodeCodec extends MappingCodec<UdtValue, Node> {

  public NodeCodec(TypeCodec<UdtValue> innerCodec) {
    super(innerCodec, GenericType.of(Node.class));
  }

  @Nullable
  @Override
  protected Node innerToOuter(@Nullable UdtValue value) {
    String name = value.getString(CassandraDB.COL_NODE_NAME);
    String ip = value.getString(CassandraDB.COL_NODE_IP_ADDRESS);
    return new Node(ip, name);
  }

  @Nullable
  @Override
  protected UdtValue outerToInner(@Nullable Node value) {
    UdtValue udtValue = ((UserDefinedType) getCqlType()).newValue();
    udtValue.setString(CassandraDB.COL_NODE_NAME, value.getHostName());
    udtValue.setString(CassandraDB.COL_NODE_IP_ADDRESS, value.getIpAddress());
    return udtValue;
  }
}
