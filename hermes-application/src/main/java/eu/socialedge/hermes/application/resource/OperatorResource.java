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
import eu.socialedge.hermes.application.resource.dto.LineRefSpec;
import eu.socialedge.hermes.application.resource.dto.OperatorSpec;
import eu.socialedge.hermes.application.resource.dto.PositionSpec;
import eu.socialedge.hermes.application.service.LineService;
import eu.socialedge.hermes.application.service.OperatorService;
import eu.socialedge.hermes.domain.infrastructure.Operator;
import eu.socialedge.hermes.domain.infrastructure.Position;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Collection;

import static eu.socialedge.hermes.application.resource.dto.SpecMapper.*;
import static java.util.Objects.isNull;

@Resource
@Path("/v1/operators")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional(readOnly = true)
public class OperatorResource {
    private final LineService lineService;
    private final OperatorService operatorService;

    @Inject
    public OperatorResource(LineService lineService, OperatorService operatorService) {
        this.lineService = lineService;
        this.operatorService = operatorService;
    }

    @POST
    @Transactional
    public Response create(@NotNull @Valid OperatorSpec operatorSpec, @Context UriInfo uriInfo) {
        String name = operatorSpec.getName();
        String desc = operatorSpec.getDescription();
        String url = operatorSpec.getWebsite();

        PositionSpec posSpec = operatorSpec.getPosition();
        Position position = !isNull(posSpec) ? Position.of(posSpec.getLatitude(), posSpec.getLongitude()) : null;

        Operator storedOperator = operatorService.createOperator(name, desc, url, position);
        return Response.created(uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(storedOperator.getId()))
                .build()).build();
    }

    @GET
    public Collection<OperatorSpec> list() {
        return operatorSpecs(operatorService.fetchAllOperators());
    }

    @GET
    @Path("/{operatorId}")
    public OperatorSpec read(@PathParam("operatorId") @Min(1) int operatorId) {
        return operatorSpec(operatorService.fetchOperator(operatorId));
    }

    @GET
    @Path("/{operatorId}/lines")
    public Collection<LineRefSpec> lines(@PathParam("operatorId") @Min(1) int operatorId) {
        return lineRefSpecs(lineService.fetchAllLinesByOperatorId(operatorId));
    }

    @PATCH
    @Transactional
    @Path("/{operatorId}")
    public Response update(@PathParam("operatorId") @Min(1) int operatorId,
                           @NotNull OperatorSpec operatorSpec) {
        String name = operatorSpec.getName();
        String desc = operatorSpec.getDescription();
        String url = operatorSpec.getWebsite();

        PositionSpec posSpec = operatorSpec.getPosition();
        Position position = !isNull(posSpec) ? Position.of(posSpec.getLatitude(), posSpec.getLongitude()) : null;

        operatorService.updateOperator(operatorId, name, desc, url, position);
        return Response.ok().build();
    }

    @DELETE
    @Transactional
    @Path("/{operatorId}")
    public Response delete(@PathParam("operatorId") @Min(1) int operatorId) {
        operatorService.removeOperator(operatorId);
        return Response.noContent().build();
    }
}
