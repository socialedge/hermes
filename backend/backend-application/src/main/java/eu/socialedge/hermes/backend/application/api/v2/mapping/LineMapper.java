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
import eu.socialedge.hermes.backend.transit.domain.provider.Agency;
import eu.socialedge.hermes.backend.transit.domain.service.Line;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;

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

        return new LineDTO()
            .id(line.getId())
            .name(line.getName())
            .description(line.getDescription())
            .vehicleType(line.getVehicleType().name())
            .agencyId(line.getAgency().getId())
            .outboundRoute(routeMapper.toDTO(line.getOutboundRoute()))
            .inboundRoute(routeMapper.toDTO(line.getInboundRoute()))
            .url(line.getUrl() != null ? line.getUrl().toString() : null);
    }

    @Override
    public Line toDomain(LineDTO dto) {
        if (dto == null)
            return null;

        try {
            return new Line.Builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .vehicleType(dto.getVehicleType())
                .agency(Entities.proxy(Agency.class, dto.getAgencyId()))
                .outboundRoute(routeMapper.toDomain(dto.getOutboundRoute()))
                .inboundRoute(routeMapper.toDomain(dto.getInboundRoute()))
                .url(dto.getUrl())
                .build();
        } catch (MalformedURLException e) {
            throw new MappingException("Failed to map dto to line entity", e);
        } catch (ReflectiveOperationException e) {
            throw new MappingException("Failed to create proxy Agency entity", e);
        }
    }
}
