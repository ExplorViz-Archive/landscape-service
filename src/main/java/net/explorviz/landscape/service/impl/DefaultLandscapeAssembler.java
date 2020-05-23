package net.explorviz.landscape.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import net.explorviz.landscape.LandscapeRecord;
import net.explorviz.landscape.model.Application;
import net.explorviz.landscape.model.Landscape;
import net.explorviz.landscape.model.Node;
import net.explorviz.landscape.service.LandscapeAssembler;
import net.explorviz.landscape.service.LandscapeAssemblyException;

@ApplicationScoped
public class DefaultLandscapeAssembler implements LandscapeAssembler {


  @Override
  public Landscape assembleFromRecords(Collection<LandscapeRecord> records)
      throws LandscapeAssemblyException {

    final String token = records.stream().findFirst()
        .orElseThrow(() -> new LandscapeAssemblyException("At least one record must be given"))
        .getLandscapeToken();

    // Create empty landscape and insert all records
    Landscape landscape = new Landscape(token, Collections.emptyList());

    return insertAll(landscape, records);
  }

  @Override
  public Landscape insertAll(Landscape landscape, Collection<LandscapeRecord> records)
      throws LandscapeAssemblyException {

    final String token = landscape.getToken();

    // Check if all records belong to the same landscape (i.e. check token)
    if (!sameToken(token, records)) {
      throw new LandscapeAssemblyException("All records must have the same token");
    }

    for (LandscapeRecord insertMe: records) {
      Node node = new Node(insertMe.getNode().getHostName(), insertMe.getNode().getIpAddress(), Collections.emptyList());
      AssemblyUtils.findNode(landscape, node).ifPresentOrElse(n -> {}, () -> landscape.getNodes().add(node));
      Application app = new Application(insertMe.getApplication().getName(), insertMe.getApplication().getLanguage(), "todo");
      AssemblyUtils.findApplication(node, app).ifPresentOrElse(a -> {}, () -> node.getApplications().add(app));

    }

    return null;
  }

  private boolean sameToken(String token, Collection<LandscapeRecord> records) {
    return records.stream().allMatch(r -> token.equals(r.getLandscapeToken()));
  }
}
