package net.explorviz.landscape.resources;

import io.smallrye.mutiny.Uni;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import net.explorviz.avro.landscape.model.Landscape;
import net.explorviz.landscape.service.assemble.LandscapeAssemblyException;
import net.explorviz.landscape.service.assemble.impl.NoRecordsException;
import net.explorviz.landscape.service.cassandra.ReactiveLandscapeService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HTTP resource to access landscapes.
 */
@Path("/v2/landscapes")
public class LandscapeResource {

  private static final Logger LOGGER = LoggerFactory.getLogger(LandscapeResource.class);

  private final ReactiveLandscapeService landscapeService;

  @Inject
  public LandscapeResource(final ReactiveLandscapeService landscapeService) {
    this.landscapeService = landscapeService;
  }

  @GET
  @Path("/{token}/structure")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Retrieve a landscape graph",
      description = "Assembles the (possibly empty) landscape of "
          + "all spans observed in the given time range")
  @APIResponses(value = {@APIResponse(responseCode = "200", description = "Success",
      content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = Landscape.class)))})
  public Uni<Landscape> getLandscape(@PathParam("token") final String token, // NOPMD
      @QueryParam("from") final Long from, @QueryParam("to") final Long to) {

    if (token == null || token.length() == 0) {
      throw new BadRequestException("Token is mandatory");
    }

    final int c = (from == null ? 0 : 1) + (to == null ? 0 : 2);
    Uni<Landscape> buildLandscape;
    try {
      switch (c) {
        case 0: // Both null
          buildLandscape = this.landscapeService.buildLandscape(token);
          break;
        case 1: // from is given
          buildLandscape = this.landscapeService.buildLandscapeFrom(token, from);
          break;
        case 2:
          buildLandscape = this.landscapeService.buildLandscapeTo(token, to);
          break;
        case 3: // NOCS
          buildLandscape = this.landscapeService.buildLandscapeBetween(token, from, to);
          break;
        default:
          throw new InternalServerErrorException("Failed query");
      }
    } catch (final NoRecordsException e) {
      throw new NotFoundException("No landscape with such token " + token, e);
    } catch (final LandscapeAssemblyException e) {
      // Never caused by the user
      throw new InternalServerErrorException(e.getMessage(), e);
    }

    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Requested {} from {} to {}, return value: {}", token, from, to, buildLandscape);
    }

    // Return empty landscape if no records found
    return buildLandscape.onFailure(NoRecordsException.class)
        .transform(t -> new NotFoundException()).onFailure(LandscapeAssemblyException.class)
        .transform(t -> new InternalServerErrorException(t.getMessage()));

  }

}
