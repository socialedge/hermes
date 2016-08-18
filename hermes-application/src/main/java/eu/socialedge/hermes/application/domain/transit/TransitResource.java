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

import eu.socialedge.hermes.application.domain.transit.dto.LineSpecification;
import eu.socialedge.hermes.application.domain.transit.dto.LineSpecificationMapper;
import eu.socialedge.hermes.application.domain.transit.dto.RouteSpecification;
import eu.socialedge.hermes.application.domain.transit.dto.RouteSpecificationMapper;
import eu.socialedge.hermes.application.ext.PATCH;
import eu.socialedge.hermes.application.ext.Resource;
import eu.socialedge.hermes.domain.transit.LineId;
import eu.socialedge.hermes.domain.transit.RouteId;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
    private final LineSpecificationMapper lineSpecMapper;
    private final RouteSpecificationMapper routeSpecMapper;

    @Inject
    public TransitResource(TransitService transitService, LineSpecificationMapper lineSpecMapper,
                           RouteSpecificationMapper routeSpecMapper) {
        this.transitService = transitService;
        this.lineSpecMapper = lineSpecMapper;
        this.routeSpecMapper = routeSpecMapper;
    }

    @POST
    public Response createLine(@NotNull @Valid LineSpecification data,
                               @Context UriInfo uriInfo) {
        transitService.createLine(data);

        return Response.created(uriInfo.getAbsolutePathBuilder()
                .path(data.id)
                .build()).build();
    }

    @POST
    @Path("/{lineId}/routes")
    public Response createRoute(@PathParam("lineId") LineId lineId,
                                @NotNull @Valid RouteSpecification data,
                                @Context UriInfo uriInfo) {
        transitService.createRoute(lineId, data);

        return Response.created(uriInfo.getAbsolutePathBuilder()
                .path(data.id)
                .build()).build();
    }

    @GET
    @Path("/{lineId}")
    public LineSpecification readLine(@PathParam("lineId") LineId lineId) {
        return lineSpecMapper.toDto(transitService.fetchLine(lineId));
    }

    @GET
    @Path("/{lineId}/routes/{routeId}")
    public RouteSpecification readRoute(@PathParam("lineId") LineId lineId,
                                        @PathParam("routeId") RouteId routeId) {
        return routeSpecMapper.toDto(transitService.fetchRoute(lineId, routeId));
    }

    @GET
    public Collection<LineSpecification> readAllLines() {
        return transitService.fetchAllLines().stream()
                .map(lineSpecMapper::toDto).collect(Collectors.toList());
    }

    @GET
    @Path("/{lineId}/routes")
    public Collection<RouteSpecification> readAllRoutes(@PathParam("lineId") LineId lineId) {
        return transitService.fetchAllRoutes(lineId).stream()
                .map(routeSpecMapper::toDto).collect(Collectors.toList());
    }

    @PATCH
    @Path("/{lineId}")
    public Response updateLine(@PathParam("lineId") LineId lineId,
                               @NotNull LineSpecification data) {
        transitService.updateLine(lineId, data);

        return Response.ok().build();
    }

    @PATCH
    @Path("/{lineId}/routes/{routeId}")
    public Response updateRoute(@PathParam("lineId") LineId lineId,
                                @PathParam("routeId") RouteId routeId,
                                @NotNull RouteSpecification data) {
        transitService.updateRoute(lineId, routeId, data);

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
