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
package eu.socialedge.hermes.domain.timetable;

import eu.socialedge.hermes.domain.timetable.dto.StopSpecification;
import eu.socialedge.hermes.domain.timetable.dto.StopSpecificationMapper;
import eu.socialedge.hermes.domain.timetable.dto.TripSpecification;
import eu.socialedge.hermes.domain.timetable.dto.TripSpecificationMapper;
import eu.socialedge.hermes.domain.infrastructure.StationId;

import org.junit.Test;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class TripSpecificationMapperTest {

    private StopSpecificationMapper stopSpecMapper = new StopSpecificationMapper();

    private TripSpecificationMapper tripDataMapper
            = new TripSpecificationMapper(stopSpecMapper);

    @Test
    public void testToData() {
        Trip trip = new Trip(TripId.of("tripId"), new HashSet<Stop>() {{
            add(new Stop(StationId.of("stationId1"), LocalTime.now().minusHours(7), LocalTime.now().minusHours(6)));
            add(new Stop(StationId.of("stationId2"), LocalTime.now().minusHours(4), LocalTime.now().minusHours(3)));
            add(new Stop(StationId.of("stationId3"), LocalTime.now().minusHours(2), LocalTime.now().minusHours(1)));
        }});

        TripSpecification data = tripDataMapper.toDto(trip);

        assertEquals(trip.id().toString(), data.id);
        assertEquals(trip.stops(), data.stops.stream()
                .map(stopSpecMapper::fromDto).collect(Collectors.toSet()));
    }

    @Test
    public void testFromData() {
        TripSpecification data = new TripSpecification();
        data.id = "tripId";
        data.stops = new HashSet<StopSpecification>() {{
            add(new StopSpecification() {{
                stationId = "stationId1";
                arrival = "12:20";
                departure = "12:21";
            }});
            add(new StopSpecification() {{
                stationId = "stationId2";
                arrival = "13:20";
                departure = "13:21";
            }});
            add(new StopSpecification() {{
                stationId = "stationId3";
                arrival = "14:20";
                departure = "14:21";
            }});
        }};

        Trip trip = tripDataMapper.fromDto(data);


        assertEquals(data.id, trip.id().toString());
        assertEquals(data.stops, trip.stops().stream()
                .map(stopSpecMapper::toDto).collect(Collectors.toSet()));
    }
}
