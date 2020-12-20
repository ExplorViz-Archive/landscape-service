package net.explorviz.landscape.service.usecase;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import net.explorviz.avro.landscape.flat.LandscapeRecord;
import net.explorviz.avro.landscape.model.Landscape;
import net.explorviz.landscape.peristence.QueryException;
import net.explorviz.landscape.peristence.Repository;
import net.explorviz.landscape.peristence.Specification;
import net.explorviz.landscape.peristence.cassandra.specifications.FindRecordsBetweenTimestamps;
import net.explorviz.landscape.service.assemble.LandscapeAssembler;
import net.explorviz.landscape.service.assemble.LandscapeAssemblyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements the use cases
 */
@ApplicationScoped
public class LandscapeServiceImpl implements LandscapeService {

  private static final Logger LOGGER = LoggerFactory.getLogger(LandscapeServiceImpl.class);

  private final Repository<LandscapeRecord> repo;
  private final LandscapeAssembler assembler;

  @Inject
  public LandscapeServiceImpl(Repository<LandscapeRecord> repo, LandscapeAssembler assembler) {
    this.repo = repo;
    this.assembler = assembler;
  }

  @Override
  public Landscape buildLandscapeBetween(String landscapeToken, long from, long to)
      throws LandscapeAssemblyException, QueryException {


    Specification spec = new FindRecordsBetweenTimestamps(landscapeToken, from, to);
    List<LandscapeRecord> recordList;
    Landscape buildLandscape;

    // Fetch records
    recordList = repo.query(spec);
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("Found {} records to token {} in time range ({}, {})", recordList.size(),
          landscapeToken, from, to);
    }

    // Assemble
    buildLandscape = assembler.assembleFromRecords(recordList);
    return buildLandscape;
  }

  @Override
  public void deleteLandscape(final String landscapeToken) {
    repo.deleteAll(landscapeToken);
  }



}
