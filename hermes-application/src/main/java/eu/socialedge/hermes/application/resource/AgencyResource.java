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
import eu.socialedge.hermes.application.resource.spec.AgencySpecification;
import eu.socialedge.hermes.application.service.AgencyService;
import eu.socialedge.hermes.domain.operator.Agency;
import eu.socialedge.hermes.domain.operator.AgencyId;

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
@Path("/v2/agencies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AgencyResource {

    private final AgencyService agencyService;

    @Inject
    public AgencyResource(AgencyService agencyService) {
        this.agencyService = agencyService;
    }

    @POST
    public Response create(@NotNull @Valid AgencySpecification spec, @Context UriInfo uriInfo) {
        agencyService.createAgency(spec);

        return Response.created(uriInfo.getAbsolutePathBuilder()
                .path(spec.agencyId)
                .build()).build();
    }

    @GET
    @Path("/{agencyId}")
    public Agency read(@PathParam("agencyId") @NotNull AgencyId agencyId) {
        Optional<Agency> agencyOpt = agencyService.fetchAgency(agencyId);

        if (!agencyOpt.isPresent())
            throw new NotFoundException("Failed to find agency. Id = " + agencyId);

        return agencyOpt.get();
    }

    @GET
    public Collection<Agency> read() {
        return agencyService.fetchAllAgencies();
    }

    @PATCH
    @Path("/{agencyId}")
    public Response update(@PathParam("agencyId") @NotNull AgencyId agencyId,
                           @NotNull AgencySpecification spec) {
        agencyService.updateAgency(spec);

        return Response.ok().build();
    }

    @DELETE
    @Path("/{agencyId}")
    public Response delete(@PathParam("agencyId") @NotNull AgencyId agencyId) {
        boolean wasDeleted = agencyService.deleteAgency(agencyId);
        if (!wasDeleted)
            throw new NotFoundException("Failed to find agency to delete. Id = " + agencyId);

        return Response.noContent().build();
    }
}
