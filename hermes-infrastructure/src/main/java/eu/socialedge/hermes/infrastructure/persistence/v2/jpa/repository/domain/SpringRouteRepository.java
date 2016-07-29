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
package eu.socialedge.hermes.infrastructure.persistence.v2.jpa.repository.domain;

import eu.socialedge.hermes.domain.v2.routing.Route;
import eu.socialedge.hermes.domain.v2.routing.RouteId;
import eu.socialedge.hermes.domain.v2.routing.RouteRepository;
import eu.socialedge.hermes.infrastructure.persistence.v2.jpa.entity.EntityMapper;
import eu.socialedge.hermes.infrastructure.persistence.v2.jpa.entity.JpaRoute;
import eu.socialedge.hermes.infrastructure.persistence.v2.jpa.repository.entity.SpringJpaRouteRepository;
import eu.socialedge.hermes.infrastructure.persistence.v2.jpa.repository.entity.SpringJpaStationRepository;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class SpringRouteRepository extends SpringRepository<Route, RouteId,
                                                         JpaRoute, String>
                                            implements RouteRepository {

    private final SpringJpaStationRepository stationRepository;

    @Inject
    public SpringRouteRepository(SpringJpaRouteRepository jpaRepository,
                                 SpringJpaStationRepository stationRepository) {
        super(jpaRepository);
        this.stationRepository = stationRepository;
    }

    @Override
    protected RouteId extractDomainId(Route domainObject) {
        return domainObject.routeId();
    }

    @Override
    protected String mapToJpaEntityId(RouteId domainId) {
        return domainId.toString();
    }

    @Override
    protected Route mapToDomainObject(JpaRoute jpaEntity) {
        return EntityMapper.mapEntityToRoute(jpaEntity);
    }

    @Override
    protected JpaRoute mapToJpaEntity(Route domainObject) {
        return EntityMapper.mapRouteToEntity(domainObject,
                stationId -> stationRepository.findOne(stationId.toString()));
    }
}
