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
import eu.socialedge.hermes.application.resource.spec.RouteSpecification;
import eu.socialedge.hermes.domain.infrastructure.StationId;
import eu.socialedge.hermes.domain.operator.AgencyId;
import eu.socialedge.hermes.domain.transit.*;

import eu.socialedge.hermes.domain.transport.VehicleType;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import static eu.socialedge.hermes.util.Iterables.isNotEmpty;
import static eu.socialedge.hermes.util.Strings.isNotBlank;

@Component
public class TransitService {

    private final LineRepository lineRepository;
    private final RouteRepository routeRepository;

    @Inject
    public TransitService(LineRepository lineRepository, RouteRepository routeRepository) {
        this.lineRepository = lineRepository;
        this.routeRepository = routeRepository;
    }

    public Collection<Line> fetchAllLines() {
        return lineRepository.list();
    }

    public Collection<Route> fetchAllRoutes(LineId lineId) {
        return fetchLine(lineId).attachedRouteIds().stream()
                    .map(this::fetchRoute)
                    .collect(Collectors.toList());
    }

    public Line fetchLine(LineId lineId) {
        return lineRepository.get(lineId).orElseThrow(()
                    -> new NotFoundException("Line not found. Id = " + lineId));
    }

    public LineRepository lineRepository() {
        return lineRepository;
    }

    public Route fetchRoute(LineId lineId, RouteId routeId) {
        if (!fetchLine(lineId).attachedRouteIds().contains(routeId))
            throw new NotFoundException("Line doesn't contain route with id = " + routeId);

        return fetchRoute(routeId);
    }

    private Route fetchRoute(RouteId routeId) {
        return routeRepository.get(routeId).orElseThrow(()
                -> new NotFoundException("Route not found. Id = " + routeId));
    }

    public void createLine(LineSpecification spec) {
        LineId lineId = LineId.of(spec.lineId);
        String name = spec.name;
        VehicleType vehicleType = VehicleType.valueOf(spec.vehicleType);
        AgencyId agencyId = AgencyId.of(spec.agencyId);
        Set<RouteId> routeIds = spec.routeIds.stream()
                .map(RouteId::new)
                .collect(Collectors.toSet());

        Line line = new Line(lineId, agencyId, name, vehicleType, routeIds);

        lineRepository.add(line);
    }

    public void createRoute(LineId lineId, RouteSpecification spec) {
        RouteId routeId = RouteId.of(spec.routeId);
        List<StationId> stationIds = spec.stationIds.stream()
                .map(StationId::of)
                .collect(Collectors.toList());

        Route route = new Route(routeId, stationIds);

        routeRepository.add(route);

        Line line = fetchLine(lineId);
        line.attachedRouteIds().add(route.id());
        lineRepository.update(line);
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

    public void updateRoute(LineId lineId, RouteId routeId, RouteSpecification spec) {
        Route persistedRoute = fetchRoute(lineId, routeId);

        persistedRoute.stationIds().clear();

        spec.stationIds.stream()
                .map(StationId::of)
                .forEach(st -> persistedRoute.stationIds().add(st));

        routeRepository.update(persistedRoute);
    }

    public void deleteLine(LineId lineId) {
        boolean wasRemoved = lineRepository.remove(lineId);
        if (!wasRemoved)
            throw new NotFoundException("Failed to find line to delete. Id = " + lineId);
    }

    public void deleteRoute(LineId lineId, RouteId routeId) {
        Line line = fetchLine(lineId);

        if (!line.attachedRouteIds().contains(routeId))
            throw new NotFoundException("Line doesn't contain route with id = " + routeId);

        boolean wasRemoved = routeRepository.remove(routeId);

        if (!wasRemoved)
            throw new NotFoundException("Failed to find route to delete. Id = " + routeId);

        line.attachedRouteIds().remove(routeId);
        lineRepository.update(line);
    }
}