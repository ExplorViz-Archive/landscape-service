package net.explorviz.landscape.resources;

import net.explorviz.landscape.model.Landscape;
import net.explorviz.landscape.peristence.QueryException;
import net.explorviz.landscape.service.assemble.LandscapeAssemblyException;
import net.explorviz.landscape.service.assemble.impl.NoRecordsException;
import net.explorviz.landscape.service.usecase.UseCases;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

@Path("/v2/landscapes")
public class LandscapeResource {

  private final UseCases useCases;

  public LandscapeResource(UseCases useCases) {
    this.useCases = useCases;
  }

  @GET
  @Path("/{token}/structure")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Retrieve a landscape graph",
      description = "Assembles the (possibly empty) landscape of all spans observed in the given time range")
  @APIResponses(value = {@APIResponse(responseCode = "200",
      description = "Success",
      content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = Landscape.class)))})
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
