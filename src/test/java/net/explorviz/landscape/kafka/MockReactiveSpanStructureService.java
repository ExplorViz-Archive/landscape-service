package net.explorviz.landscape.kafka;

import com.google.errorprone.annotations.ForOverride;
import io.quarkus.test.Mock;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import net.explorviz.landscape.persistence.model.SpanStructure;
import net.explorviz.landscape.service.cassandra.ReactiveLandscapeService;
import net.explorviz.landscape.service.cassandra.ReactiveSpanStructureService;

@Mock
@ApplicationScoped
public class MockSpanStructureRepositoryImpl extends ReactiveSpanStructureService {

  private final List<SpanStructure> spanStructures = new ArrayList<>();

  @Override
  public Multi<SpanStructure> findByToken(String landscapeToken) {
    final List<SpanStructure> spanStructuresWithToken = this.spanStructures.stream()
        .filter(spanStructure -> spanStructure.getLandscapeToken().equals(landscapeToken))
        .collect(Collectors.toList());

    return Multi.createFrom().iterable(spanStructuresWithToken);
  }

  @Override
  public Multi<SpanStructure> findBetweenInterval(String landscapeToken, long fromTs,
      long toTs) {
    return null;
  }

  @Override
  public Uni<Void> add(SpanStructure spanStructure) {
    spanStructures.add(spanStructure);
    return Uni.createFrom().voidItem();
  }

  @Override
  public Uni<Void> deleteByToken(String landscapeToken) {
    return null;
  }
}
