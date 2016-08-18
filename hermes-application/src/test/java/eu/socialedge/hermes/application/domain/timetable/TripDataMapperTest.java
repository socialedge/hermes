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

import eu.socialedge.hermes.domain.infrastructure.StationId;
import eu.socialedge.hermes.domain.timetable.Stop;
import eu.socialedge.hermes.domain.timetable.Trip;
import eu.socialedge.hermes.domain.timetable.TripId;
import org.junit.Test;

import java.time.LocalTime;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;

public class TripDataMapperTest {

    private TripMapper tripDataMapper = new TripMapper();

    @Test
    public void testToData() {
        Trip trip = new Trip(TripId.of("tripId"), new HashSet<Stop>() {{
            add(new Stop(StationId.of("stationId1"), LocalTime.now().minusHours(7), LocalTime.now().minusHours(6)));
            add(new Stop(StationId.of("stationId2"), LocalTime.now().minusHours(4), LocalTime.now().minusHours(3)));
            add(new Stop(StationId.of("stationId3"), LocalTime.now().minusHours(2), LocalTime.now().minusHours(1)));
        }});

        TripData data = tripDataMapper.toDto(trip);

        assertEquals(trip.id().toString(), data.tripId);
        assertEquals(trip.stops(), data.stops);
    }

    @Test
    public void testFromData() {
        TripData data = new TripData();
        data.tripId = "tripId";
        data.stops = new HashSet<Stop>() {{
            add(new Stop(StationId.of("stationId1"), LocalTime.now().minusHours(7), LocalTime.now().minusHours(6)));
            add(new Stop(StationId.of("stationId2"), LocalTime.now().minusHours(4), LocalTime.now().minusHours(3)));
            add(new Stop(StationId.of("stationId3"), LocalTime.now().minusHours(2), LocalTime.now().minusHours(1)));
        }};

        Trip trip = tripDataMapper.fromDto(data);


        assertEquals(data.tripId, trip.id().toString());
        assertEquals(data.stops, trip.stops());
    }
}
