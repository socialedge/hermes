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
import eu.socialedge.hermes.application.resource.ext.PATCH;
import eu.socialedge.hermes.application.resource.ext.Resource;
import eu.socialedge.hermes.domain.infrastructure.Station;
import eu.socialedge.hermes.domain.infrastructure.StationRepository;

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

@Resource
@Path("/v1/stations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StationResource {
    @Inject private StationRepository stationRepository;

    @POST
    public Response create(@NotNull Station station, @Context UriInfo uriInfo) {
        Station persistedStation = stationRepository.store(station);

        return Response.created(uriInfo.getAbsolutePathBuilder()
                                       .path(persistedStation.getCodeId())
                                       .build())
                       .build();
    }

    @GET
    public Collection<Station> read() {
        return stationRepository.list();
    }

    @GET
    @Path("/{stationCodeId}")
    public Station read(@PathParam("stationCodeId") @Size(min = 1) String stationCodeId) {
        return stationRepository.get(stationCodeId).orElseThrow(()
            ->  new NotFoundException("No station found with code id = " + stationCodeId));
    }
    
    @PATCH
    @Path("/{stationCodeId}")
    public Response update(@PathParam("stationCodeId") @Size(min = 1) String stationCodeId,
                           @NotNull @Valid StationPatch patch) {
        Station stationToPatch = read(stationCodeId);

        stationToPatch.setName(patch.getName());
        stationRepository.store(stationToPatch);
        return Response.ok().build();
    }
    
    @DELETE
    @Path("/{stationCodeId}")
    public Response delete(@PathParam("stationCodeId") @Size(min = 1) String stationCodeId) {
        stationRepository.remove(read(stationCodeId));
        return Response.noContent().build();
    }

    private static class StationPatch {
        @NotNull
        @Size(min = 1)
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
