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
package eu.socialedge.hermes.application.domain.transit;

import eu.socialedge.hermes.application.ext.PATCH;
import eu.socialedge.hermes.application.ext.Resource;
import eu.socialedge.hermes.domain.transit.Line;
import eu.socialedge.hermes.domain.transit.LineId;
import eu.socialedge.hermes.domain.transit.Route;
import eu.socialedge.hermes.domain.transit.RouteId;

import java.util.Collection;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Resource
@Path("/v1.2/lines")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransitResource {

    private final TransitService transitService;

    @Inject
    public TransitResource(TransitService transitService) {
        this.transitService = transitService;
    }

    @POST
    public Response createLine(@NotNull @Valid LineSpecification spec,
                               @Context UriInfo uriInfo) {
        transitService.createLine(spec);

        return Response.created(uriInfo.getAbsolutePathBuilder()
                .path(spec.lineId)
                .build()).build();
    }

    @POST
    @Path("/{lineId}/routes")
    public Response createRoute(@PathParam("lineId") LineId lineId,
                                @NotNull @Valid RouteSpecification spec,
                                @Context UriInfo uriInfo) {
        transitService.createRoute(lineId, spec);

        return Response.created(uriInfo.getAbsolutePathBuilder()
                .path(spec.routeId)
                .build()).build();
    }

    @GET
    @Path("/{lineId}")
    public Line readLine(@PathParam("lineId") LineId lineId) {
        return transitService.fetchLine(lineId);
    }

    @GET
    @Path("/{lineId}/routes/{routeId}")
    public Route readRoute(@PathParam("lineId") LineId lineId,
                           @PathParam("routeId") RouteId routeId) {
        return transitService.fetchRoute(lineId, routeId);
    }

    @GET
    public Collection<Line> readAllLines() {
        return transitService.fetchAllLines();
    }

    @GET
    @Path("/{lineId}/routes")
    public Collection<Route> readAllRoutes(@PathParam("lineId") LineId lineId) {
        return transitService.fetchAllRoutes(lineId);
    }

    @PATCH
    @Path("/{lineId}")
    public Response updateLine(@PathParam("lineId") LineId lineId,
                               @NotNull LineSpecification spec) {
        transitService.updateLine(lineId, spec);

        return Response.ok().build();
    }

    @PATCH
    @Path("/{lineId}/routes/{routeId}")
    public Response updateRoute(@PathParam("lineId") LineId lineId,
                                @PathParam("routeId") RouteId routeId,
                                @NotNull RouteSpecification spec) {
        transitService.updateRoute(lineId, routeId, spec);

        return Response.ok().build();
    }

    @DELETE
    @Path("/{lineId}")
    public Response deleteLine(@PathParam("lineId") LineId lineId) {
        transitService.deleteLine(lineId);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{lineId}/routes/{routeId}")
    public Response deleteRoute(@PathParam("lineId") LineId lineId,
                                @PathParam("routeId") RouteId routeId) {

        transitService.deleteRoute(lineId, routeId);

        return Response.noContent().build();
    }
}
