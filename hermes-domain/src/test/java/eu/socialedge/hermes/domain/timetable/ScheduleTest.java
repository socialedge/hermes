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

import eu.socialedge.hermes.domain.infrastructure.StationId;
import eu.socialedge.hermes.domain.transit.RouteId;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ScheduleTest {

    private Set<Trip> trips = new HashSet<>();
    private Schedule schedule;

    @Before
    public void setUp() {
        trips.add(randomTrip());
        trips.add(randomTrip());
        trips.add(randomTrip());

        schedule = new Schedule(ScheduleId.of("scheduleId"), RouteId.of("routeId"),
                ScheduleAvailability.weekendDays(LocalDate.now().minusDays(3), LocalDate.now()), trips);
    }

    @Test
    public void testHasTripShouldReturnTrueIfContains() {
        trips.forEach(trip -> {
            assertTrue(schedule.hasTrip(trip));
        });
    }

    @Test
    public void testHasTripShouldReturnFalseIfNotContains() {
        trips.forEach(trip -> {
            assertFalse(schedule.hasTrip(randomTrip()));
        });
    }

    @Test
    public void testAddTripShouldContainTripAfterAdd() {
        Trip tripToAdd = randomTrip();

        schedule.addTrip(tripToAdd);

        assertTrue(trips.contains(tripToAdd));
    }

    @Test
    public void testRemoveTripShouldNotContainRemovedTrip() {
        Trip tripToRemove = randomTrip();
        trips.add(tripToRemove);

        schedule.removeTrip(tripToRemove);

        assertFalse(trips.contains(tripToRemove));
    }

    @Test
    public void testRemoveAllTripsShouldRemoveAllTrips() {
        schedule.removeAllTrips();

        assertTrue(trips.isEmpty());
    }

    @Test
    public void testIsEmptyShouldReturnFalseForNotEmpty() {
        assertFalse(schedule.isEmpty());
    }

    @Test
    public void testIsEmptyShouldReturnTrueForEmpty() {
        trips.clear();

        assertTrue(schedule.isEmpty());
    }

    private Trip randomTrip() {
        Collection<Stop> stops = new HashSet<>();
        int stopsCount = ThreadLocalRandom.current().nextInt(1, 20);
        for (int j = 0; j < stopsCount; j++) {
            stops.add(randomStop());
        }
        return new Trip(stops);
    }

    private Stop randomStop() {
        StationId stationId = StationId.of("stationId" + ThreadLocalRandom.current().nextInt());
        LocalTime arrival = LocalTime.now().minusMinutes(ThreadLocalRandom.current().nextInt());
        LocalTime departure = LocalTime.now().minusMinutes(ThreadLocalRandom.current().nextInt());
        return new Stop(stationId, arrival, departure);
    }
}
