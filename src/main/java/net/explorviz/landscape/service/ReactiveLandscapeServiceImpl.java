package net.explorviz.landscape.service;

import io.smallrye.mutiny.Uni;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import net.explorviz.avro.landscape.flat.LandscapeRecord;
import net.explorviz.avro.landscape.model.Landscape;
import net.explorviz.landscape.peristence.SpanStructureRepositoy;
import net.explorviz.landscape.service.assemble.LandscapeAssembler;
import net.explorviz.landscape.service.assemble.LandscapeAssemblyException;
import net.explorviz.landscape.service.converter.SpanToRecordConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements the use cases.
 */
@ApplicationScoped
public class LandscapeServiceImpl implements LandscapeService {

  private static final Logger LOGGER = LoggerFactory.getLogger(LandscapeServiceImpl.class);

  private final SpanStructureRepositoy repo;
  private final LandscapeAssembler assembler;
  private final SpanToRecordConverter converter;

  @Inject
  public LandscapeServiceImpl(final SpanStructureRepositoy repo,
                              final LandscapeAssembler assembler,
                              final SpanToRecordConverter converter) {
    this.repo = repo;
    this.assembler = assembler;
    this.converter = converter;
  }

  @Override
  public Uni<Landscape> buildLandscapeBetween(final String landscapeToken, final long from,
                                         final long to)
      throws LandscapeAssemblyException {


    Uni<List<LandscapeRecord>> recordsList =
        repo.getBetween(landscapeToken, from, to).map(converter::toRecord)
            .collectItems()
            .asList();


    return recordsList.onItem().invoke(assembler::assembleFromRecords);

  }

  @Override
  public void deleteLandscape(final String landscapeToken) {
    this.repo.deleteAll(landscapeToken);
  }



}
