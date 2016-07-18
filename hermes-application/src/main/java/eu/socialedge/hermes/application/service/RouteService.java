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

import eu.socialedge.hermes.application.exception.NotFoundException;
import eu.socialedge.hermes.domain.infrastructure.Route;
import eu.socialedge.hermes.domain.infrastructure.RouteRepository;
import eu.socialedge.hermes.domain.infrastructure.Station;
import eu.socialedge.hermes.domain.infrastructure.Waypoint;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.Validate.notNull;

@Component
@Transactional(readOnly = true)
public class RouteService {
    private final RouteRepository routeRepository;

    @Inject
    public RouteService(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    @Transactional
    public Route createLine(String routeCode, Collection<Waypoint> waypoints) {
        Route route = new Route(routeCode);

        if (waypoints != null && !waypoints.isEmpty())
            route.setWaypoints(waypoints);

        return routeRepository.store(route);
    }

    @Transactional
    public Waypoint createWaypoint(String routeCode, Station station, int position) {
        if (position <= 0)
            throw new IllegalArgumentException("Invalid position (not > 0)");

        Route route = fetchRoute(routeCode);

        Waypoint storedWaypoint = route.insertWaypoint(station, position);
        routeRepository.store(route);

        return storedWaypoint;
    }

    public Collection<Route> fetchAllRoutes() {
        return routeRepository.list();
    }

    public Route fetchRoute(String routeCode) {
        return routeRepository.get(notNull(routeCode)).orElseThrow(()
                -> new NotFoundException("No route was found with code = " + routeCode));
    }

    public Collection<Route> fetchRoutes(Collection<String> routeCodes) {
        if (notNull(routeCodes).isEmpty())
            return Collections.emptyList();

        return routeCodes.stream().map(this::fetchRoute).collect(Collectors.toSet());
    }

    public Collection<Waypoint> fetchWaypoints(String routeCode) {
        return fetchRoute(routeCode).getWaypoints();
    }

    @Transactional
    public void removeRoute(String routeCode) {
        routeRepository.remove(fetchRoute(routeCode));
    }

    @Transactional
    public void removeWaypoint(String routeCode, String stationCodeId) {
        Route route = fetchRoute(routeCode);

        if (!route.removeWaypoint(stationCodeId))
            throw new NotFoundException("No station on route found with code id = " + stationCodeId);

        routeRepository.store(route);
    }
}
