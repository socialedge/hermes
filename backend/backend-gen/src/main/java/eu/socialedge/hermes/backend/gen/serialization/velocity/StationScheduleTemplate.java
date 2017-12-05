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
package eu.socialedge.hermes.backend.gen.serialization.velocity;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import eu.socialedge.hermes.backend.schedule.domain.Schedule;
import eu.socialedge.hermes.backend.schedule.domain.Stop;
import eu.socialedge.hermes.backend.schedule.domain.Trip;
import eu.socialedge.hermes.backend.transit.domain.infra.Station;
import eu.socialedge.hermes.backend.transit.domain.service.Line;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;

import static java.util.stream.Collectors.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StationScheduleTemplate {
    private String lineName;
    private String vehicleType;
    private List<String> followingStations;
    private String firstStation;
    private String currentStation;
    private List<ScheduleTemplateData> schedules;

    public static StationScheduleTemplate from(Line line, Station station, Iterable<Schedule> schedules) {
        List<Station> stations;
        Function<Schedule, List<Trip>> tripsSupplier;
        if (line.getInboundRoute().getStations().contains(station)) {
            stations = line.getInboundRoute().getStations();
            tripsSupplier = Schedule::getInboundTrips;
        } else if (line.getOutboundRoute().getStations().contains(station)) {
            stations = line.getOutboundRoute().getStations();
            tripsSupplier = Schedule::getOutboundTrips;
        } else {
            throw new RuntimeException("Line " + line.getName() + " does not contain station " + station.getName());
        }

        val lineName = line.getName();
        val vehicleType = line.getVehicleType().name();
        val firstStation = stations.get(0).getName();
        val currentStation = station.getName();
        val followingStations = stations.subList(stations.indexOf(station) + 1, stations.size()).stream()
                .map(Station::getName).collect(toList());

        val scheduleData = new ArrayList<ScheduleTemplateData>();
        for (val schedule : schedules) {
            val availabilityString = formatAvailabilityDays(schedule.getAvailability().getAvailabilityDays());
            val stationStops = getStationStops(tripsSupplier.apply(schedule), station);
            val stationTimes = stationStops.stream()
                .map(Stop::getArrival)
                .collect(groupingBy(LocalTime::getHour, TreeMap::new,
                    mapping(LocalTime::getMinute, collectingAndThen(toList(), list -> list.stream().sorted().collect(toList())))));
            scheduleData.add(new ScheduleTemplateData(availabilityString, stationTimes));
        }

        return new StationScheduleTemplate(lineName, vehicleType, followingStations, firstStation, currentStation,
                scheduleData);
    }

    private static String formatAvailabilityDays(Iterable<DayOfWeek> days) {
        return StreamSupport.stream(days.spliterator(), false)
            .sorted()
            .map(day -> day.getDisplayName(TextStyle.SHORT, new Locale("uk", "UA")))
            .map(String::toUpperCase)
            .collect(joining(", "));
    }

    private static List<Stop> getStationStops(List<Trip> trips, Station station) {
        return trips.stream().flatMap(trip -> trip.getStops().stream())
                .filter(stop -> stop.getStation().equals(station)).collect(toList());
    }
}
