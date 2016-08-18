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
package eu.socialedge.hermes.application.domain.timetable;

import eu.socialedge.hermes.application.domain.DtoMapper;
import eu.socialedge.hermes.domain.timetable.Trip;
import eu.socialedge.hermes.domain.timetable.TripId;
import org.springframework.stereotype.Component;

@Component
public class TripMapper implements DtoMapper<TripData, Trip> {

    public TripData toDto(Trip trip) {
        TripData data = new TripData();

        data.tripId = trip.id().toString();
        data.stops = trip.stops();

        return data;
    }

    public Trip fromDto(TripData data) {
        TripId tripId = TripId.of(data.tripId);

        return new Trip(tripId, data.stops);
    }
}
