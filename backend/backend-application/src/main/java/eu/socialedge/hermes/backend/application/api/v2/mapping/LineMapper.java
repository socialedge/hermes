/*
 * Hermes - The Municipal Transport Timetable System
 * Copyright (c) 2016-2017 SocialEdge
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

package eu.socialedge.hermes.backend.application.api.v2.mapping;

import eu.socialedge.hermes.backend.application.api.dto.LineDTO;
import eu.socialedge.hermes.backend.application.api.v2.mapping.util.Entities;
import eu.socialedge.hermes.backend.transit.domain.VehicleType;
import eu.socialedge.hermes.backend.transit.domain.provider.Agency;
import eu.socialedge.hermes.backend.transit.domain.service.Line;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
public class LineMapper implements Mapper<Line, LineDTO> {

    private final RouteMapper routeMapper;

    @Autowired
    public LineMapper(RouteMapper routeMapper) {
        this.routeMapper = routeMapper;
    }

    @Override
    public LineDTO toDTO(Line line) {
        if (line == null)
            return null;

        val dto = new LineDTO();

        dto.setId(line.getId());
        dto.setName(line.getName());
        dto.setDescription(line.getDescription());
        dto.setVehicleType(line.getVehicleType().name());
        dto.setAgencyId(line.getAgency().getId());
        dto.setOutboundRoute(routeMapper.toDTO(line.getOutboundRoute()));

        if (line.getInboundRoute() != null)
            dto.setInboundRoute(routeMapper.toDTO(line.getInboundRoute()));

        if (line.getUrl() != null)
            dto.setUrl(line.getUrl().toString());

        return dto;
    }

    @Override
    public Line toDomain(LineDTO dto) {
        if (dto == null)
            return null;

        try {
            val id = dto.getId();
            val name = dto.getName();
            val desc = dto.getDescription();
            val vt = VehicleType.fromNameOrOther(dto.getVehicleType());
            val agency = agencyFromId(dto.getAgencyId());
            val outRoute = routeMapper.toDomain(dto.getOutboundRoute());
            val inRoute = routeMapper.toDomain(dto.getInboundRoute());
            val url = isBlank(dto.getUrl()) ? (URL) null : new URL(dto.getUrl());

            return new Line(id, name, desc, vt, outRoute, inRoute, agency, url);
        } catch (MalformedURLException e) {
            throw new MappingException("Failed to map dto to line entity", e);
        }
    }

    private Agency agencyFromId(String id) {
        try {
            return Entities.proxy(Agency.class, id);
        } catch (ReflectiveOperationException e) {
            throw new MappingException("Failed to create proxy Agency entity", e);
        }
    }
}
