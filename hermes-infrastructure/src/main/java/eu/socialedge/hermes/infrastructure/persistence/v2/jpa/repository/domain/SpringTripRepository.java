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

import eu.socialedge.hermes.domain.v2.infrastructure.StationId;
import eu.socialedge.hermes.domain.v2.routing.RouteId;
import eu.socialedge.hermes.domain.v2.schedule.Trip;
import eu.socialedge.hermes.domain.v2.schedule.TripId;
import eu.socialedge.hermes.domain.v2.schedule.TripRepository;
import eu.socialedge.hermes.infrastructure.persistence.v2.jpa.entity.EntityMapper;
import eu.socialedge.hermes.infrastructure.persistence.v2.jpa.entity.JpaRoute;
import eu.socialedge.hermes.infrastructure.persistence.v2.jpa.entity.JpaStation;
import eu.socialedge.hermes.infrastructure.persistence.v2.jpa.entity.JpaTrip;
import eu.socialedge.hermes.infrastructure.persistence.v2.jpa.repository.entity.SpringJpaRouteRepository;
import eu.socialedge.hermes.infrastructure.persistence.v2.jpa.repository.entity.SpringJpaStationRepository;
import eu.socialedge.hermes.infrastructure.persistence.v2.jpa.repository.entity.SpringJpaTripRepository;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.function.Function;

@Component
public class SpringTripRepository extends SpringRepository<Trip, TripId,
                                                        JpaTrip, String>
                                            implements TripRepository {
    private final SpringJpaStationRepository stationRepository;
    private final SpringJpaRouteRepository routeRepository;

    @Inject
    public SpringTripRepository(SpringJpaTripRepository jpaRepository,
                                SpringJpaStationRepository stationRepository,
                                SpringJpaRouteRepository routeRepository) {
        super(jpaRepository);
        this.stationRepository = stationRepository;
        this.routeRepository = routeRepository;
    }

    @Override
    protected TripId extractDomainId(Trip domainObject) {
        return domainObject.tripId();
    }

    @Override
    protected String mapToJpaEntityId(TripId domainId) {
        return domainId.toString();
    }

    @Override
    protected Trip mapToDomainObject(JpaTrip jpaEntity) {
        return EntityMapper.mapEntityToTrip(jpaEntity);
    }

    @Override
    protected JpaTrip mapToJpaEntity(Trip domainObject) {
        return EntityMapper.mapTripToEntity(domainObject,
                routeId -> routeRepository.findOne(routeId.toString()),
                stationId -> stationRepository.findOne(stationId.toString()));
    }
}
