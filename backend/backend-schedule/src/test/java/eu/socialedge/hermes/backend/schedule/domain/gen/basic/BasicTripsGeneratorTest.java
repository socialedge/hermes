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
package eu.socialedge.hermes.backend.schedule.domain.gen.basic;

import eu.socialedge.hermes.backend.schedule.domain.Availability;
import eu.socialedge.hermes.backend.schedule.domain.Stop;
import eu.socialedge.hermes.backend.schedule.domain.Trip;
import eu.socialedge.hermes.backend.schedule.domain.gen.ScheduleGenerator;
import eu.socialedge.hermes.backend.schedule.domain.gen.TransitConstraints;
import eu.socialedge.hermes.backend.schedule.domain.gen.TripFactory;
import eu.socialedge.hermes.backend.transit.domain.VehicleType;
import eu.socialedge.hermes.backend.transit.domain.geo.Location;
import eu.socialedge.hermes.backend.transit.domain.infra.Station;
import eu.socialedge.hermes.backend.transit.domain.provider.Agency;
import eu.socialedge.hermes.backend.transit.domain.service.Line;
import eu.socialedge.hermes.backend.transit.domain.service.Route;
import eu.socialedge.hermes.backend.transit.domain.service.Segment;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import tec.uom.se.quantity.Quantities;
import tec.uom.se.unit.Units;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BasicTripsGeneratorTest {

    @Mock
    private TripFactory tripFactory;

    private ScheduleGenerator scheduleGenerator;

    private Station inboundStation1;
    private Station inboundStation2;
    private Station outboundStation1;
    private Station outboundStation2;

    private Line line;

    private final Availability availability = Availability.workingDays(LocalDate.now(), LocalDate.now().plusDays(1));

    @Before
    public void setUp() {
        val vehicleTypes = new HashSet<VehicleType>() {{
            add(VehicleType.BUS);
        }};
        inboundStation1 = new Station("inbound1", vehicleTypes, Location.of(1.1, 1.1), Duration.ofSeconds(10));
        inboundStation2 = new Station("inbound2", vehicleTypes, Location.of(2.2, 2.2), Duration.ofSeconds(10));
        outboundStation1 = new Station("outbound1", vehicleTypes, Location.of(1.1, 1.1), Duration.ofSeconds(20));
        outboundStation2 = new Station("outbound2", vehicleTypes, Location.of(2.2, 2.2), Duration.ofSeconds(20));

        val inboundRoute = new Route(Collections.singletonList(new Segment(inboundStation1, inboundStation2, Quantities.getQuantity(1000, Units.METRE))));
        val outboundRoute = new Route(Collections.singletonList(new Segment(outboundStation1, outboundStation2, Quantities.getQuantity(1000, Units.METRE))));
        line = new Line("line", VehicleType.BUS, inboundRoute, outboundRoute, new Agency("Agency"));

        when(tripFactory.create(any(LocalTime.class), eq(inboundRoute))).then(invocation -> {
            val arrivalTime = invocation.getArgumentAt(0, LocalTime.class);
            val stop1 = new Stop(arrivalTime, arrivalTime.plus(inboundStation1.getDwell()), inboundStation1);
            val stop2 = new Stop(arrivalTime, arrivalTime.plus(inboundStation2.getDwell()), inboundStation2);
            return new Trip(Arrays.asList(stop1, stop2));
        });

        when(tripFactory.create(any(LocalTime.class), eq(outboundRoute))).then(invocation -> {
            val arrivalTime = invocation.getArgumentAt(0, LocalTime.class);
            val stop1 = new Stop(arrivalTime, arrivalTime.plus(outboundStation1.getDwell()), outboundStation1);
            val stop2 = new Stop(arrivalTime, arrivalTime.plus(outboundStation2.getDwell()), outboundStation2);
            return new Trip(Arrays.asList(stop1, stop2));
        });

        scheduleGenerator = new BasicScheduleGenerator(tripFactory);
    }

    @Test
    public void shouldSetDescriptionWithoutChanges() {
        val description = "desc";

        val now = LocalTime.now();
        val transitConstrains = new TransitConstraints(now, now.plusHours(2), now, now.plusHours(2), Duration.ofMinutes(10), Duration.ofMinutes(1));

        val schedule = scheduleGenerator.generate(line, availability, description, transitConstrains);

        assertEquals(description, schedule.getDescription());
    }

    @Test
    public void shouldSetAvailabilityWithoutChanges() {
        val now = LocalTime.now();
        val transitConstrains = new TransitConstraints(now, now.plusHours(2), now, now.plusHours(2), Duration.ofMinutes(10), Duration.ofMinutes(1));

        val schedule = scheduleGenerator.generate(line, availability, transitConstrains);

        assertEquals(availability, schedule.getAvailability());
    }

    @Test
    public void shouldSetSameLineWithoutChanges() {
        val now = LocalTime.now();
        val transitConstrains = new TransitConstraints(now, now.plusHours(2), now, now.plusHours(2), Duration.ofMinutes(10), Duration.ofMinutes(1));

        val schedule = scheduleGenerator.generate(line, availability, transitConstrains);

        assertEquals(line, schedule.getLine());
    }

    @Test
    public void shouldCreateInboundTrips() {
        val now = LocalTime.now();
        val transitConstrains = new TransitConstraints(now, now.plusHours(2), now, now.plusHours(2), Duration.ofMinutes(10), Duration.ofMinutes(1));

        val schedule = scheduleGenerator.generate(line, availability, transitConstrains);

        assertFalse(schedule.getInboundTrips().isEmpty());
    }

    @Test
    public void shouldCreateOutboundTrips() {
        val now = LocalTime.now();
        val transitConstrains = new TransitConstraints(now, now.plusHours(2), now, now.plusHours(2), Duration.ofMinutes(10), Duration.ofMinutes(1));

        val schedule = scheduleGenerator.generate(line, availability, transitConstrains);

        assertFalse(schedule.getOutboundTrips().isEmpty());
    }

    @Test
    public void shouldAddOnlyInboundTripsIntoInboundBucket() {
        val now = LocalTime.now();
        val transitConstrains = new TransitConstraints(now, now.plusHours(2), now, now.plusHours(2), Duration.ofMinutes(10), Duration.ofMinutes(1));

        val schedule = scheduleGenerator.generate(line, availability, transitConstrains);

        val inboundTrips = schedule.getInboundTrips();
        for (val trip : inboundTrips) {
            assertEquals(2, trip.getStops().size());
            val tripStations = trip.getStops().stream().map(Stop::getStation).collect(Collectors.toList());
            assertTrue(tripStations.containsAll(Arrays.asList(inboundStation1, inboundStation2)));
        }
    }

    @Test
    public void shouldAddOnlyOutboundTripsIntoOutboundBucket() {
        val now = LocalTime.now();
        val transitConstrains = new TransitConstraints(now, now.plusHours(2), now, now.plusHours(2), Duration.ofMinutes(10), Duration.ofMinutes(1));

        val schedule = scheduleGenerator.generate(line, availability, transitConstrains);

        val inboundTrips = schedule.getOutboundTrips();
        for (val trip : inboundTrips) {
            assertEquals(2, trip.getStops().size());
            val tripStations = trip.getStops().stream().map(Stop::getStation).collect(Collectors.toList());
            assertTrue(tripStations.containsAll(Arrays.asList(outboundStation1, outboundStation2)));
        }
    }

    @Test
    public void shouldStartInboundTripsAtStartTimeInbound() {
        val now = LocalTime.now();
        val transitConstrains = new TransitConstraints(now, now.plusHours(2), now, now.plusHours(2), Duration.ofMinutes(10), Duration.ofMinutes(1));

        val schedule = scheduleGenerator.generate(line, availability, transitConstrains);

        val firstTrip = schedule.getInboundTrips().stream()
            .sorted(Comparator.comparing(this::getTripStartTime))
            .findFirst().get();
        assertEquals(now, getTripStartTime(firstTrip));
    }

    @Test
    public void shouldStartOutboundTripsAtStartTimeOutbound() {
        val now = LocalTime.now();
        val transitConstrains = new TransitConstraints(now, now.plusHours(2), now, now.plusHours(2), Duration.ofMinutes(10), Duration.ofMinutes(1));

        val schedule = scheduleGenerator.generate(line, availability, transitConstrains);

        val firstTrip = schedule.getOutboundTrips().stream()
            .sorted(Comparator.comparing(this::getTripStartTime))
            .findFirst().get();
        assertEquals(now, getTripStartTime(firstTrip));
    }

    @Test
    public void shouldNotPlaceInboundTripsAfterEndTimeInbound() {
        val now = LocalTime.now();
        val endTimeInbound = now.plusHours(2);
        val transitConstrains = new TransitConstraints(now, endTimeInbound, now, now.plusHours(2), Duration.ofMinutes(10), Duration.ofMinutes(1));

        val schedule = scheduleGenerator.generate(line, availability, transitConstrains);

        val lastTrip = schedule.getInboundTrips().stream()
            .sorted(Comparator.comparing(this::getTripStartTime).reversed())
            .findFirst().get();
        assertTrue(getTripStartTime(lastTrip).isBefore(endTimeInbound));
    }

    @Test
    public void shouldNotPlaceOutboundTripsAfterEndTimeOutbound() {
        val now = LocalTime.now();
        val endTimeOutbound = now.plusHours(2);
        val transitConstrains = new TransitConstraints(now, now.plusHours(2), now, endTimeOutbound, Duration.ofMinutes(10), Duration.ofMinutes(1));

        val schedule = scheduleGenerator.generate(line, availability, transitConstrains);

        val lastTrip = schedule.getOutboundTrips().stream()
            .sorted(Comparator.comparing(this::getTripStartTime).reversed())
            .findFirst().get();
        assertTrue(getTripStartTime(lastTrip).isBefore(endTimeOutbound));
    }

    @Test
    public void shouldUseHeadwayAsDurationBetweenTrips() {
        val now = LocalTime.now();
        val headway = Duration.ofMinutes(10);
        val transitConstrains = new TransitConstraints(now, now.plusHours(2), now, now.plusHours(2), headway, Duration.ofMinutes(1));

        val schedule = scheduleGenerator.generate(line, availability, transitConstrains);

        val inboundTrips = new ArrayList<Trip>(schedule.getInboundTrips());
        inboundTrips.sort(Comparator.comparing(this::getTripStartTime));
        for (int i = 0; i < inboundTrips.size() - 1; i++) {
            val currentTrip = inboundTrips.get(i);
            val nextTrip = inboundTrips.get(i + 1);
            assertEquals(headway, Duration.between(getTripStartTime(currentTrip), getTripStartTime(nextTrip)));
        }

        val outboundTrips = new ArrayList<Trip>(schedule.getOutboundTrips());
        outboundTrips.sort(Comparator.comparing(this::getTripStartTime));
        for (int i = 0; i < outboundTrips.size() - 1; i++) {
            val currentTrip = outboundTrips.get(i);
            val nextTrip = outboundTrips.get(i + 1);
            assertEquals(headway, Duration.between(getTripStartTime(currentTrip), getTripStartTime(nextTrip)));
        }
    }

    private LocalTime getTripStartTime(Trip trip) {
        return trip.getStops().stream().sorted(Comparator.comparing(Stop::getArrival)).map(Stop::getArrival).findFirst().get();
    }
}
