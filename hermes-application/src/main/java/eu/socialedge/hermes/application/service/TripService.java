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

import eu.socialedge.hermes.application.resource.spec.TripSpecification;
import eu.socialedge.hermes.domain.timetable.Trip;
import eu.socialedge.hermes.domain.timetable.TripId;
import eu.socialedge.hermes.domain.timetable.TripRepository;

import org.springframework.stereotype.Component;

import java.util.Collection;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import static eu.socialedge.hermes.util.Iterables.isNotEmpty;

@Component
public class TripService {

    private final TripRepository tripRepository;

    @Inject
    public TripService(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }
    
    public Collection<Trip> fetchAllTrips() {
        return tripRepository.list();
    }

    public Trip fetchTrip(TripId tripId) {
        return tripRepository.get(tripId).orElseThrow(()
                    -> new NotFoundException("Trip not found. Id = " + tripId));
    }

    public void createTrip(TripSpecification spec) {
        Trip trip = new Trip(TripId.of(spec.tripId), spec.stops);

        tripRepository.add(trip);
    }

    public void updateTrip(TripId tripId, TripSpecification spec) {
        Trip persistedTrip = fetchTrip(tripId);

        if (isNotEmpty(spec.stops)) {
            persistedTrip.stops().clear();
            persistedTrip.stops().addAll(spec.stops);
        }

        tripRepository.update(persistedTrip);
    }

    public void deleteTrip(TripId tripId) {
        boolean wasRemoved = tripRepository.remove(tripId);
        if (!wasRemoved)
            throw new NotFoundException("Failed to find trip to delete. Id = " + tripId);
    }
}