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
package eu.socialedge.hermes.application.domain.transit;

import eu.socialedge.hermes.application.domain.transit.dto.LineSpecification;
import eu.socialedge.hermes.application.domain.transit.dto.LineSpecificationMapper;
import eu.socialedge.hermes.application.domain.transit.dto.RouteSpecification;
import eu.socialedge.hermes.application.domain.transit.dto.RouteSpecificationMapper;
import eu.socialedge.hermes.domain.transit.Line;
import eu.socialedge.hermes.domain.transit.LineId;
import eu.socialedge.hermes.domain.transit.LineRepository;
import eu.socialedge.hermes.domain.transit.Route;
import eu.socialedge.hermes.domain.transit.RouteId;
import eu.socialedge.hermes.domain.transit.RouteRepository;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import static eu.socialedge.hermes.util.Iterables.isNotEmpty;
import static eu.socialedge.hermes.util.Strings.isNotBlank;

@Component
public class TransitService {

    private final LineRepository lineRepository;
    private final RouteRepository routeRepository;
    private final LineSpecificationMapper lineSpecificationMapper;
    private final RouteSpecificationMapper routeSpecificationMapper;

    @Inject
    public TransitService(LineRepository lineRepository, RouteRepository routeRepository,
                          LineSpecificationMapper lineSpecificationMapper, RouteSpecificationMapper routeSpecificationMapper) {
        this.lineRepository = lineRepository;
        this.routeRepository = routeRepository;
        this.lineSpecificationMapper = lineSpecificationMapper;
        this.routeSpecificationMapper = routeSpecificationMapper;
    }

    public Collection<LineSpecification> fetchAllLines() {
        return lineRepository.list().stream().map(lineSpecificationMapper::toDto).collect(Collectors.toList());
    }

    public Collection<RouteSpecification> fetchAllRoutes(LineId lineId) {
        return fetchLine(lineId).routeIds.stream()
                    .map(RouteId::of)
                    .map(this::fetchRoute)
                    .collect(Collectors.toList());
    }

    public LineSpecification fetchLine(LineId lineId) {
        Line line = lineRepository.get(lineId).orElseThrow(()
                    -> new NotFoundException("Line not found. Id = " + lineId));

        return lineSpecificationMapper.toDto(line);
    }

    public LineRepository lineRepository() {
        return lineRepository;
    }

    public RouteSpecification fetchRoute(LineId lineId, RouteId routeId) {
        if (!fetchLine(lineId).routeIds.contains(routeId.toString()))
            throw new NotFoundException("Line doesn't contain route with id = " + routeId);

        return fetchRoute(routeId);
    }

    private RouteSpecification fetchRoute(RouteId routeId) {
        Route route = routeRepository.get(routeId).orElseThrow(()
                -> new NotFoundException("Route not found. Id = " + routeId));

        return routeSpecificationMapper.toDto(route);
    }

    public void createLine(LineSpecification data) {
        lineRepository.add(lineSpecificationMapper.fromDto(data));
    }

    public void createRoute(LineId lineId, RouteSpecification data) {
        Route route = routeSpecificationMapper.fromDto(data);
        routeRepository.add(route);

        LineSpecification lineSpecification = fetchLine(lineId);
        lineSpecification.routeIds.add(route.id().toString());
        lineRepository.update(lineSpecificationMapper.fromDto(lineSpecification));
    }

    public void updateLine(LineId lineId, LineSpecification data) {
        LineSpecification persistedLineSpecification = fetchLine(lineId);

        if (isNotBlank(data.name)) {
            persistedLineSpecification.name = data.name;
        }

        if (isNotBlank(data.agencyId)) {
            persistedLineSpecification.agencyId = data.agencyId;
        }

        if (isNotEmpty(data.routeIds)) {
            persistedLineSpecification.routeIds = data.routeIds;
        }

        lineRepository.update(lineSpecificationMapper.fromDto(persistedLineSpecification));
    }

    public void updateRoute(LineId lineId, RouteId routeId, RouteSpecification data) {
        RouteSpecification persistedRouteSpecification = fetchRoute(lineId, routeId);

        persistedRouteSpecification.stationIds = data.stationIds;

        routeRepository.update(routeSpecificationMapper.fromDto(persistedRouteSpecification));
    }

    public void deleteLine(LineId lineId) {
        boolean wasRemoved = lineRepository.remove(lineId);
        if (!wasRemoved)
            throw new NotFoundException("Failed to find line to delete. Id = " + lineId);
    }

    public void deleteRoute(LineId lineId, RouteId routeId) {
        LineSpecification lineSpecification = fetchLine(lineId);

        if (!lineSpecification.routeIds.contains(routeId.toString()))
            throw new NotFoundException("Line doesn't contain route with id = " + routeId);

        boolean wasRemoved = routeRepository.remove(routeId);

        if (!wasRemoved)
            throw new NotFoundException("Failed to find route to delete. Id = " + routeId);

        lineSpecification.routeIds.remove(routeId.toString());
        lineRepository.update(lineSpecificationMapper.fromDto(lineSpecification));
    }
}