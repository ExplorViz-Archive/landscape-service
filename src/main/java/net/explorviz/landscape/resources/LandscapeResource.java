package net.explorviz.landscape.resources;

import java.awt.*;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import net.explorviz.landscape.model.Landscape;
import net.explorviz.landscape.peristence.QueryException;
import net.explorviz.landscape.service.assemble.LandscapeAssemblyException;
import net.explorviz.landscape.service.assemble.impl.InvalidRecordException;
import net.explorviz.landscape.service.assemble.impl.NoRecordsException;
import net.explorviz.landscape.service.usecase.UseCases;

@Path("/v2/landscapes")
public class LandscapeResource {

  private UseCases useCases;

  public LandscapeResource(UseCases useCases) {
    this.useCases = useCases;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Landscape getLandscape(
      @QueryParam("token") String token,
      @QueryParam("from") Long from,
      @QueryParam("to") Long to) {

    if (token == null || token.length() == 0) {
      throw new BadRequestException("Token is mandatory");
    }

    int c = (from == null ? 0 : 1) + (to == null ? 0 : 2);
    Landscape buildLandscape = new Landscape(token);
    try {
      switch (c) {
        case 0: // Both null
          buildLandscape = useCases.buildLandscape(token);
          break;
        case 1: // from is given
          System.out.println(from);
          buildLandscape = useCases.BuildLandscapeFrom(token, from);
          break;
        case 2:
          System.out.println(to);
          buildLandscape = useCases.BuildLandscapeTo(token, to);
          break;
        case 3:
          System.out.println(from + "   " + to);
          buildLandscape = useCases.BuildLandscapeBetweeen(token, from, to);
      }
    } catch (QueryException e) {
      throw new InternalServerErrorException("Could not dispatch query");
    } catch (NoRecordsException e) {
      // Do nothing, returns empty landscape
    } catch (LandscapeAssemblyException e) {
      // Never caused by the user
      throw new InternalServerErrorException(e.getMessage());
    }


    return buildLandscape;
  }


}
