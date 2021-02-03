package net.explorviz.landscape.service.assemble.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import net.explorviz.avro.landscape.flat.LandscapeRecord;
import net.explorviz.avro.landscape.model.Application;
import net.explorviz.avro.landscape.model.Class;
import net.explorviz.avro.landscape.model.Landscape;
import net.explorviz.avro.landscape.model.Method;
import net.explorviz.avro.landscape.model.Node;
import net.explorviz.avro.landscape.model.Package;
import net.explorviz.landscape.service.assemble.LandscapeAssembler;
import net.explorviz.landscape.service.assemble.LandscapeAssemblyException;

/**
 * Assemble a landscape graph out of a set of flat landscape records.
 */
@ApplicationScoped
public class DefaultLandscapeAssembler implements LandscapeAssembler {

  private final RecordValidator validator;

  @Inject
  public DefaultLandscapeAssembler(final RecordValidator validator) {
    this.validator = validator;
  }

  @Override
  public Landscape assembleFromRecords(final Collection<LandscapeRecord> records)
      throws LandscapeAssemblyException {

    final String token = records.stream().findFirst()
        .orElseThrow(NoRecordsException::new)
        .getLandscapeToken();

    // Create empty landscape and insert all records
    final Landscape landscape = new Landscape(token, new ArrayList<>());

    this.insertAll(landscape, records);
    return landscape;
  }

  @Override
  public void insertAll(final Landscape landscape, final Collection<LandscapeRecord> records)
      throws LandscapeAssemblyException {

    final String token = landscape.getLandscapeToken();

    // Check if all records belong to the same landscape (i.e. check token)
    if (!this.sameToken(token, records)) {
      throw new InvalidRecordException("All records must have the same token");
    }

    for (final LandscapeRecord insertMe : records) {

      // Throws if invalid
      this.validator.validate(insertMe);

      // Find node in landscape or insert new
      final String hostName = insertMe.getNode().getHostName();
      final String ipAddress = insertMe.getNode().getIpAddress();
      Node node;
      final Optional<Node> foundNode = AssemblyUtils.findNode(landscape, hostName, ipAddress);

      if (foundNode.isPresent()) {
        node = foundNode.get();
      } else {
        node = new Node(ipAddress, hostName, new ArrayList<>()); // NOPMD
        landscape.getNodes().add(node);
      }

      // Find application in node or insert new
      final String appName = insertMe.getApplication().getName();
      final long instanceId = insertMe.getApplication().getInstanceId();
      final String appLanguage = insertMe.getApplication().getLanguage();
      Application app;
      final Optional<Application> foundApp =
          AssemblyUtils.findApplication(node, appName, instanceId);
      if (foundApp.isPresent()) {
        app = foundApp.get();
      } else {
        app = new Application(appName, appLanguage, instanceId, new ArrayList<>()); // NOPMD
        node.getApplications().add(app);
      }

      // Merge package structure
      final String[] pkgs = insertMe.getPackage$().split("\\.");
      final int unknownPkgIndex =
          PackageHelper.lowestPackageIndex(app, pkgs);

      if (unknownPkgIndex < pkgs.length) {

        final String[] pksToInsert = Arrays.copyOfRange(pkgs, unknownPkgIndex, pkgs.length);
        final Package rootToInsert = PackageHelper.toHierarchy(pksToInsert);
        // Merge missing packages
        if (unknownPkgIndex == 0) {
          // Add new root package
          app.getPackages().add(rootToInsert);
        } else {
          // Merge into hierarchy
          final String[] existing = Arrays.copyOfRange(pkgs, 0, unknownPkgIndex);
          final Package lowest = PackageHelper.fromPath(app, existing);
          lowest.getSubPackages().add(rootToInsert);
        }

      }
      // The package to add the class to
      final Package leafPkg = PackageHelper.fromPath(app, pkgs);

      // Get or creat class
      Class cls;
      final Optional<Class> foundCls = AssemblyUtils.findClazz(leafPkg, insertMe.getClass$());
      if (foundCls.isPresent()) {
        cls = foundCls.get();
      } else {
        cls = new Class(insertMe.getClass$(), new ArrayList<>()); // NOPMD
        leafPkg.getClasses().add(cls);
      }
      // Add the method
      cls.getMethods().add(new Method(insertMe.getMethod(), insertMe.getHashCode())); // NOPMD
    }
  }

  private boolean sameToken(final String token, final Collection<LandscapeRecord> records) {
    return records.stream().allMatch(r -> token.equals(r.getLandscapeToken()));
  }
}
