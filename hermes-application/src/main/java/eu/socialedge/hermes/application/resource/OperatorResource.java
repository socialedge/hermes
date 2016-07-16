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

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Collection;

import eu.socialedge.hermes.application.resource.exception.BadRequestException;
import eu.socialedge.hermes.application.resource.ext.PATCH;
import eu.socialedge.hermes.application.resource.ext.Resource;
import eu.socialedge.hermes.domain.infrastructure.*;

import javax.inject.Inject;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Resource
@Path("/v1/operators")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OperatorResource {

    @Inject
    private OperatorRepository operatorRepository;

    @Inject
    private LineRepository lineRepository;

    @GET
    public Collection<Operator> list() {
        return operatorRepository.list();
    }

    @GET
    @Path("/{operatorId}")
    public Operator get(@PathParam("operatorId") @Min(1) int operatorId) {
        return operatorRepository.get(operatorId).orElseThrow(() ->
                new NotFoundException("No operator was found with id + " + operatorId));
    }

    @GET
    @Path("/{operatorId}/lines")
    public Collection<Line> lines(@PathParam("operatorId") @Min(1) int operatorId) {
        return lineRepository.findByOperator(get(operatorId));
    }

    @POST
    public Response create(@NotNull(message = "OperatorPatch") OperatorPatch operatorPatch, @Context UriInfo uriInfo) {
        Operator operator = new Operator(operatorPatch.getName());

        operator.setDescription(operatorPatch.getDescription());
        operator.setWebsite(toUrl(operatorPatch.getWebsite()));

        Position position = new Position(operatorPatch.getPosition().getLatitude(),
                operatorPatch.getPosition().getLongitude());

        operator.setPosition(position);

        operator = operatorRepository.store(operator);

        URI resourceUri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(operator.getOperatorId()))
                .build();

        return Response.created(resourceUri).build();
    }

    @PATCH
    @Path("/{operatorId}")
    public Response update(@PathParam("operatorId") @Min(1) int operatorId,
                           @NotNull(message = "OperatorPatch") OperatorPatch operatorPatch) {
        Operator operator = get(operatorId);

        if (operatorPatch.getName() != null) {
            operator.setName(operatorPatch.getName());
        }
        if (operatorPatch.getDescription() != null) {
            operator.setDescription(operatorPatch.getDescription());
        }
        if (operatorPatch.getWebsite() != null) {
            operator.setWebsite(toUrl(operatorPatch.getWebsite()));
        }
        if (operatorPatch.getPosition() != null) {
            operator.setPosition(operatorPatch.getPosition());
        }

        operatorRepository.store(operator);

        return Response.ok().build();
    }

    @DELETE
    @Path("/{operatorId}")
    public Response delete(@PathParam("operatorId") @Min(1) int operatorId) {
        operatorRepository.remove(get(operatorId));

        return Response.noContent().build();
    }

    private URL toUrl(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new BadRequestException("Invalid url was passed as web site address: " + url, e);
        }
    }

    private static class OperatorPatch {
        private String name;
        private String description;
        private String website;
        private Position position;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setWebsite(String website) {
            this.website = website;
        }

        public String getDescription() {
            return description;
        }

        public String getWebsite() {
            return website;
        }

        public Position getPosition() {
            return position;
        }

        public void setPosition(Position position) {
            this.position = position;
        }
    }
}
