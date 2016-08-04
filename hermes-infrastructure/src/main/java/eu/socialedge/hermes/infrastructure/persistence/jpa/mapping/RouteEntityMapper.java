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
package eu.socialedge.hermes.infrastructure.persistence.jpa.mapping;

import eu.socialedge.hermes.domain.infrastructure.StationId;
import eu.socialedge.hermes.domain.transit.Route;
import eu.socialedge.hermes.domain.transit.RouteId;
import eu.socialedge.hermes.infrastructure.persistence.jpa.entity.JpaRoute;
import eu.socialedge.hermes.infrastructure.persistence.jpa.entity.JpaStation;
import eu.socialedge.hermes.infrastructure.persistence.jpa.entity.JpaWaypoint;
import eu.socialedge.hermes.infrastructure.persistence.jpa.repository.entity.SpringJpaStationRepository;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.inject.Inject;

@Component
public class RouteEntityMapper implements EntityMapper<Route, JpaRoute> {

    private final SpringJpaStationRepository jpaStationRepository;

    @Inject
    public RouteEntityMapper(SpringJpaStationRepository jpaStationRepository, StationEntityMapper stationEntityMapper) {
        this.jpaStationRepository = jpaStationRepository;
    }

    @Override
    public JpaRoute mapToEntity(Route route) {
        JpaRoute jpaRoute = new JpaRoute();

        SortedSet<JpaWaypoint> waypoints = new TreeSet<>();

        int posPointer = 0;
        for (StationId stationId : route) {
            JpaWaypoint jpaWaypoint = new JpaWaypoint();
            jpaWaypoint.station(findStationById(stationId));
            jpaWaypoint.position(posPointer++);

            waypoints.add(jpaWaypoint);
        }

        jpaRoute.waypoints(waypoints);
        jpaRoute.routeId(route.id().toString());

        return jpaRoute;
    }

    @Override
    public Route mapToDomain(JpaRoute jpaRoute) {
        RouteId routeId = RouteId.of(jpaRoute.routeId());

        Collection<StationId> stationIds = jpaRoute.waypoints().stream()
                .map(wp -> wp.station().stationId())
                .map(st -> StationId.of(st))
                .collect(Collectors.toList());

        return new Route(routeId, stationIds);
    }

    private JpaStation findStationById(StationId stationId) {
        return jpaStationRepository.findOne(stationId.toString());
    }
}
