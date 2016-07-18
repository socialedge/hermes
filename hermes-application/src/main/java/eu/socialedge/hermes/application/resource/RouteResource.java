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
import eu.socialedge.hermes.application.resource.exception.NotFoundException;
import eu.socialedge.hermes.domain.infrastructure.*;
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
import java.util.Set;
import java.util.stream.Collectors;

import static eu.socialedge.hermes.application.resource.dto.DTOMapper.routeResponse;
import static eu.socialedge.hermes.application.resource.dto.DTOMapper.waypointResponse;

@Resource
@Path("/v1/routes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional(readOnly = true)
public class RouteResource {
    @Inject private RouteRepository routeRepository;
    @Inject private StationRepository stationRepository;

    @POST
    @Transactional
    public Response create(@NotNull @Valid RouteDTO routeDTO, @Context UriInfo uriInfo) {
        String codeId = routeDTO.getCodeId();
        Set<WaypointDTO> waypointDefs = routeDTO.getWaypoints();

        Route route;
        if (waypointDefs != null && !waypointDefs.isEmpty()) {
            Collection<Waypoint> waypoints = unwrapWaypoints(waypointDefs);
            route = new Route(codeId, waypoints);
        } else {
            route = new Route(codeId);
        }

        Route persistedRoute = routeRepository.store(route);

        return Response.created(uriInfo.getAbsolutePathBuilder()
                .path(persistedRoute.getCodeId())
                .build()).build();
    }

    @POST
    @Transactional
    @Path("/{routeCodeId}/waypoints")
    public Response createWaypoint(@PathParam("routeCodeId") @Size(min = 1) String routeCodeId,
                                   @NotNull @Valid WaypointDTO waypointDTO) {
        Route route = fetchRoute(routeCodeId);
        Station station = fetchStation(waypointDTO.getStationCodeId());

        route.insertWaypoint(station, waypointDTO.getPosition());
        routeRepository.store(route);

        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    public Collection<RouteDTO> read() {
        return routeResponse(routeRepository.list());
    }
    
    @GET
    @Path("/{routeCodeId}")
    public RouteDTO read(@PathParam("routeCodeId") @Size(min = 1) String routeCodeId) {
        return routeResponse(fetchRoute(routeCodeId));
    }

    @GET
    @Path("/{routeCodeId}/waypoints")
    public Collection<WaypointDTO> readWaypoints(@PathParam("routeCodeId") @Size(min = 1) String routeCodeId) {
        return waypointResponse(fetchRoute(routeCodeId).getWaypoints());
    }

    @DELETE
    @Transactional
    @Path("/{routeCodeId}")
    public Response delete(@PathParam("routeCodeId") @Size(min = 1) String routeCodeId) {
        routeRepository.remove(fetchRoute(routeCodeId));
        return Response.noContent().build();
    }

    @DELETE
    @Transactional
    @Path("/{routeCodeId}/waypoints/{stationCodeId}")
    public Response deleteWaypoint(@PathParam("routeCodeId") @Size(min = 1) String routeCodeId,
                                   @PathParam("stationCodeId") @Size(min = 1) String stationCodeId) {
        Route route = fetchRoute(routeCodeId);

        if (!route.removeWaypoint(stationCodeId))
            throw new NotFoundException("No station on route found with code id = " + stationCodeId);

        routeRepository.store(route);
        return Response.noContent().build();
    }

    private Collection<Waypoint> unwrapWaypoints(Collection<WaypointDTO> waypointDTOs) {
        return waypointDTOs.stream().map(wdto -> {
            Station station = fetchStation(wdto.getStationCodeId());
            int position = wdto.getPosition();

            return Waypoint.of(station, position);
        }).collect(Collectors.toList());
    }

    private Station fetchStation(String stationCodeId) {
        return stationRepository.get(stationCodeId).orElseThrow(()
                ->  new NotFoundException("No station found with code id = " + stationCodeId));
    }

    private Route fetchRoute(String routeCodeId) {
        return routeRepository.get(routeCodeId).orElseThrow(()
                -> new NotFoundException("No station found with code id = " + routeCodeId));
    }
}
