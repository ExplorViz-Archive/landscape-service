package net.explorviz.landscape.kafka;

import io.quarkus.test.Mock;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import net.explorviz.landscape.persistence.SpanStructureRepositoy;
import net.explorviz.landscape.persistence.model.SpanStructure;

@Mock
@ApplicationScoped
public class MockSpanStructureRepositoryImpl implements SpanStructureRepositoy {

  private final List<SpanStructure> spanStructures = new ArrayList<>();

  @Override
  public Multi<SpanStructure> getAll(String landscapeToken) {

    final List<SpanStructure> spanStructuresWithToken = this.spanStructures.stream()
        .filter(spanStructure -> spanStructure.getLandscapeToken().equals(landscapeToken))
        .collect(Collectors.toList());

    return Multi.createFrom().iterable(spanStructuresWithToken);
  }

  @Override
  public Multi<SpanStructure> getBetween(String landscapeToken, long tsFrom, long tsTo) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Uni<Void> add(SpanStructure item) {
    spanStructures.add(item);
    return Uni.createFrom().voidItem();
  }

  @Override
  public Uni<Void> deleteAll(String token) {
    // TODO Auto-generated method stub
    return null;
  }

}
