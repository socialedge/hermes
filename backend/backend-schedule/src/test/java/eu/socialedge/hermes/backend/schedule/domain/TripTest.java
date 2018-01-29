/*
 * Hermes - The Municipal Transport Timetable System
 * Copyright (c) 2016-2018 SocialEdge
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
package eu.socialedge.hermes.backend.schedule.domain;

import eu.socialedge.hermes.backend.transit.domain.VehicleType;
import eu.socialedge.hermes.backend.transit.domain.geo.Location;
import eu.socialedge.hermes.backend.transit.domain.infra.Station;
import lombok.val;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;

public class TripTest {

    @Test
    public void shouldReturnLatestArrivalTime() {
        val station = new Station("name", new HashSet<VehicleType>() {{add (VehicleType.BUS);}}, Location.of(1.1, 1.1), Duration.ofSeconds(1));
        val stops = new ArrayList<Stop>();
        val now = LocalTime.now();
        val firstStop = new Stop(now, now.plusSeconds(5), station);
        val someStop = new Stop(now.plusMinutes(4), now.plusMinutes(4).plusSeconds(4), station);
        val anotherStop = new Stop(now.plusMinutes(10), now.plusMinutes(10).plusSeconds(4), station);
        val lastStop = new Stop(now.plusMinutes(15), now.plusMinutes(15).plusSeconds(4), station);
        stops.add(firstStop);
        stops.add(lastStop);
        stops.add(someStop);
        stops.add(anotherStop);
        val trip = new Trip(stops);

        val result = trip.getArrivalTime();

        assertEquals(result, lastStop.getArrival());
    }
}
