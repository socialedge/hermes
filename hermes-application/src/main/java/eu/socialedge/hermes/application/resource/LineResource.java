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

import eu.socialedge.hermes.application.resource.ext.PATCH;
import eu.socialedge.hermes.application.resource.ext.Resource;
import eu.socialedge.hermes.domain.infrastructure.*;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Collection;

@Resource
@Path("/v1/lines")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LineResource {
    @Inject private LineRepository lineRepository;
    @Inject private RouteRepository routeRepository;
    @Inject private OperatorRepository operatorRepository;

    @GET
    public Collection<Line> read() {
        return lineRepository.list();
    }

    @GET
    @Path("/{lineCodeId}")
    public Line read(@PathParam("lineCodeId") @Size(min = 1) String lineCodeId) {
        return lineRepository.get(lineCodeId).orElseThrow(() ->
                new NotFoundException("No line was found with code + " + lineCodeId));
    }

    @GET
    @Path("/{lineCodeId}/routes")
    public Collection<Route> routes(@PathParam("lineCodeId") @Size(min = 1) String lineCodeId) {
        return read(lineCodeId).getRoutes();
    }

    @POST
    public Response create(@NotNull Line line, @Context UriInfo uriInfo) {
        Line createdLine = lineRepository.store(line);

        URI resourceUri = uriInfo.getAbsolutePathBuilder()
                .path(createdLine.getLineCodeId())
                .build();

        return Response.created(resourceUri).build();
    }

    @PATCH
    @Path("/{lineCodeId}")
    public Response update(@PathParam("lineCodeId") @Size(min = 1) String lineCodeId,
                           @NotNull LinePatch linePatch) {
        Line line = read(lineCodeId);

        if (linePatch.getOperatorId() != null) {
            line.setOperator(readOperator(linePatch.getOperatorId()));
        }

        lineRepository.store(line);

        return Response.ok().build();
    }

    @DELETE
    @Path("/{lineCodeId}")
    public Response delete(@PathParam("lineCodeId") @Size(min = 1) String lineCodeId) {
        lineRepository.remove(read(lineCodeId));

        return Response.noContent().build();
    }

    @PUT
    @Path("/{lineCodeId}/routes/{routeCodeId}")
    public Response attachRoute(@PathParam("lineCodeId") @Size(min = 1) String lineCodeId,
                                @PathParam("routeCodeId") @Size(min = 1) String routeCodeId) {
        Line line = read(lineCodeId);
        Route route = routeRepository.get(routeCodeId).orElseThrow(() ->
                new NotFoundException("No route was found with code + " + routeCodeId));

        line.addRoute(route);
        lineRepository.store(line);

        return Response.ok().build();
    }

    @DELETE
    @Path("/{lineCodeId}/routes/{routeCodeId}")
    public Response detachRoute(@PathParam("lineCodeId") @Size(min = 1) String lineCodeId,
                                @PathParam("routeCodeId") @Size(min = 1) String routeCodeId) {
        Line line = read(lineCodeId);
        Route route = routeRepository.get(routeCodeId).orElseThrow(() ->
                new NotFoundException("No route was found with code + " + routeCodeId));

        line.removeRoute(route);
        lineRepository.store(line);

        return Response.ok().build();
    }

    private Operator readOperator(Integer operatorId) {
        return operatorRepository.get(operatorId).orElseThrow(() ->
                new NotFoundException("No operator was found with id + " + operatorId));
    }

    private static class LinePatch {
        private TransportType transportType;
        private Integer operatorId;

        public TransportType getTransportType() {
            return transportType;
        }

        public Integer getOperatorId() {
            return operatorId;
        }
    }
}
