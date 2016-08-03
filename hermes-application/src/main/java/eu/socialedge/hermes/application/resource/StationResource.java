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
import eu.socialedge.hermes.application.resource.spec.StationSpecification;
import eu.socialedge.hermes.application.service.StationService;
import eu.socialedge.hermes.domain.infrastructure.Station;
import eu.socialedge.hermes.domain.infrastructure.StationId;

import java.util.Collection;
import java.util.Optional;

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
@Path("/v1.1/stations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StationResource {

    private final StationService stationService;

    @Inject
    public StationResource(StationService stationService) {
        this.stationService = stationService;
    }

    @POST
    public Response create(@NotNull @Valid StationSpecification spec, @Context UriInfo uriInfo) {
        stationService.createStation(spec);

        return Response.created(uriInfo.getAbsolutePathBuilder()
                .path(spec.stationId)
                .build()).build();
    }

    @GET
    @Path("/{stationId}")
    public Station read(@PathParam("stationId") @NotNull StationId stationId) {
        Optional<Station> stationOpt = stationService.fetchStation(stationId);

        if (!stationOpt.isPresent())
            throw new NotFoundException("Failed to find station. Id = " + stationId);

        return stationOpt.get();
    }

    @GET
    public Collection<Station> read() {
        return stationService.fetchAllStations();
    }

    @PATCH
    @Path("/{stationId}")
    public Response update(@PathParam("stationId") @NotNull StationId stationId,
                           @NotNull StationSpecification spec) {
        stationService.updateStation(spec);

        return Response.ok().build();
    }

    @DELETE
    @Path("/{stationId}")
    public Response delete(@PathParam("stationId") @NotNull StationId stationId) {
        boolean wasDeleted = stationService.deleteStation(stationId);
        if (!wasDeleted)
            throw new NotFoundException("Failed to find station to delete. Id = " + stationId);

        return Response.noContent().build();
    }
}
