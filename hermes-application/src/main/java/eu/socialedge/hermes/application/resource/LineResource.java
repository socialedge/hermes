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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static eu.socialedge.hermes.application.resource.dto.DTOMapper.lineResponse;
import static eu.socialedge.hermes.application.resource.dto.DTOMapper.routeResponse;

@Resource
@Path("/v1/lines")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional(readOnly = true)
public class LineResource {
    @Inject private LineRepository lineRepository;
    @Inject private RouteRepository routeRepository;
    @Inject private OperatorRepository operatorRepository;

    @POST
    @Transactional
    public Response create(@NotNull @Valid LineDTO lineDTO, @Context UriInfo uriInfo) {
        Line line = new Line(lineDTO.getCodeId(), lineDTO.getTransportType());

        Set<String> routeCodes = lineDTO.getRouteCodes();
        if (routeCodes != null && !routeCodes.isEmpty()) {
            Set<Route> routes = fetchRoutes(routeCodes);
            line.setRoutes(routes);
        }

        int operatorId = lineDTO.getOperatorId();
        if (operatorId > 0)
            line.setOperator(fetchOperator(operatorId));

        Line storedLine = lineRepository.store(line);
        return Response.created(uriInfo.getAbsolutePathBuilder()
                .path(storedLine.getCodeId())
                .build()).build();
    }

    @POST
    @Path("/{lineCodeId}/routes")
    @Transactional
    public Response attachRoute(@NotNull @Size(min = 1) String lineCodeId,
                                @NotNull @Size(min = 1) List<String> routeCodes) {
        Line line = fetchLine(lineCodeId);

        Set<Route> routes = fetchRoutes(routeCodes);
        line.getRoutes().addAll(routes);
        lineRepository.store(line);

        return Response.ok().build();
    }

    @GET
    public Collection<LineDTO> read() {
        return lineResponse(lineRepository.list());
    }

    @GET
    @Path("/{lineCodeId}")
    public LineDTO read(@PathParam("lineCodeId") @Size(min = 1) String lineCodeId) {
        return lineResponse(fetchLine(lineCodeId));
    }

    @GET
    @Path("/{lineCodeId}/routes")
    public Collection<RouteDTO> routes(@PathParam("lineCodeId") @Size(min = 1) String lineCodeId) {
        return routeResponse(fetchLine(lineCodeId).getRoutes());
    }

    @PATCH
    @Transactional
    @Path("/{lineCodeId}")
    public Response update(@PathParam("lineCodeId") @Size(min = 1) String lineCodeId,
                           @NotNull LineDTO lineDTO) {
        Line line = fetchLine(lineCodeId);
        boolean wasUpdated = false;

        int operatorId = lineDTO.getOperatorId();
        if (operatorId > 0) {
            line.setOperator(fetchOperator(operatorId));
            wasUpdated = true;
        }

        Set<String> routeCodes = lineDTO.getRouteCodes();
        if (routeCodes != null && !routeCodes.isEmpty()) {
            Set<Route> routes = fetchRoutes(routeCodes);
            line.setRoutes(routes);
            wasUpdated = true;
        }

        if (wasUpdated)
            lineRepository.store(line);

        return Response.ok().build();
    }

    @DELETE
    @Transactional
    @Path("/{lineCodeId}")
    public Response delete(@PathParam("lineCodeId") @Size(min = 1) String lineCodeId) {
        lineRepository.remove(fetchLine(lineCodeId));
        return Response.noContent().build();
    }

    @DELETE
    @Transactional
    @Path("/{lineCodeId}/routes/{routeCodeId}")
    public Response detachRoute(@PathParam("lineCodeId") @Size(min = 1) String lineCodeId,
                                @PathParam("routeCodeId") @Size(min = 1) List<String> routeCodes) {
        Line line = fetchLine(lineCodeId);


        Set<Route> routes = fetchRoutes(routeCodes);
        line.getRoutes().removeAll(routes);
        lineRepository.store(line);

        return Response.ok().build();
    }

    private Line fetchLine(String lineCodeId) {
        return lineRepository.get(lineCodeId).orElseThrow(() ->
                new NotFoundException("No line was found with code + " + lineCodeId));
    }

    private Operator fetchOperator(Integer operatorId) {
        return operatorRepository.get(operatorId).orElseThrow(() ->
                new NotFoundException("No operator was found with id + " + operatorId));
    }

    private Route fetchRoute(String routeCode) {
        return routeRepository.get(routeCode).orElseThrow(()
                -> new NotFoundException("No route was found with code = " + routeCode));
    }

    private Set<Route> fetchRoutes(Collection<String> routeCodes) {
        return routeCodes.stream().map(this::fetchRoute).collect(Collectors.toSet());
    }
}
