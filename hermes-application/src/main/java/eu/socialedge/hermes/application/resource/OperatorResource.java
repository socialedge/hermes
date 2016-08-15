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
import eu.socialedge.hermes.application.service.OperatorService;
import eu.socialedge.hermes.domain.operator.Agency;
import eu.socialedge.hermes.domain.operator.AgencyId;

import java.util.Collection;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Resource
@Path("/v1.2/agencies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OperatorResource {

    private final OperatorService operatorService;

    @Inject
    public OperatorResource(OperatorService operatorService) {
        this.operatorService = operatorService;
    }

    @POST
    public Response createAgency(@NotNull @Valid AgencySpecification spec, @Context UriInfo uriInfo) {
        operatorService.createAgency(spec);

        return Response.created(uriInfo.getAbsolutePathBuilder()
                .path(spec.agencyId)
                .build()).build();
    }

    @GET
    @Path("/{agencyId}")
    public Agency readAgency(@PathParam("agencyId") AgencyId agencyId) {
        return operatorService.fetchAgency(agencyId);
    }

    @GET
    public Collection<Agency> readAllAgencies() {
        return operatorService.fetchAllAgencies();
    }

    @PATCH
    @Path("/{agencyId}")
    public Response updateAgency(@PathParam("agencyId") AgencyId agencyId,
                                 @NotNull AgencySpecification spec) {
        operatorService.updateAgency(agencyId, spec);

        return Response.ok().build();
    }

    @DELETE
    @Path("/{agencyId}")
    public Response deleteAgency(@PathParam("agencyId") AgencyId agencyId) {
        operatorService.deleteAgency(agencyId);
        return Response.noContent().build();
    }
}
