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
package eu.socialedge.hermes.application.domain.timetable.dto;

import eu.socialedge.hermes.application.domain.SpecificationMapper;
import eu.socialedge.hermes.domain.timetable.Stop;
import eu.socialedge.hermes.domain.timetable.Trip;
import eu.socialedge.hermes.domain.timetable.TripId;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

@Component
public class TripSpecificationMapper implements SpecificationMapper<TripSpecification, Trip> {

    private final StopSpecificationMapper stopSpecMapper;

    @Inject
    public TripSpecificationMapper(StopSpecificationMapper stopSpecMapper) {
        this.stopSpecMapper = stopSpecMapper;
    }

    public TripSpecification toDto(Trip trip) {
        TripSpecification tripSpec = new TripSpecification();

        tripSpec.id = trip.id().toString();
        tripSpec.stops = trip.stops().stream()
                .map(stopSpecMapper::toDto).collect(Collectors.toSet());

        return tripSpec;
    }

    public Trip fromDto(TripSpecification data) {
        Set<Stop> tripStops = data.stops.stream()
                .map(stopSpecMapper::fromDto).collect(Collectors.toSet());

        return new Trip(TripId.of(data.id), tripStops);
    }
}
