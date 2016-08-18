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
package eu.socialedge.hermes.application.domain.transit.dto;

import eu.socialedge.hermes.application.domain.SpecificationMapper;
import eu.socialedge.hermes.domain.operator.AgencyId;
import eu.socialedge.hermes.domain.transit.Line;
import eu.socialedge.hermes.domain.transit.LineId;
import eu.socialedge.hermes.domain.transit.RouteId;
import eu.socialedge.hermes.domain.transport.VehicleType;

import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class LineSpecificationMapper implements SpecificationMapper<LineSpecification, Line> {

    public LineSpecification toDto(Line line) {
        LineSpecification data = new LineSpecification();

        data.id = line.id().toString();
        data.name = line.name();
        data.agencyId = line.agencyId().toString();
        data.routeIds = line.attachedRouteIds().stream()
                .map(RouteId::toString).collect(Collectors.toSet());
        data.vehicleType = line.vehicleType().name();

        return data;
    }

    public Line fromDto(LineSpecification data) {
        return new Line(LineId.of(data.id),
                AgencyId.of(data.agencyId),
                data.name,
                VehicleType.valueOf(data.vehicleType),
                data.routeIds.stream().map(RouteId::of).collect(Collectors.toSet()));
    }
}
