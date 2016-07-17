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

import eu.socialedge.hermes.application.resource.exception.NotFoundException;
import eu.socialedge.hermes.application.resource.ext.Resource;
import eu.socialedge.hermes.domain.ServiceException;
import eu.socialedge.hermes.domain.infrastructure.*;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Collection;
import java.util.Set;

@Resource
@Path("/v1/routes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RouteResource {
    @Inject private RouteRepository routeRepository;
    @Inject private StationRepository stationRepository;

    @POST
    public Response create(@NotNull Route route, @Context UriInfo uriInfo) {
        Route persistedRoute = routeRepository.store(route);
        return Response.created(uriInfo.getAbsolutePathBuilder()
                                       .path(persistedRoute.getCodeId())
                                       .build())
                       .build();
    }

    @POST
    @Path("/{routeCodeId}/waypoints")
    public Response createWaypoint(@PathParam("routeCodeId") @Size(min = 1) String routeCodeId,
                                   @NotNull @Valid WaypointDefinition wpDef) {
        Route route = read(routeCodeId);
        Station station = stationRepository.get(wpDef.getStationCodeId()).orElseThrow(() ->
                new NotFoundException("Cannot find station with code id = " + wpDef.getStationCodeId()));

        route.insertWaypoint(station, wpDef.getPosition());
        routeRepository.store(route);

        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    public Collection<Route> read() {
        return routeRepository.list();
    }
    
    @GET
    @Path("/{routeCodeId}")
    public Route read(@PathParam("routeCodeId") @Size(min = 1) String routeCodeId) {
        return routeRepository.get(routeCodeId).orElseThrow(()
            -> new NotFoundException("No station found with code id = " + routeCodeId));
    }

    @GET
    @Path("/{routeCodeId}/waypoints")
    public Set<Waypoint> readWaypoints(@PathParam("routeCodeId") @Size(min = 1) String routeCodeId) {
        return read(routeCodeId).getWaypoints();
    }

    @DELETE
    @Path("/{routeCodeId}")
    public Response delete(@PathParam("routeCodeId") @Size(min = 1) String routeCodeId) {
        routeRepository.remove(read(routeCodeId));
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{routeCodeId}/waypoints/{stationCodeId}")
    public Response deleteWaypoint(@PathParam("routeCodeId") @Size(min = 1) String routeCodeId,
                                   @PathParam("stationCodeId") @Size(min = 1) String stationCodeId) {
        Route route = routeRepository.get(routeCodeId).orElseThrow(()
                -> new ServiceException("Failed to find route with code = " + routeCodeId));
        Station station = stationRepository.get(stationCodeId).orElseThrow(() ->
                new NotFoundException("Cannot find station with code id = " + stationCodeId));

        if (!route.removeWaypoint(station))
            throw new NotFoundException("No station on route found with code id = " + stationCodeId);

        routeRepository.store(route);
        return Response.noContent().build();
    }

    private static class WaypointDefinition {
        @NotNull
        @Size(min = 1)
        private String stationCodeId;
        private int position;

        public String getStationCodeId() {
            return stationCodeId;
        }

        public void setStationCodeId(String stationCodeId) {
            this.stationCodeId = stationCodeId;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }
    }
}
