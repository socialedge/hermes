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

import eu.socialedge.hermes.domain.transit.*;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.Collection;
import java.util.stream.Collectors;

import static eu.socialedge.hermes.util.Iterables.isNotEmpty;
import static eu.socialedge.hermes.util.Strings.isNotBlank;

@Component
public class TransitService {

    private final LineRepository lineRepository;
    private final RouteRepository routeRepository;
    private final LineMapper lineMapper;
    private final RouteMapper routeMapper;

    @Inject
    public TransitService(LineRepository lineRepository, RouteRepository routeRepository,
                          LineMapper lineMapper, RouteMapper routeMapper) {
        this.lineRepository = lineRepository;
        this.routeRepository = routeRepository;
        this.lineMapper = lineMapper;
        this.routeMapper = routeMapper;
    }

    public Collection<LineData> fetchAllLines() {
        return lineRepository.list().stream().map(lineMapper::toDto).collect(Collectors.toList());
    }

    public Collection<RouteData> fetchAllRoutes(LineId lineId) {
        return fetchLine(lineId).routeIds.stream()
                    .map(RouteId::of)
                    .map(this::fetchRoute)
                    .collect(Collectors.toList());
    }

    public LineData fetchLine(LineId lineId) {
        Line line = lineRepository.get(lineId).orElseThrow(()
                    -> new NotFoundException("Line not found. Id = " + lineId));

        return lineMapper.toDto(line);
    }

    public LineRepository lineRepository() {
        return lineRepository;
    }

    public RouteData fetchRoute(LineId lineId, RouteId routeId) {
        if (!fetchLine(lineId).routeIds.contains(routeId.toString()))
            throw new NotFoundException("Line doesn't contain route with id = " + routeId);

        return fetchRoute(routeId);
    }

    private RouteData fetchRoute(RouteId routeId) {
        Route route = routeRepository.get(routeId).orElseThrow(()
                -> new NotFoundException("Route not found. Id = " + routeId));

        return routeMapper.toDto(route);
    }

    public void createLine(LineData data) {
        lineRepository.add(lineMapper.fromDto(data));
    }

    public void createRoute(LineId lineId, RouteData data) {
        Route route = routeMapper.fromDto(data);
        routeRepository.add(route);

        LineData lineData = fetchLine(lineId);
        lineData.routeIds.add(route.id().toString());
        lineRepository.update(lineMapper.fromDto(lineData));
    }

    public void updateLine(LineId lineId, LineData data) {
        LineData persistedLineData = fetchLine(lineId);

        if (isNotBlank(data.name)) {
            persistedLineData.name = data.name;
        }

        if (isNotBlank(data.agencyId)) {
            persistedLineData.agencyId = data.agencyId;
        }

        if (isNotEmpty(data.routeIds)) {
            persistedLineData.routeIds = data.routeIds;
        }

        lineRepository.update(lineMapper.fromDto(persistedLineData));
    }

    public void updateRoute(LineId lineId, RouteId routeId, RouteData data) {
        RouteData persistedRouteData = fetchRoute(lineId, routeId);

        persistedRouteData.stationIds = data.stationIds;

        routeRepository.update(routeMapper.fromDto(persistedRouteData));
    }

    public void deleteLine(LineId lineId) {
        boolean wasRemoved = lineRepository.remove(lineId);
        if (!wasRemoved)
            throw new NotFoundException("Failed to find line to delete. Id = " + lineId);
    }

    public void deleteRoute(LineId lineId, RouteId routeId) {
        LineData lineData = fetchLine(lineId);

        if (!lineData.routeIds.contains(routeId.toString()))
            throw new NotFoundException("Line doesn't contain route with id = " + routeId);

        boolean wasRemoved = routeRepository.remove(routeId);

        if (!wasRemoved)
            throw new NotFoundException("Failed to find route to delete. Id = " + routeId);

        lineData.routeIds.remove(routeId.toString());
        lineRepository.update(lineMapper.fromDto(lineData));
    }
}