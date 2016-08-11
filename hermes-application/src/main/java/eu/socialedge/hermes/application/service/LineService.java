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

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import static eu.socialedge.hermes.util.Iterables.isNotEmpty;
import static eu.socialedge.hermes.util.Strings.isNotBlank;

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

    public Line fetchLine(LineId lineId) {
        return lineRepository.get(lineId).orElseThrow(()
                    -> new NotFoundException("Line not found. Id = " + lineId));
    }

    public void createLine(LineSpecification lineSpecification) {
        LineId lineId = LineId.of(lineSpecification.lineId);
        String name = lineSpecification.name;
        AgencyId agencyId = AgencyId.of(lineSpecification.agencyId);
        Set<RouteId> routeIds = lineSpecification.routeIds.stream()
                .map(RouteId::new)
                .collect(Collectors.toSet());

        Line line = new Line(lineId, agencyId, name, routeIds);

        lineRepository.add(line);
    }

    public void updateLine(LineId lineId, LineSpecification spec) {
        Line persistedLine = fetchLine(lineId);

        if (isNotBlank(spec.name)) persistedLine.name(spec.name);

        if (isNotBlank(spec.agencyId))
            persistedLine.agencyId(AgencyId.of(spec.agencyId));

        if (isNotEmpty(spec.routeIds)) {
            Set<RouteId> routeIds = spec.routeIds.stream()
                    .map(RouteId::new)
                    .collect(Collectors.toSet());

            persistedLine.attachedRouteIds().clear();
            persistedLine.attachedRouteIds().addAll(routeIds);
        }

        lineRepository.update(persistedLine);
    }

    public void deleteLine(LineId lineId) {
        boolean wasRemoved = lineRepository.remove(lineId);
        if (!wasRemoved)
            throw new NotFoundException("Failed to find line to delete. Id = " + lineId);
    }
}