package net.explorviz.landscape.service.cassandra;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import net.explorviz.avro.landscape.flat.LandscapeRecord;
import net.explorviz.avro.landscape.model.Landscape;
import net.explorviz.landscape.persistence.model.SpanStructure;
import net.explorviz.landscape.service.assemble.LandscapeAssembler;
import net.explorviz.landscape.service.converter.SpanToRecordConverter;

/**
 * Implements the use cases.
 */
@ApplicationScoped
public class ReactiveLandscapeServiceImpl implements ReactiveLandscapeService {

  //private static final Logger LOGGER =
  // LoggerFactory.getLogger(ReactiveLandscapeServiceImpl.class);

  private final ReactiveSpanStructureService spanStructureService;
  private final LandscapeAssembler assembler;
  private final SpanToRecordConverter converter;

  /**
   * Reactive interface to initialize landscape service attributes.
   *
   * @param spanStructureService Service for span structures
   * @param assembler Assembler for landscape
   * @param converter Span to record converter
   */
  @Inject
  public ReactiveLandscapeServiceImpl(final ReactiveSpanStructureService spanStructureService,
      final LandscapeAssembler assembler, final SpanToRecordConverter converter) {
    this.spanStructureService = spanStructureService;
    this.assembler = assembler;
    this.converter = converter;
  }

  @Override
  public Uni<Landscape> buildLandscapeBetween(final String landscapeToken, final long from,
      final long to) {

    final Uni<List<LandscapeRecord>> recordsList =
        this.spanStructureService.findBetweenInterval(landscapeToken, from, to)
            .map(this.converter::toRecord).collect().asList();

    return recordsList.onItem().transform(this.assembler::assembleFromRecords);

  }

  @Override
  public Uni<Void> deleteLandscape(final String landscapeToken) {
    return this.spanStructureService.deleteByToken(landscapeToken);
  }

  @Override
  public Multi<SpanStructure> cloneLandscape(final String landscapeToken,
      final String clonedLandscapeToken) {
    return this.spanStructureService.findByToken(clonedLandscapeToken)
        .invoke(x -> x.setLandscapeToken(landscapeToken)).call(this.spanStructureService::add);
  }

}
