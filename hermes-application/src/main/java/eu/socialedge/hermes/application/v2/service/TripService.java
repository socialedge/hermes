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
package eu.socialedge.hermes.application.v2.service;

import eu.socialedge.hermes.application.v2.resource.spec.TripSpecification;
import eu.socialedge.hermes.domain.v2.transit.Stops;
import eu.socialedge.hermes.domain.v2.transit.Trip;
import eu.socialedge.hermes.domain.v2.transit.TripAvailability;
import eu.socialedge.hermes.domain.v2.transit.TripId;
import eu.socialedge.hermes.domain.v2.transit.TripRepository;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

import javax.inject.Inject;

import static java.util.Objects.isNull;
import static org.hibernate.internal.util.collections.CollectionHelper.isNotEmpty;

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

    public Optional<Trip> fetchTrip(TripId tripId) {
        return tripRepository.get(tripId);
    }

    public void createTrip(TripSpecification spec) {
        TripId tripId = TripId.of(spec.tripId);
        TripAvailability tripAvailability = spec.tripAvailability;
        Stops stops = new Stops(spec.stops);

        Trip trip = new Trip(tripId, tripAvailability, stops);

        tripRepository.save(trip);
    }

    public void updateTrip(TripSpecification spec) {
        TripId tripId = TripId.of(spec.tripId);

        Optional<Trip> persistedTripOpt = fetchTrip(tripId);
        if (!persistedTripOpt.isPresent())
            throw new ServiceException("Failed to find Trip to update. Id = " + tripId);

        Trip persistedTrip = persistedTripOpt.get();

        if (!isNull(spec.tripAvailability))
            persistedTrip.tripAvailability(spec.tripAvailability);

        if (isNotEmpty(spec.stops)) {
            persistedTrip.stops().clear();
            persistedTrip.stops().addAll(spec.stops);
        }

        tripRepository.save(persistedTrip);
    }

    public boolean deleteTrip(TripId tripId) {
        return tripRepository.remove(tripId);
    }
}
