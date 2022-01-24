package net.explorviz.landscape.service;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import net.explorviz.avro.landscape.flat.LandscapeRecord;
import net.explorviz.avro.landscape.model.Landscape;
import net.explorviz.landscape.peristence.SpanStructureRepositoy;
import net.explorviz.landscape.peristence.model.SpanStructure;
import net.explorviz.landscape.service.assemble.LandscapeAssembler;
import net.explorviz.landscape.service.converter.SpanToRecordConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements the use cases.
 */
@ApplicationScoped
public class ReactiveLandscapeServiceImpl implements ReactiveLandscapeService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReactiveLandscapeServiceImpl.class);

  private final SpanStructureRepositoy repo;
  private final LandscapeAssembler assembler;
  private final SpanToRecordConverter converter;

  @Inject
  public ReactiveLandscapeServiceImpl(final SpanStructureRepositoy repo,
      final LandscapeAssembler assembler, final SpanToRecordConverter converter) {
    this.repo = repo;
    this.assembler = assembler;
    this.converter = converter;
  }

  @Override
  public Uni<Landscape> buildLandscapeBetween(final String landscapeToken, final long from,
      final long to) {

    final Uni<List<LandscapeRecord>> recordsList = this.repo.getBetween(landscapeToken, from, to)
        .map(this.converter::toRecord).collectItems().asList();

    recordsList.onItem().invoke(recs -> {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Found {} records for landscape with token {}", recs.size(), landscapeToken);
      }
    });
    return recordsList.onItem().transform(this.assembler::assembleFromRecords);

  }

  @Override
  public Uni<Void> deleteLandscape(final String landscapeToken) {
    return this.repo.deleteAll(landscapeToken);
  }

  @Override
  public Multi<SpanStructure> cloneLandscape(final String landscapeToken,
      final String clonedLandscapeToken) {
    return this.repo.getAll(clonedLandscapeToken).invoke(x -> x.setLandscapeToken(landscapeToken))
        .call(this.repo::add);
  }

}
