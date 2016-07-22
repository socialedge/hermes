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

import eu.socialedge.hermes.application.exception.BadRequestException;
import eu.socialedge.hermes.application.ext.PATCH;
import eu.socialedge.hermes.application.ext.Resource;
import eu.socialedge.hermes.application.resource.dto.PositionSpec;
import eu.socialedge.hermes.application.resource.dto.StationSpec;
import eu.socialedge.hermes.application.service.StationService;
import eu.socialedge.hermes.domain.infrastructure.Position;
import eu.socialedge.hermes.domain.infrastructure.Station;
import eu.socialedge.hermes.domain.infrastructure.TransportType;
import org.apache.commons.lang3.StringUtils;
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

import static eu.socialedge.hermes.application.resource.dto.SpecMapper.stationSpec;
import static eu.socialedge.hermes.application.resource.dto.SpecMapper.stationSpecs;
import static java.util.Objects.isNull;

@Resource
@Path("/v1/stations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional(readOnly = true)
public class StationResource {
    private final StationService stationService;

    @Inject
    public StationResource(StationService stationService) {
        this.stationService = stationService;
    }

    @POST
    @Transactional
    public Response create(@NotNull @Valid StationSpec stationSpec, @Context UriInfo uriInfo) {
        String stationCode = stationSpec.getCodeId();
        String name = stationSpec.getName();
        TransportType type = TransportType.valueOf(stationSpec.getName());

        PositionSpec posSpec = stationSpec.getPosition();
        Position position = !isNull(posSpec) ? Position.of(posSpec.getLatitude(), posSpec.getLongitude()) : null;

        Station persistedStation = stationService.createStation(stationCode, name, type, position);
        return Response.created(uriInfo.getAbsolutePathBuilder()
                .path(persistedStation.getCodeId())
                .build()).build();
    }

    @GET
    public Collection<StationSpec> read() {
        return stationSpecs(stationService.fetchAllStations());
    }

    @GET
    @Path("/{stationCodeId}")
    public StationSpec read(@PathParam("stationCodeId") @Size(min = 1) String stationCodeId) {
        return stationSpec(stationService.fetchStation(stationCodeId));
    }
    
    @PATCH
    @Transactional
    @Path("/{stationCodeId}")
    public Response update(@PathParam("stationCodeId") @Size(min = 1) String stationCodeId,
                           @NotNull StationSpec stationSpec) {
        String name = stationSpec.getName();

        if (StringUtils.isBlank(name))
            throw new BadRequestException("Station name param must be specified");

        stationService.updateStation(stationCodeId, name);
        return Response.ok().build();
    }
    
    @DELETE
    @Transactional
    @Path("/{stationCodeId}")
    public Response delete(@PathParam("stationCodeId") @Size(min = 1) String stationCodeId) {
        stationService.removeStation(stationCodeId);
        return Response.noContent().build();
    }
}
