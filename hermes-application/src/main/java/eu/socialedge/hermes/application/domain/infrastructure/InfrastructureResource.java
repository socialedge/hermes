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
package eu.socialedge.hermes.application.domain.infrastructure;

import eu.socialedge.hermes.domain.infrastructure.StationId;
import eu.socialedge.hermes.domain.infrastructure.dto.StationSpecification;
import eu.socialedge.hermes.domain.infrastructure.dto.StationSpecificationMapper;
import eu.socialedge.hermes.application.ext.PATCH;
import eu.socialedge.hermes.application.ext.Resource;

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
@Path("/v1.2/stations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InfrastructureResource {

    private final InfrastructureService infrastructureService;
    private final StationSpecificationMapper stationSpecMapper;

    @Inject
    public InfrastructureResource(InfrastructureService infrastructureService,
                                  StationSpecificationMapper stationSpecMapper) {
        this.infrastructureService = infrastructureService;
        this.stationSpecMapper = stationSpecMapper;
    }

    @POST
    public Response createStation(@NotNull @Valid StationSpecification data,
                                  @Context UriInfo uriInfo) {
        infrastructureService.createStation(data);

        return Response.created(uriInfo.getAbsolutePathBuilder()
                .path(data.id)
                .build()).build();
    }

    @GET
    @Path("/{stationId}")
    public StationSpecification readStation(@PathParam("stationId") StationId stationId) {
        return stationSpecMapper.toDto(infrastructureService.fetchStation(stationId));
    }

    @GET
    public Collection<StationSpecification> readAllStations() {
        return infrastructureService.fetchAllStations().stream()
                .map(stationSpecMapper::toDto).collect(Collectors.toList());
    }

    @PATCH
    @Path("/{stationId}")
    public Response updateStation(@PathParam("stationId") StationId stationId,
                                  @NotNull StationSpecification data) {
        infrastructureService.updateStation(stationId, data);

        return Response.ok().build();
    }

    @DELETE
    @Path("/{stationId}")
    public Response deleteStation(@PathParam("stationId") StationId stationId) {
        infrastructureService.deleteStation(stationId);
        return Response.noContent().build();
    }
}
