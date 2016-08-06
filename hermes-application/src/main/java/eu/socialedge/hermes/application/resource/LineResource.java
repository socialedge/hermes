/**
 * Hermes - The Municipal Transport Timetable System
 * Copyright (c) 2016 SocialEdge
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package eu.socialedge.hermes.application.resource;

import eu.socialedge.hermes.application.ext.PATCH;
import eu.socialedge.hermes.application.ext.Resource;
import eu.socialedge.hermes.application.resource.spec.LineSpecification;
import eu.socialedge.hermes.application.service.LineService;
import eu.socialedge.hermes.domain.transit.Line;
import eu.socialedge.hermes.domain.transit.LineId;

import java.util.Collection;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Resource
@Path("/v1.1/lines")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LineResource {

    private final LineService lineService;

    @Inject
    public LineResource(LineService lineService) {
        this.lineService = lineService;
    }

    @POST
    public Response create(@NotNull @Valid LineSpecification spec, @Context UriInfo uriInfo) {
        lineService.createLine(spec);

        return Response.created(uriInfo.getAbsolutePathBuilder()
                .path(spec.lineId)
                .build()).build();
    }

    @GET
    @Path("/{lineId}")
    public Line read(@PathParam("lineId") @NotNull LineId lineId) {
        return lineService.fetchLine(lineId)
                .orElseThrow(() -> new NotFoundException("Failed to find line. Id = " + lineId));
    }

    @GET
    public Collection<Line> read() {
        return lineService.fetchAllLines();
    }

    @PATCH
    @Path("/{lineId}")
    public Response update(@PathParam("lineId") @NotNull LineId lineId,
                           @NotNull LineSpecification spec) {
        lineService.updateLine(lineId, spec);

        return Response.ok().build();
    }

    @DELETE
    @Path("/{lineId}")
    public Response delete(@PathParam("lineId") @NotNull LineId lineId) {
        boolean wasDeleted = lineService.deleteLine(lineId);
        if (!wasDeleted)
            throw new NotFoundException("Failed to find line to delete. Id = " + lineId);

        return Response.noContent().build();
    }
}
