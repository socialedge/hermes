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
package eu.socialedge.hermes.infrastructure.persistence.jpa.repository.domain;

import eu.socialedge.hermes.domain.transit.Route;
import eu.socialedge.hermes.domain.transit.RouteId;
import eu.socialedge.hermes.domain.transit.RouteRepository;
import eu.socialedge.hermes.infrastructure.persistence.jpa.entity.JpaRoute;
import eu.socialedge.hermes.infrastructure.persistence.jpa.mapping.RouteEntityMapper;
import eu.socialedge.hermes.infrastructure.persistence.jpa.repository.entity.SpringJpaRouteRepository;

import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class SpringRouteRepository extends SpringRepository<Route, RouteId,
                                                         JpaRoute, String>
                                                        implements RouteRepository {

    private final RouteEntityMapper routeEntityMapper;

    @Inject
    protected SpringRouteRepository(SpringJpaRouteRepository jpaRouteRepository,
                                    RouteEntityMapper routeEntityMapper) {
        super(jpaRouteRepository);
        this.routeEntityMapper = routeEntityMapper;
    }

    @Override
    protected String mapToJpaEntityId(RouteId routeId) {
        return routeId.toString();
    }

    @Override
    protected Route mapToDomainObject(JpaRoute jpaRoute) {
        return routeEntityMapper.mapToDomain(jpaRoute);
    }

    @Override
    protected JpaRoute mapToJpaEntity(Route route) {
        return routeEntityMapper.mapToEntity(route);
    }
}
