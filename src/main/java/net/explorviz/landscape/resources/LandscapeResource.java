package net.explorviz.landscape.resources;

import java.util.ArrayList;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import net.explorviz.avro.landscape.model.Landscape;
import net.explorviz.landscape.peristence.QueryException;
import net.explorviz.landscape.service.assemble.LandscapeAssemblyException;
import net.explorviz.landscape.service.assemble.impl.NoRecordsException;
import net.explorviz.landscape.service.LandscapeService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

@Path("/v2/landscapes")
public class LandscapeResource {

  private final LandscapeService landscapeService;

  public LandscapeResource(LandscapeService landscapeService) {
    this.landscapeService = landscapeService;
  }

  @GET
  @Path("/{token}/structure")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Retrieve a landscape graph",
             description = "Assembles the (possibly empty) landscape of all spans observed in the given time range")
  @APIResponses(value = {@APIResponse(responseCode = "200",
                                      description = "Success",
                                      content = @Content(mediaType = "application/json",
                                                         schema = @Schema(
                                                             implementation = Landscape.class)))})
  public Landscape getLandscape(@PathParam("token") String token, @QueryParam("from") Long from,
                                @QueryParam("to") Long to) {

    if (token == null || token.length() == 0) {
      throw new BadRequestException("Token is mandatory");
    }

    int c = (from == null ? 0 : 1) + (to == null ? 0 : 2);
    Landscape buildLandscape = new Landscape(token, new ArrayList<>());
    try {
      switch (c) {
        case 0: // Both null
          buildLandscape = landscapeService.buildLandscape(token);
          break;
        case 1: // from is given
          System.out.println(from);
          buildLandscape = landscapeService.buildLandscapeFrom(token, from);
          break;
        case 2:
          System.out.println(to);
          buildLandscape = landscapeService.buildLandscapeTo(token, to);
          break;
        case 3:
          System.out.println(from + "   " + to);
          buildLandscape = landscapeService.buildLandscapeBetween(token, from, to);
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
