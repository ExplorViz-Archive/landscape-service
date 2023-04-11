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
  public Landscape assembleFromRecords(final Collection<LandscapeRecord> records) {

    final String token =
        records.stream().findFirst().orElseThrow(NoRecordsException::new).getLandscapeToken();

    // Create empty landscape and insert all records
    final Landscape landscape = new Landscape(token, new ArrayList<>());

    this.insertAll(landscape, records);
    return landscape;
  }

  @Override
  public void insertAll(final Landscape landscape,
      final Collection<LandscapeRecord> records) { // NOPMD NOCS
    final String token = landscape.getLandscapeToken();

    // Check if all records belong to the same landscape (i.e. check token)
    if (!this.sameToken(token, records)) {
      throw new InvalidRecordException("All records must have the same token");
    }

    for (final LandscapeRecord record : records) {

      // Throws if invalid
      this.validator.validate(record);

      final Node node = getNodeForRecord(landscape, record);
      final Application app = getApplicationForRecord(record, node);
      final String[] packages = getPackagesForRecord(record, app);
      final Package leafPkg = PackageHelper.fromPath(app, packages);
      final Class cls = getClassForRecord(record, leafPkg);

      // Add method to class
      cls.getMethods().add(new Method(record.getMethod(), record.getHashCode())); // NOPMD
    }
  }

  private Node getNodeForRecord(final Landscape landscape, final LandscapeRecord record) {
    Node node;

    final String hostName = record.getNode().getHostName();
    final String ipAddress = record.getNode().getIpAddress();

    // Find node in landscape or insert new
    final Optional<Node> foundNode = AssemblyUtils.findNode(landscape, hostName, ipAddress);

    if (foundNode.isPresent()) {
      node = foundNode.get();
    } else {
      node = new Node(ipAddress, hostName, new ArrayList<>()); // NOPMD
      landscape.getNodes().add(node);
    }

    return node;
  }

  private Application getApplicationForRecord(final LandscapeRecord record, final Node node) {
    Application app;

    // Find application in node or insert new
    final String appName = record.getApplication().getName();
    final String instanceId = record.getApplication().getInstanceId();
    final String appLanguage = record.getApplication().getLanguage();
    final Optional<Application> foundApp = AssemblyUtils.findApplication(node, appName, instanceId);
    if (foundApp.isPresent()) {
      app = foundApp.get();
    } else {
      app = new Application(appName, appLanguage, instanceId, new ArrayList<>()); // NOPMD
      node.getApplications().add(app);
    }

    return app;
  }

  private String[] getPackagesForRecord(final LandscapeRecord record, final Application app) {
    // Merge package structure
    final String[] packages = record.getPackage$().split("\\.");
    final int unknownPkgIndex = PackageHelper.lowestPackageIndex(app, packages);

    if (unknownPkgIndex < packages.length) {

      final String[] pksToInsert = Arrays.copyOfRange(packages, unknownPkgIndex, packages.length);
      final Package rootToInsert = PackageHelper.toHierarchy(pksToInsert);
      // Merge missing packages
      if (unknownPkgIndex == 0) {
        // Add new root package
        app.getPackages().add(rootToInsert);
      } else {
        // Merge into hierarchy
        final String[] existing = Arrays.copyOfRange(packages, 0, unknownPkgIndex);
        final Package lowest = PackageHelper.fromPath(app, existing);
        lowest.getSubPackages().add(rootToInsert);
      }
    }

    return packages;
  }

  private Class getClassForRecord(final LandscapeRecord record, final Package leafPkg) {
    // Get or creat class
    Class cls;
    final Optional<Class> foundCls = AssemblyUtils.findClazz(leafPkg, record.getClass$());
    if (foundCls.isPresent()) {
      cls = foundCls.get();
    } else {
      cls = new Class(record.getClass$(), new ArrayList<>()); // NOPMD
      leafPkg.getClasses().add(cls);
    }

    return cls;
  }

  private boolean sameToken(final String token, final Collection<LandscapeRecord> records) {
    return records.stream().allMatch(r -> token.equals(r.getLandscapeToken()));
  }
}
