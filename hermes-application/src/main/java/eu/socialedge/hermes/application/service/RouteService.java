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

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

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

    public Optional<Route> fetchRoute(RouteId routeId) {
        return routeRepository.get(routeId);
    }

    public void createRoute(RouteSpecification spec) {
        RouteId routeId = RouteId.of(spec.routeId);
        Collection<StationId> stationIds = spec.stationIds.stream()
                .map(StationId::of)
                .collect(Collectors.toList());

        Route route = new Route(routeId, stationIds);

        routeRepository.save(route);
    }

    public void updateRoute(RouteSpecification spec) {
        RouteId routeId = RouteId.of(spec.routeId);

        Optional<Route> persistedRouteOpt = fetchRoute(routeId);
        if (!persistedRouteOpt.isPresent())
            throw new ServiceException("Failed to find Route to update. Id = " + routeId);

        Route persistedRoute = persistedRouteOpt.get();

        persistedRoute.removeAllStations();

        spec.stationIds.stream()
                .map(StationId::of)
                .forEach(persistedRoute::appendStation);
        
        routeRepository.save(persistedRoute);
    }

    public boolean deleteRoute(RouteId routeId) {
        return routeRepository.remove(routeId);
    }
}
