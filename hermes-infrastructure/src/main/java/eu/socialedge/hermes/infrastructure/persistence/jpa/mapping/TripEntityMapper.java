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

import eu.socialedge.hermes.domain.timetable.Trip;
import eu.socialedge.hermes.infrastructure.persistence.jpa.entity.JpaTrip;

import org.springframework.stereotype.Component;

import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.inject.Inject;

@Component
public class TripEntityMapper implements EntityMapper<Trip, JpaTrip> {

    private final StopEntityMapper stopEntityMapper;

    @Inject
    public TripEntityMapper(StopEntityMapper stopEntityMapper) {
        this.stopEntityMapper = stopEntityMapper;
    }


    @Override
    public JpaTrip mapToEntity(Trip trip) {
        JpaTrip jpaTrip = new JpaTrip();

        jpaTrip.stops(trip.stream()
                .map(stopEntityMapper::mapToEntity)
                .collect(Collectors.toCollection(TreeSet::new)));

        return jpaTrip;
    }

    @Override
    public Trip mapToDomain(JpaTrip jpaTrip) {
        return new Trip(jpaTrip.stops().stream()
                .map(stopEntityMapper::mapToDomain)
                .collect(Collectors.toSet()));
    }
}
