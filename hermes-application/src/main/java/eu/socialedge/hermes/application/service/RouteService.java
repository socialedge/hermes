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

import eu.socialedge.hermes.application.resource.spec.RouteSpecification;
import eu.socialedge.hermes.domain.infrastructure.StationId;
import eu.socialedge.hermes.domain.transit.Route;
import eu.socialedge.hermes.domain.transit.RouteId;
import eu.socialedge.hermes.domain.transit.RouteRepository;
import eu.socialedge.hermes.domain.transport.VehicleType;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

@Component
public class RouteService {

    private final RouteRepository routeRepository;

    @Inject
    public RouteService(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    public Collection<Route> fetchAllRoutes() {
        return routeRepository.list();
    }

    public Route fetchRoute(RouteId routeId) {
        return routeRepository.get(routeId).orElseThrow(()
                    -> new NotFoundException("Route not found. Id = " + routeId));
    }

    public void createRoute(RouteSpecification spec) {
        RouteId routeId = RouteId.of(spec.routeId);
        VehicleType vehicleType = VehicleType.valueOf(spec.vehicleType);
        List<StationId> stationIds = spec.stationIds.stream()
                .map(StationId::of)
                .collect(Collectors.toList());

        Route route = new Route(routeId, vehicleType, stationIds);

        routeRepository.add(route);
    }

    public void updateRoute(RouteId routeId, RouteSpecification spec) {
        Route persistedRoute = fetchRoute(routeId);

        persistedRoute.stationIds().clear();

        spec.stationIds.stream()
                .map(StationId::of)
                .forEach(st -> persistedRoute.stationIds().add(st));

        routeRepository.update(persistedRoute);
    }

    public void deleteRoute(RouteId routeId) {
        boolean wasRemoved = routeRepository.remove(routeId);

        if (!wasRemoved)
            throw new NotFoundException("Failed to find route to delete. Id = " + routeId);
    }
}
