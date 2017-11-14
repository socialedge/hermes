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

package eu.socialedge.hermes.backend.export.data;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import eu.socialedge.hermes.backend.schedule.domain.Schedule;
import eu.socialedge.hermes.backend.schedule.domain.Stop;
import eu.socialedge.hermes.backend.schedule.domain.Trip;
import eu.socialedge.hermes.backend.transit.domain.infra.Station;
import eu.socialedge.hermes.backend.transit.domain.service.Line;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StationScheduleTemplate {
    private String lineName;
    private String vehicleType;
    private List<String> followingStations;
    private String firstStation;
    private String currentStation;
    private List<ScheduleTemplateData> schedules;

    public static StationScheduleTemplate create(Station station, List<Schedule> schedules, Line line) {
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

        val lineId = line.getName();
        val vehicleType = line.getVehicleType().name();
        val firstStation = stations.get(0).getName();
        val currentStation = station.getName();
        val followingStations = stations.subList(stations.indexOf(station), stations.size() - 1).stream()
                .map(Station::getName).collect(Collectors.toList());

        val scheduleData = new ArrayList<ScheduleTemplateData>();
        for (Schedule schedule : schedules) {
            // TODO format beautifully
            val availabilityString = schedule.getAvailability().getAvailabilityDays().stream().map(Enum::toString)
                    .collect(Collectors.joining(", "));
            val times = getStationStops(tripsSupplier.apply(schedule), station).stream()
                    .map(Stop::getArrival).collect(Collectors.groupingBy(LocalTime::getHour, TreeMap::new,
                            Collectors.mapping(LocalTime::getMinute, Collectors.toList())));
            scheduleData.add(new ScheduleTemplateData(availabilityString, times));
        }

        return new StationScheduleTemplate(lineId, vehicleType, followingStations, firstStation, currentStation,
                scheduleData);
    }

    private static List<Stop> getStationStops(List<Trip> trips, Station station) {
        return trips.stream().flatMap(trip -> trip.getStops().stream())
                .filter(stop -> stop.getStation().equals(station)).collect(Collectors.toList());
    }
}
