/*
 * Hermes - The Municipal Transport Timetable System
 * Copyright (c) 2016-2017 SocialEdge
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
 *
 */
package eu.socialedge.hermes.backend.timetable;

import eu.socialedge.hermes.backend.schedule.domain.Availability;
import eu.socialedge.hermes.backend.schedule.domain.Schedule;
import eu.socialedge.hermes.backend.schedule.domain.Stop;
import eu.socialedge.hermes.backend.schedule.domain.Trip;
import eu.socialedge.hermes.backend.transit.domain.VehicleType;
import eu.socialedge.hermes.backend.transit.domain.geo.Location;
import eu.socialedge.hermes.backend.transit.domain.infra.Station;
import eu.socialedge.hermes.backend.transit.domain.provider.Agency;
import eu.socialedge.hermes.backend.transit.domain.service.Line;
import eu.socialedge.hermes.backend.transit.domain.service.Route;
import eu.socialedge.hermes.backend.transit.domain.service.Segment;
import lombok.val;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class DomainTestUtils {

    public static Station createStation() {
        return new Station.Builder()
            .name(UUID.randomUUID().toString())
            .vehicleType(VehicleType.TROLLEYBUS)
            .location(Location.of(1.0, 1.0))
            .dwell(Duration.ofSeconds(10))
            .build();
    }

    public static Line createLine(int inboundSize, int outboundSize) {
        val inboundStations = new ArrayList<Station>(inboundSize);
        val outboundStations = new ArrayList<Station>(outboundSize);
        for (int i = 0; i < inboundSize; i++) {
            inboundStations.add(createStation());
        }
        for (int i = 0; i < outboundSize; i++) {
            outboundStations.add(createStation());
        }
        return createLine(inboundStations, outboundStations);
    }

    public static Line createLine(List<Station> inboundStations, List<Station> outboundStations) {
        return new Line.Builder()
            .name(UUID.randomUUID().toString())
            .vehicleType(VehicleType.TROLLEYBUS)
            .inboundRoute(createRoute(inboundStations))
            .outboundRoute(createRoute(outboundStations))
            .agency(new Agency("Agency name"))
            .build();
    }

    public static Route createRoute(List<Station> stations) {
        return Route.of(formSegments(stations));
    }

    public static Schedule createSchedule(Line line, int tripsCount) {
        return new Schedule.Builder()
            .availability(Availability.workingDays(LocalDate.MIN, LocalDate.MAX))
            .line(line)
            .inboundTrips(createTrips(line.getInboundRoute().getStations(), tripsCount))
            .outboundTrips(createTrips(line.getOutboundRoute().getStations(), tripsCount))
            .build();
    }

    public static List<Trip> createTrips(List<Station> stations, int count) {
        val trips = new ArrayList<Trip>();
        LocalTime localTime = LocalTime.now();
        for (int i = 0; i < count; i++) {
            val stops = new ArrayList<Stop>();
            for (val station : stations) {
                stops.add(new Stop(localTime.plusMinutes(i), localTime.plusMinutes(i).plus(station.getDwell()), station));
            }
            trips.add(new Trip(stops));
            localTime = localTime.plusMinutes(10);
        }
        return trips;
    }

    private static List<Segment> formSegments(List<Station> stations) {
        val segments = new ArrayList<Segment>();
        for (int i = 0; i < stations.size() - 1; i++) {
            segments.add(new Segment(stations.get(i), stations.get(i + 1)));
        }
        return segments;
    }
}
