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
import eu.socialedge.hermes.application.resource.dto.StationDTO;
import eu.socialedge.hermes.application.resource.exception.NotFoundException;
import eu.socialedge.hermes.domain.infrastructure.Station;
import eu.socialedge.hermes.domain.infrastructure.StationRepository;
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

import static eu.socialedge.hermes.application.resource.dto.DTOMapper.stationResponse;

@Resource
@Path("/v1/stations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional(readOnly = true)
public class StationResource {
    @Inject private StationRepository stationRepository;

    @POST
    @Transactional
    public Response create(@NotNull @Valid StationDTO stationDTO, @Context UriInfo uriInfo) {
        String codeId = stationDTO.getCodeId();
        String name = stationDTO.getName();
        TransportType transportType = stationDTO.getTransportType();

        Station persistedStation = stationRepository.store(new Station(codeId, name, transportType));
        return Response.created(uriInfo.getAbsolutePathBuilder()
                .path(persistedStation.getCodeId())
                .build()).build();
    }

    @GET
    public Collection<StationDTO> read() {
        return stationResponse(stationRepository.list());
    }

    @GET
    @Path("/{stationCodeId}")
    public StationDTO read(@PathParam("stationCodeId") @Size(min = 1) String stationCodeId) {
        return stationResponse(fetchStation(stationCodeId));
    }
    
    @PATCH
    @Transactional
    @Path("/{stationCodeId}")
    public Response update(@PathParam("stationCodeId") @Size(min = 1) String stationCodeId,
                           @NotNull StationDTO stationDTO) {
        String patchName = stationDTO.getName();

        if (StringUtils.isNotBlank(patchName)) {
            Station stationToPatch = fetchStation(stationCodeId);

            stationToPatch.setName(patchName);
            stationRepository.store(stationToPatch);
        }

        return Response.ok().build();
    }
    
    @DELETE
    @Transactional
    @Path("/{stationCodeId}")
    public Response delete(@PathParam("stationCodeId") @Size(min = 1) String stationCodeId) {
        stationRepository.remove(fetchStation(stationCodeId));
        return Response.noContent().build();
    }

    private Station fetchStation(String stationCodeId) {
        return stationRepository.get(stationCodeId).orElseThrow(()
                ->  new NotFoundException("No station found with code id = " + stationCodeId));
    }
}
