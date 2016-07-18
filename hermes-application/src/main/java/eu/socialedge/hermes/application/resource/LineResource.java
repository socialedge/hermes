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
import eu.socialedge.hermes.application.resource.dto.LineDTO;
import eu.socialedge.hermes.application.resource.dto.RouteDTO;
import eu.socialedge.hermes.application.service.LineService;
import eu.socialedge.hermes.domain.infrastructure.Line;
import eu.socialedge.hermes.domain.infrastructure.TransportType;
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
import java.util.List;

import static eu.socialedge.hermes.application.resource.dto.DTOMapper.lineResponse;
import static eu.socialedge.hermes.application.resource.dto.DTOMapper.routeResponse;

@Resource
@Path("/v1/lines")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional(readOnly = true)
public class LineResource {
    private final LineService lineService;

    @Inject
    public LineResource(LineService lineService) {
        this.lineService = lineService;
    }

    @POST
    @Transactional
    public Response create(@NotNull @Valid LineDTO lineDTO, @Context UriInfo uriInfo) {
        String codeId = lineDTO.getCodeId();
        int operatorId = lineDTO.getOperatorId();
        Collection<String> routeCodes = lineDTO.getRouteCodes();
        TransportType transportType = lineDTO.getTransportType();

        Line storedLine = lineService.createLine(codeId, operatorId, transportType, routeCodes);
        return Response.created(uriInfo.getAbsolutePathBuilder()
                .path(storedLine.getCodeId())
                .build()).build();
    }

    @POST
    @Path("/{lineCode}/routes")
    @Transactional
    public Response attachRoute(@NotNull @Size(min = 1) @PathParam("lineCode") String lineCode,
                                @NotNull @Size(min = 1) List<String> routeCodes) {
        lineService.attachRoute(lineCode, routeCodes);
        return Response.ok().build();
    }

    @GET
    public Collection<LineDTO> read() {
        return lineResponse(lineService.fetchAllLines());
    }

    @GET
    @Path("/{lineCode}")
    public LineDTO read(@PathParam("lineCode") @Size(min = 1) String lineCode) {
        return lineResponse(lineService.fetchLine(lineCode));
    }

    @GET
    @Path("/{lineCode}/routes")
    public Collection<RouteDTO> routes(@PathParam("lineCode") @Size(min = 1) String lineCode) {
        return routeResponse(lineService.fetchLine(lineCode).getRoutes());
    }

    @PATCH
    @Transactional
    @Path("/{lineCode}")
    public Response update(@PathParam("lineCode") @Size(min = 1) String lineCode,
                           @NotNull LineDTO lineDTO) {
        int operatorId = lineDTO.getOperatorId();
        Collection<String> routeCodes = lineDTO.getRouteCodes();

        lineService.updateLine(lineCode, operatorId, routeCodes);
        return Response.ok().build();
    }

    @DELETE
    @Transactional
    @Path("/{lineCode}")
    public Response delete(@PathParam("lineCode") @Size(min = 1) String lineCode) {
        lineService.removeLine(lineCode);
        return Response.noContent().build();
    }

    @DELETE
    @Transactional
    @Path("/{lineCode}/routes/{routeCodeId}")
    public Response detachRoute(@PathParam("lineCode") @Size(min = 1) String lineCode,
                                @PathParam("routeCodeId") @Size(min = 1) List<String> routeCodes) {
        lineService.detachRoute(lineCode, routeCodes);
        return Response.ok().build();
    }
}
