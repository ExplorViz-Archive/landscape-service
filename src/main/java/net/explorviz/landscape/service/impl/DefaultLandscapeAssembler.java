package net.explorviz.landscape.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import net.explorviz.landscape.LandscapeRecord;
import net.explorviz.landscape.model.Application;
import net.explorviz.landscape.model.Clazz;
import net.explorviz.landscape.model.Landscape;
import net.explorviz.landscape.model.Node;
import net.explorviz.landscape.model.Package;
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
    Landscape landscape = new Landscape(token, new ArrayList<>());

    insertAll(landscape, records);
    return landscape;
  }

  @Override
  public void insertAll(Landscape landscape, Collection<LandscapeRecord> records)
      throws LandscapeAssemblyException {

    final String token = landscape.getToken();

    // Check if all records belong to the same landscape (i.e. check token)
    if (!sameToken(token, records)) {
      throw new LandscapeAssemblyException("All records must have the same token");
    }

    for (LandscapeRecord insertMe : records) {

      // Find node in landscape or insert new
      String hostName = insertMe.getNode().getHostName();
      String ipAddress = insertMe.getNode().getIpAddress();
      Node node;
      Optional<Node> foundNode = AssemblyUtils.findNode(landscape, hostName, ipAddress);

      if (foundNode.isPresent()) {
        node = foundNode.get();
      } else {
        node = new Node(hostName, ipAddress);
        landscape.getNodes().add(node);
      }

      // Find application in node or insert new
      String appName = insertMe.getApplication().getName();
      String appPid = "todo"; //insertMe.getApplication().getPid();
      String appLanguage = insertMe.getApplication().getLanguage();
      Application app;
      Optional<Application> foundApp =
          AssemblyUtils.findApplication(node, appPid, appName, appLanguage);
      if (foundApp.isPresent()) {
        app = foundApp.get();
      } else {
        app = new Application(appName, appLanguage, appPid);
        node.getApplications().add(app);
      }

      // Merge package structure
      String[] pkgs = insertMe.getPackage$().split("\\.");
      int unknownPkgIndex =
          PackageHelper.lowestPackageIndex(app, pkgs);

      if (unknownPkgIndex < pkgs.length) {

        String[] pksToInsert = Arrays.copyOfRange(pkgs, unknownPkgIndex, pkgs.length);
        Package rootToInsert = PackageHelper.toHierarchy(pksToInsert);
        // Merge missing packages
        if (unknownPkgIndex == 0) {
          // Add new root package
          app.getPackages().add(rootToInsert);
        } else {
          // Merge into hierarchy
          String[] existing = Arrays.copyOfRange(pkgs, 0, unknownPkgIndex);
          Package lowest = PackageHelper.fromPath(app, existing);
          lowest.getSubPackages().add(rootToInsert);
        }

      }
      // The package to add the class to
      Package leafPkg = PackageHelper.fromPath(app, pkgs);

      // Get or creat class
      Clazz cls;
      Optional<Clazz> foundCls = AssemblyUtils.findClazz(leafPkg, insertMe.getClass$());
      if (foundCls.isPresent()) {
        cls = foundCls.get();
      } else {
        cls = new Clazz(insertMe.getClass$());
        leafPkg.getClasses().add(cls);
      }
      // Add the method
      cls.getMethods().add(insertMe.getMethod());
    }
  }

  private boolean sameToken(String token, Collection<LandscapeRecord> records) {
    return records.stream().allMatch(r -> token.equals(r.getLandscapeToken()));
  }
}
