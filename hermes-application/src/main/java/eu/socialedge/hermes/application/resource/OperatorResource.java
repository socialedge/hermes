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
import eu.socialedge.hermes.application.resource.dto.OperatorDTO;
import eu.socialedge.hermes.application.resource.dto.PositionDTO;
import eu.socialedge.hermes.application.resource.exception.BadRequestException;
import eu.socialedge.hermes.application.resource.exception.NotFoundException;
import eu.socialedge.hermes.domain.infrastructure.*;
import org.apache.commons.lang3.StringUtils;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import static eu.socialedge.hermes.application.resource.dto.DTOMapper.operatorResponse;

@Resource
@Path("/v1/operators")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional(readOnly = true)
public class OperatorResource {
    @Inject private OperatorRepository operatorRepository;
    @Inject private LineRepository lineRepository;

    @POST
    @Transactional
    public Response create(@NotNull @Valid OperatorDTO operatorDTO, @Context UriInfo uriInfo) {
        Operator operator = new Operator(operatorDTO.getName());

        String patchDescription = operatorDTO.getDescription();
        if (StringUtils.isNotBlank(patchDescription))
            operator.setDescription(patchDescription);

        String patchWebsite = operatorDTO.getWebsite();
        if (StringUtils.isNotBlank(patchWebsite))
            operator.setWebsite(toUrl(patchWebsite));

        PositionDTO patchPosition = operatorDTO.getPosition();
        if (patchPosition != null)
            operator.setPosition(Position.of(patchPosition.getLatitude(),
                                             patchPosition.getLongitude()));

        Operator storedOperator = operatorRepository.store(operator);
        return Response.created(uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(storedOperator.getId()))
                .build()).build();
    }

    @GET
    public Collection<OperatorDTO> list() {
        return operatorResponse(operatorRepository.list());
    }

    @GET
    @Path("/{operatorId}")
    public OperatorDTO read(@PathParam("operatorId") @Min(1) int operatorId) {
        return operatorResponse(fetchOperator(operatorId));
    }

    @GET
    @Path("/{operatorId}/lines")
    public Collection<Line> lines(@PathParam("operatorId") @Min(1) int operatorId) {
        return lineRepository.findByOperator(fetchOperator(operatorId));
    }

    @PATCH
    @Transactional
    @Path("/{operatorId}")
    public Response update(@PathParam("operatorId") @Min(1) int operatorId,
                           @NotNull OperatorDTO operatorDTO) {
        Operator operator = fetchOperator(operatorId);

        if (StringUtils.isNotBlank(operatorDTO.getName()))
            operator.setName(operatorDTO.getName());
        if (StringUtils.isNotBlank(operatorDTO.getDescription()))
            operator.setDescription(operatorDTO.getDescription());
        if (StringUtils.isNotBlank(operatorDTO.getWebsite()))
            operator.setWebsite(toUrl(operatorDTO.getWebsite()));

        PositionDTO patchPosition = operatorDTO.getPosition();
        if (operatorDTO.getPosition() != null) {
            Float latitude = patchPosition.getLatitude();
            Float longitude = patchPosition.getLongitude();

            if (latitude != null && longitude != null)
               operator.setPosition(Position.of(latitude, longitude));
        }

        operatorRepository.store(operator);
        return Response.ok().build();
    }

    @DELETE
    @Transactional
    @Path("/{operatorId}")
    public Response delete(@PathParam("operatorId") @Min(1) int operatorId) {
        operatorRepository.remove(fetchOperator(operatorId));
        return Response.noContent().build();
    }

    private Operator fetchOperator(int operatorId) {
        return operatorRepository.get(operatorId).orElseThrow(() ->
                new NotFoundException("No operator was found with id = " + operatorId));
    }

    private URL toUrl(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new BadRequestException("Invalid url was passed as web site address: " + url, e);
        }
    }
}
