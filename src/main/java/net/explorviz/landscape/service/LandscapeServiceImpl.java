package net.explorviz.landscape.service;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import net.explorviz.avro.landscape.model.Landscape;
import net.explorviz.landscape.peristence.SpanStructureRepositoy;
import net.explorviz.landscape.service.assemble.LandscapeAssembler;
import net.explorviz.landscape.service.assemble.LandscapeAssemblyException;
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

  @Inject
  public LandscapeServiceImpl(final SpanStructureRepositoy repo,
      final LandscapeAssembler assembler) {
    this.repo = repo;
    this.assembler = assembler;
  }

  @Override
  public Landscape buildLandscapeBetween(final String landscapeToken, final long from,
      final long to)
      throws LandscapeAssemblyException {




    if (LOGGER.isInfoEnabled()) {
      //LOGGER.info("Found {} records to token {} in time range ({}, {})", recordList.size(),
      //    landscapeToken, from, to);
    }

    // Assemble
    //buildLandscape = this.assembler.assembleFromRecords(recordList);
    //return buildLandscape;
    return null;
  }

  @Override
  public void deleteLandscape(final String landscapeToken) {
    this.repo.deleteAll(landscapeToken);
  }



}
