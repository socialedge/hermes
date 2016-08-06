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
import eu.socialedge.hermes.application.resource.spec.RouteSpecification;
import eu.socialedge.hermes.application.service.RouteService;
import eu.socialedge.hermes.domain.transit.Route;
import eu.socialedge.hermes.domain.transit.RouteId;

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
@Path("/v1.1/routes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RouteResource {

    private final RouteService routeService;

    @Inject
    public RouteResource(RouteService routeService) {
        this.routeService = routeService;
    }

    @POST
    public Response create(@NotNull @Valid RouteSpecification spec, @Context UriInfo uriInfo) {
        routeService.createRoute(spec);

        return Response.created(uriInfo.getAbsolutePathBuilder()
                .path(spec.routeId)
                .build()).build();
    }

    @GET
    @Path("/{routeId}")
    public Route read(@PathParam("routeId") @NotNull RouteId routeId) {
        return routeService.fetchRoute(routeId)
                .orElseThrow(() -> new NotFoundException("Failed to find route. Id = " + routeId));
    }

    @GET
    public Collection<Route> read() {
        return routeService.fetchAllRoutes();
    }

    @PATCH
    @Path("/{routeId}")
    public Response update(@PathParam("routeId") @NotNull RouteId routeId,
                           @NotNull RouteSpecification spec) {
        routeService.updateRoute(routeId, spec);

        return Response.ok().build();
    }

    @DELETE
    @Path("/{routeId}")
    public Response delete(@PathParam("routeId") @NotNull RouteId routeId) {
        boolean wasDeleted = routeService.deleteRoute(routeId);
        if (!wasDeleted)
            throw new NotFoundException("Failed to find route to delete. Id = " + routeId);

        return Response.noContent().build();
    }
}
