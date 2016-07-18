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

import eu.socialedge.hermes.application.ext.Resource;
import eu.socialedge.hermes.application.resource.dto.RouteDTO;
import eu.socialedge.hermes.application.resource.dto.WaypointDTO;
import eu.socialedge.hermes.application.service.RouteService;
import eu.socialedge.hermes.application.service.StationService;
import eu.socialedge.hermes.domain.infrastructure.Route;
import eu.socialedge.hermes.domain.infrastructure.Station;
import eu.socialedge.hermes.domain.infrastructure.Waypoint;
import org.springframework.transaction.annotation.Transactional;

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
import java.util.stream.Collectors;

import static eu.socialedge.hermes.application.resource.dto.DTOMapper.routeResponse;
import static eu.socialedge.hermes.application.resource.dto.DTOMapper.waypointResponse;

@Resource
@Path("/v1/routes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional(readOnly = true)
public class RouteResource {
    private final RouteService routeService;
    private final StationService stationService;

    @Inject
    public RouteResource(RouteService routeService, StationService stationService) {
        this.routeService = routeService;
        this.stationService = stationService;
    }

    @POST
    @Transactional
    public Response create(@NotNull @Valid RouteDTO routeDTO, @Context UriInfo uriInfo) {
        String routeCode = routeDTO.getCodeId();
        Collection<Waypoint> waypoints = unwrapWaypoints(routeDTO.getWaypoints());

        Route persistedRoute = routeService.createLine(routeCode, waypoints);
        return Response.created(uriInfo.getAbsolutePathBuilder()
                .path(persistedRoute.getCodeId())
                .build()).build();
    }

    @POST
    @Transactional
    @Path("/{routeCode}/waypoints")
    public Response createWaypoint(@PathParam("routeCode") @Size(min = 1) String routeCode,
                                   @NotNull @Valid WaypointDTO waypointDTO,
                                   @Context UriInfo uriInfo) {
        Station station = stationService.fetchStation(waypointDTO.getStationCodeId());
        int position = waypointDTO.getPosition();

        routeService.createWaypoint(routeCode, station, position);
        return Response.created(uriInfo.getAbsolutePathBuilder()
                .path(routeCode)
                .build()).build();
    }

    @GET
    public Collection<RouteDTO> read() {
        return routeResponse(routeService.fetchAllRoutes());
    }
    
    @GET
    @Path("/{routeCode}")
    public RouteDTO read(@PathParam("routeCode") @Size(min = 1) String routeCode) {
        return routeResponse(routeService.fetchRoute(routeCode));
    }

    @GET
    @Path("/{routeCode}/waypoints")
    public Collection<WaypointDTO> readWaypoints(@PathParam("routeCode") @Size(min = 1) String routeCode) {
        return waypointResponse(routeService.fetchWaypoints(routeCode));
    }

    @DELETE
    @Transactional
    @Path("/{routeCode}")
    public Response delete(@PathParam("routeCode") @Size(min = 1) String routeCode) {
        routeService.removeRoute(routeCode);
        return Response.noContent().build();
    }

    @DELETE
    @Transactional
    @Path("/{routeCode}/waypoints/{stationCodeId}")
    public Response deleteWaypoint(@PathParam("routeCode") @Size(min = 1) String routeCode,
                                   @PathParam("stationCodeId") @Size(min = 1) String stationCodeId) {
        routeService.removeWaypoint(routeCode, stationCodeId);
        return Response.noContent().build();
    }

    private Collection<Waypoint> unwrapWaypoints(Collection<WaypointDTO> waypointDTOs) {
        return waypointDTOs.stream().map(wdto -> {
            Station station = stationService.fetchStation(wdto.getStationCodeId());
            int position = wdto.getPosition();

            return Waypoint.of(station, position);
        }).collect(Collectors.toList());
    }
}
