package net.explorviz.landscape.resources;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import net.explorviz.landscape.model.Landscape;
import net.explorviz.landscape.service.usecase.UseCases;

@Path("/v2/landscapes")
public class LandscapeResource {

  private UseCases useCases;

  public LandscapeResource(UseCases useCases) {
    this.useCases = useCases;
  }

  @GET
  public Landscape getLandscape(
      @QueryParam("token") String token,
      @QueryParam("from") Long from,
      @QueryParam("to") Long to) throws LandscapeException {

    if (token == null || token.length() == 0) {
      throw new BadRequestException("Token is mandatory");
    }

    int c = (from == null ? 0 : 1) + (to == null ? 0 : 2);
    Landscape buildLandscape = null;
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


    return buildLandscape;
  }


}
