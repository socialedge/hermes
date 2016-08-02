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
package eu.socialedge.hermes.infrastructure.persistence.v2.jpa.mapping;

import eu.socialedge.hermes.domain.v2.transit.Stops;
import eu.socialedge.hermes.domain.v2.transit.Trip;
import eu.socialedge.hermes.domain.v2.transit.TripAvailability;
import eu.socialedge.hermes.domain.v2.transit.TripId;
import eu.socialedge.hermes.infrastructure.persistence.v2.jpa.entity.JpaStop;
import eu.socialedge.hermes.infrastructure.persistence.v2.jpa.entity.JpaTrip;
import eu.socialedge.hermes.infrastructure.persistence.v2.jpa.entity.JpaTripAvailability;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.SortedSet;

@Component
public class TripEntityMapper implements EntityMapper<Trip, JpaTrip> {

    private final StopEntityMapper stopEntityMapper;
    private final TripAvailabilityEntityMapper availabilityEntityMapper;

    @Inject
    public TripEntityMapper(StopEntityMapper stopEntityMapper,
                            TripAvailabilityEntityMapper availabilityEntityMapper) {
        this.stopEntityMapper = stopEntityMapper;
        this.availabilityEntityMapper = availabilityEntityMapper;
    }


    @Override
    public JpaTrip mapToEntity(Trip trip) {
        TripId tripId = trip.tripId();
        SortedSet<JpaStop> jpaStops = stopEntityMapper.mapToEntity(trip.stops());
        JpaTripAvailability jpaTripAvailability = availabilityEntityMapper.mapToEntity(trip.tripAvailability());

        JpaTrip jpaTrip = new JpaTrip();
        jpaTrip.tripAvailability(jpaTripAvailability);
        jpaTrip.stops(jpaStops);

        return jpaTrip;
    }

    @Override
    public Trip mapToDomain(JpaTrip jpaTrip) {
        TripId tripId = TripId.of(jpaTrip.tripId());
        TripAvailability tripAvailability = availabilityEntityMapper.mapToDomain(jpaTrip.tripAvailability());
        Stops stops = stopEntityMapper.mapToDomain(jpaTrip.stops());

        return new Trip(tripId, tripAvailability, stops);
    }
}
