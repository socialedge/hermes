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

import eu.socialedge.hermes.domain.v2.transit.Trip;
import eu.socialedge.hermes.domain.v2.transit.TripId;
import eu.socialedge.hermes.domain.v2.transit.TripRepository;
import eu.socialedge.hermes.infrastructure.persistence.v2.jpa.entity.JpaTrip;
import eu.socialedge.hermes.infrastructure.persistence.v2.jpa.mapping.TripEntityMapper;
import eu.socialedge.hermes.infrastructure.persistence.v2.jpa.repository.entity
        .SpringJpaTripRepository;

import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class SpringTripRepository extends SpringRepository<Trip, TripId,
                                                        JpaTrip, String>
                                            implements TripRepository {

    private final TripEntityMapper tripEntityMapper;

    @Inject
    public SpringTripRepository(SpringJpaTripRepository jpaRepository,
                                TripEntityMapper tripEntityMapper) {
        super(jpaRepository);
        this.tripEntityMapper = tripEntityMapper;
    }

    @Override
    protected TripId extractDomainId(Trip trip) {
        return trip.tripId();
    }

    @Override
    protected String mapToJpaEntityId(TripId tripId) {
        return tripId.toString();
    }

    @Override
    protected Trip mapToDomainObject(JpaTrip jpaTrip) {
        return tripEntityMapper.mapToDomain(jpaTrip);
    }

    @Override
    protected JpaTrip mapToJpaEntity(Trip trip) {
        return tripEntityMapper.mapToEntity(trip);
    }
}
