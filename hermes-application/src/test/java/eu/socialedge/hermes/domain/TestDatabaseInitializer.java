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
package eu.socialedge.hermes.domain;

import eu.socialedge.hermes.domain.contact.Email;
import eu.socialedge.hermes.domain.contact.Phone;
import eu.socialedge.hermes.domain.geo.Location;
import eu.socialedge.hermes.domain.infrastructure.Station;
import eu.socialedge.hermes.domain.infrastructure.StationId;
import eu.socialedge.hermes.domain.infrastructure.StationRepository;
import eu.socialedge.hermes.domain.operator.Agency;
import eu.socialedge.hermes.domain.operator.AgencyId;
import eu.socialedge.hermes.domain.operator.AgencyRepository;
import eu.socialedge.hermes.domain.timetable.*;
import eu.socialedge.hermes.domain.transit.*;
import eu.socialedge.hermes.domain.transport.VehicleType;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class TestDatabaseInitializer {

    private AgencyRepository agencyRepository;
    private LineRepository lineRepository;
    private RouteRepository routeRepository;
    private ScheduleRepository scheduleRepository;
    private StationRepository stationRepository;
    private TripRepository tripRepository;

    public static final int EACH_ENTITY_COUNT = 3;
    public static final String STATION_ID_BASE = "stationId";
    public static final String AGENCY_ID_BASE = "agencyId";
    public static final String TRIP_ID_BASE = "tripId";
    public static final String SCHEDULE_ID_BASE = "scheduleId";
    public static final String LINE_ID_BASE = "lineId";
    public static final String ROUTE_ID_BASE = "routeId";

    @Inject
    public TestDatabaseInitializer(AgencyRepository agencyRepository, LineRepository lineRepository,
                                   RouteRepository routeRepository, ScheduleRepository scheduleRepository,
                                   StationRepository stationRepository, TripRepository tripRepository) {
        this.agencyRepository = agencyRepository;
        this.lineRepository = lineRepository;
        this.routeRepository = routeRepository;
        this.scheduleRepository = scheduleRepository;
        this.stationRepository = stationRepository;
        this.tripRepository = tripRepository;
    }

    public void initialize() throws MalformedURLException {
        clear();

        for (int i = 0; i < EACH_ENTITY_COUNT; i++) {
            addStation(i);
            addAgency(i);
            addTrip(i);
            addRoute(i);
            addSchedule(i);
            addLine(i);
        }
    }

    public void clear() {
        agencyRepository.clear();
        lineRepository.clear();
        routeRepository.clear();
        scheduleRepository.clear();
        stationRepository.clear();
        tripRepository.clear();
    }

    public Station addStation(int index) {
        Set<VehicleType> vehicleTypes = new HashSet<>();
        vehicleTypes.add(VehicleType.TRAM);

        Station station = new Station(StationId.of(STATION_ID_BASE + index), "station" + index,
                Location.of(index, index), vehicleTypes);

        stationRepository.add(station);
        return station;
    }

    public Agency addAgency(int index) throws MalformedURLException {
        Agency agency = new Agency(AgencyId.of(AGENCY_ID_BASE + index), "agency" + index, new URL("http://google.com"),
                ZoneOffset.UTC, Location.of(index, index), Phone.of("+1 12345678910"), Email.of("email@gmail.com"));
        agencyRepository.add(agency);
        return agency;
    }

    public Trip addTrip(int index) {
        Stop stop = new Stop(StationId.of(STATION_ID_BASE + index), LocalTime.now().minusHours(20), LocalTime.now().minusHours(18));
        Trip trip = new Trip(TripId.of(TRIP_ID_BASE + index), new HashSet<Stop>() {{
            add(stop);
        }});
        tripRepository.add(trip);
        return trip;
    }

    public Route addRoute(int index) {
        Route route = new Route(RouteId.of(ROUTE_ID_BASE + index), Arrays.asList(StationId.of(STATION_ID_BASE + index)));
        routeRepository.add(route);
        return route;
    }

    public Schedule addSchedule(int index) {
        Schedule schedule = new Schedule(ScheduleId.of(SCHEDULE_ID_BASE + index), RouteId.of(ROUTE_ID_BASE + index),
                "schedule" + index, ScheduleAvailability.weekendDays(LocalDate.now().minusDays(index), LocalDate.now()),
                new HashSet<TripId>() {{
                    add(TripId.of(TRIP_ID_BASE + index));
                }});
        scheduleRepository.add(schedule);
        return schedule;
    }

    public Line addLine(int index) {
        Line line = new Line(LineId.of(LINE_ID_BASE + index), AgencyId.of(AGENCY_ID_BASE + index), "line" + index,
                VehicleType.BUS, "line" + index, new HashSet<RouteId>() {{
            add(RouteId.of(ROUTE_ID_BASE + index));
        }});
        lineRepository.add(line);
        return line;
    }

}
