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
package eu.socialedge.hermes.application.service;

import eu.socialedge.hermes.application.resource.spec.LineSpecification;
import eu.socialedge.hermes.domain.operator.AgencyId;
import eu.socialedge.hermes.domain.transit.Line;
import eu.socialedge.hermes.domain.transit.LineId;
import eu.socialedge.hermes.domain.transit.LineRepository;
import eu.socialedge.hermes.domain.transit.RouteId;
import eu.socialedge.hermes.domain.transport.VehicleType;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.hibernate.internal.util.collections.CollectionHelper.isNotEmpty;

@Component
public class LineService {
    private final LineRepository lineRepository;

    @Inject
    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public Collection<Line> fetchAllLines() {
        return lineRepository.list();
    }

    public Optional<Line> fetchLine(LineId lineId) {
        return lineRepository.get(lineId);
    }

    public void createLine(LineSpecification lineSpecification) {
        LineId lineId = LineId.of(lineSpecification.lineId);
        String name = lineSpecification.name;
        AgencyId agencyId = AgencyId.of(lineSpecification.agencyId);
        VehicleType vehicleType = VehicleType.valueOf(lineSpecification.vehicleType);
        Collection<RouteId> routeIds = lineSpecification.routeIds.stream()
                .map(RouteId::new)
                .collect(Collectors.toList());

        Line line = new Line(lineId, name, agencyId, vehicleType, routeIds);

        lineRepository.save(line);
    }

    public void updateLine(LineSpecification lineSpecification) {
        LineId lineId = LineId.of(lineSpecification.lineId);

        Optional<Line> persistedLineOpt = fetchLine(lineId);
        if (!persistedLineOpt.isPresent())
            throw new ServiceException("Failed to find Line to update. Id = " + lineId);

        Line persistedLine = persistedLineOpt.get();

        if (isNotBlank(lineSpecification.name))
            persistedLine.name(lineSpecification.name);

        if (isNotBlank(lineSpecification.agencyId))
            persistedLine.agencyId(AgencyId.of(lineSpecification.agencyId));

        if (isNotBlank(lineSpecification.vehicleType))
            persistedLine.vehicleType(VehicleType.valueOf(lineSpecification.vehicleType));

        if (isNotEmpty(lineSpecification.routeIds)) {
            Collection<RouteId> routeIds = lineSpecification.routeIds.stream()
                    .map(RouteId::new)
                    .collect(Collectors.toList());

            persistedLine.routeIds().clear();
            persistedLine.routeIds().addAll(routeIds);
        }

        lineRepository.save(persistedLine);
    }

    public boolean deleteLine(LineId lineId) {
        return lineRepository.remove(lineId);
    }
}
