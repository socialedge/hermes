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
package eu.socialedge.hermes.infrastructure.persistence.v2.jpa.entity;

import eu.socialedge.hermes.domain.v2.infrastructure.Station;
import eu.socialedge.hermes.domain.v2.infrastructure.StationId;
import eu.socialedge.hermes.domain.v2.infrastructure.TransportType;
import eu.socialedge.hermes.domain.v2.operator.*;
import eu.socialedge.hermes.domain.v2.routing.*;
import eu.socialedge.hermes.domain.v2.schedule.Stop;
import eu.socialedge.hermes.domain.v2.schedule.Trip;
import eu.socialedge.hermes.domain.v2.schedule.TripAvailability;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class EntityMapper {

    public static JpaAgency mapAgencyToEntity(Agency agency) {
        JpaAgency jpaAgency = new JpaAgency();

        jpaAgency.agencyId(agency.agencyId().toString());

        jpaAgency.name(agency.name());
        jpaAgency.website(agency.website().toString());

        jpaAgency.timeZone(agency.timeZoneOffset().getTotalSeconds());
        jpaAgency.location(mapLocationToEntity(agency.location()));

        if (!isNull(agency.phone()))
            jpaAgency.phone(agency.phone().toString());
        if (!isNull(agency.email()))
            jpaAgency.email(agency.email().toString());

        return jpaAgency;
    }

    public static Agency mapEntityToAgency(JpaAgency jpaAgency) throws MalformedURLException {
        String agencyId = jpaAgency.agencyId();

        String name = jpaAgency.name();
        URL website = new URL(jpaAgency.website());

        ZoneOffset zoneOffset = ZoneOffset.ofTotalSeconds(jpaAgency.timeZone());
        Location location = mapEntityToLocation(jpaAgency.location());

        Agency agency = new Agency(agencyId, name, website, zoneOffset, location);

        if (!isBlank(jpaAgency.email()))
            agency.email(new Email(jpaAgency.email()));
        if (!isBlank(jpaAgency.phone()))
            agency.phone(new Phone(jpaAgency.phone()));

        return agency;
    }

    public static JpaStation mapStationToEntity(Station station) {
        JpaStation jpaStation = new JpaStation();

        jpaStation.stationId(station.stationId().toString());
        jpaStation.name(station.name());

        jpaStation.location(mapLocationToEntity(station.location()));
        jpaStation.transportTypes(station.transportTypes());

        return jpaStation;
    }

    public static Station mapEntityToStation(JpaStation jpaStation) {
        String stationId = jpaStation.stationId();
        String name = jpaStation.name();
        Location location = mapEntityToLocation(jpaStation.location());
        Set<TransportType> transportTypes = jpaStation.transportTypes();

        return new Station(stationId, name, location, transportTypes);
    }

    public static Route mapEntityToRoute(JpaRoute jpaRoute) {
        String routeId = jpaRoute.routeId();
        Waypoints waypoints = mapEntityToWaypoints(jpaRoute.waypoints());

        return new Route(routeId, waypoints);
    }

    public static JpaRoute mapRouteToEntity(Route route, Function<StationId, JpaStation> supplier) {
        String routeId = route.routeId().toString();
        Collection<JpaWaypoint> jpaWaypoints = mapWaypointToEntities(route.waypoints(), supplier);

        JpaRoute jpaRoute = new JpaRoute();
        jpaRoute.routeId(routeId);
        jpaRoute.waypoints(new TreeSet<>(jpaWaypoints));

        return jpaRoute;
    }


    public static Line mapEntityToLine(JpaLine jpaLine) {
        String lineId = jpaLine.lineId();
        AgencyId agencyId = AgencyId.of(jpaLine.agency().agencyId());
        TransportType transportType = jpaLine.transportType();
        Set<RouteId> routeIds = jpaLine.routes().stream()
                .map(JpaRoute::routeId)
                .map(RouteId::of)
                .collect(Collectors.toSet());

        return new Line(lineId, agencyId, transportType, routeIds);
    }

    public static JpaLine mapLineToEntity(Line line, Function<RouteId, JpaRoute> routeSupplier,
                                            Function<AgencyId, JpaAgency> agencySupplier) {
        String lineId = line.lineId().toString();
        JpaAgency jpaAgency = agencySupplier.apply(line.agencyId());
        TransportType transportType = line.transportType();
        Set<JpaRoute> jpaRoutes = line.routeIds().stream()
                .map(routeSupplier)
                .collect(Collectors.toSet());

        JpaLine jpaLine = new JpaLine();

        jpaLine.lineId(lineId);
        jpaLine.agency(jpaAgency);
        jpaLine.transportType(transportType);
        jpaLine.routes(jpaRoutes);

        return jpaLine;
    }

    public static Trip mapEntityToTrip(JpaTrip jpaTrip) {
        String tripId = jpaTrip.tripId();
        RouteId routeId = RouteId.of(jpaTrip.route().routeId());
        TripAvailability tripAvailability = mapEntityToTripAvailability(jpaTrip.tripAvailability());
        Collection<Stop> stops = mapEntityToStops(jpaTrip.stops(), jpaTrip.route().waypoints());

        return new Trip(tripId, routeId, tripAvailability, stops);
    }

    public static JpaTrip mapTripToEntity(Trip trip, Function<RouteId, JpaRoute> routeSupplier,
                                          Function<StationId, JpaStation> stationSupplier) {

        String tripId = trip.tripId().toString();
        JpaRoute jpaRoute = routeSupplier.apply(trip.routeId());
        Collection<JpaStop> jpaStops = mapStopToEntities(trip.stops(), stationSupplier);
        JpaTripAvailability jpaTripAvailability = mapTripAvailabilityToEntity(trip.serviceAvailability());

        JpaTrip jpaTrip = new JpaTrip();
        jpaTrip.tripId(tripId);
        jpaTrip.route(jpaRoute);
        jpaTrip.stops(jpaStops);
        jpaTrip.tripAvailability(jpaTripAvailability);
        return jpaTrip;
    }


    private static Waypoint mapEntityToWaypoint(JpaWaypoint jpaWaypoint) {
        StationId stationId = StationId.of(jpaWaypoint.station().stationId());
        int position = jpaWaypoint.position();

        return Waypoint.of(stationId, position);
    }

    private static Waypoints mapEntityToWaypoints(Collection<JpaWaypoint> jpaWaypoints) {
        return new Waypoints(jpaWaypoints.stream().map(EntityMapper::mapEntityToWaypoint).collect(Collectors.toSet()));
    }

    private static JpaWaypoint mapWaypointToEntity(Waypoint waypoint, Function<StationId, JpaStation> supplier) {
        int position = waypoint.position();
        JpaStation jpaStation = supplier.apply(waypoint.stationId());

        JpaWaypoint jpaWaypoint = new JpaWaypoint();
        jpaWaypoint.station(jpaStation);
        jpaWaypoint.position(position);

        return jpaWaypoint;
    }

    private static Collection<JpaWaypoint> mapWaypointToEntities(Waypoints waypoints, Function<StationId, JpaStation> supplier) {
        return waypoints.stream().map(wp -> mapWaypointToEntity(wp, supplier)).collect(Collectors.toSet());
    }

    private static Location mapEntityToLocation(JpaLocation jpaLocation) {
        return Location.of(jpaLocation.latitude(), jpaLocation.longitude());
    }

    private static JpaLocation mapLocationToEntity(Location location) {
        JpaLocation jpaLocation = new JpaLocation();

        jpaLocation.latitude(location.latitude());
        jpaLocation.longitude(location.longitude());

        return jpaLocation;
    }

    private static Stop mapEntityToStop(JpaStop jpaStop, Collection<JpaWaypoint> waypoints) {
        StationId stationId = StationId.of(jpaStop.station().stationId());
        Optional<JpaWaypoint> jpaWaypointOpt = waypoints.stream()
                .filter(wp -> wp.station().equals(jpaStop.station())).findFirst();
        if (!jpaWaypointOpt.isPresent())
            throw new RuntimeException("Cannot find a waypoint on station id = " + stationId);

        Waypoint waypoint = Waypoint.of(stationId, jpaWaypointOpt.get().position());
        LocalTime arrival = jpaStop.arrival();
        LocalTime departure = jpaStop.departure();

        return new Stop(waypoint, arrival, departure);
    }

    private static Collection<Stop> mapEntityToStops(Collection<JpaStop> jpaStops, Collection<JpaWaypoint> waypoints) {
        return jpaStops.stream().map(s -> mapEntityToStop(s, waypoints)).collect(Collectors.toSet());
    }

    private static JpaStop mapStopToEntity(Stop stop, Function<StationId, JpaStation> supplier) {
        LocalTime arrival = stop.arrival();
        LocalTime departure = stop.departure();
        JpaStation jpaStation = supplier.apply(stop.waypoint().stationId());

        JpaStop jpaStop = new JpaStop();

        jpaStop.station(jpaStation);
        jpaStop.arrival(arrival);
        jpaStop.departure(departure);

        return jpaStop;
    }

    private static Collection<JpaStop> mapStopToEntities(Collection<Stop> stops, Function<StationId, JpaStation> supplier) {
        return stops.stream().map(s -> mapStopToEntity(s, supplier)).collect(Collectors.toSet());
    }


    private static TripAvailability mapEntityToTripAvailability(JpaTripAvailability jpaAvailability) {
        TripAvailability.TripAvailabilityBuilder builder = TripAvailability.builder();

        if (jpaAvailability.isOnMondays()) builder.onMondays();
        if (jpaAvailability.isOnTuesdays()) builder.onTuesdays();
        if (jpaAvailability.isOnWednesdays()) builder.onWednesdays();
        if (jpaAvailability.isOnThursdays()) builder.onThursdays();
        if (jpaAvailability.isOnFridays()) builder.onFridays();
        if (jpaAvailability.isOnSaturdays()) builder.onSaturdays();
        if (jpaAvailability.isOnSundays()) builder.onSundays();

        builder.from(jpaAvailability.startDate());
        builder.to(jpaAvailability.endDate());
        builder.withExceptionDays(jpaAvailability.exceptionDays());

        return builder.build();
    }

    private static JpaTripAvailability mapTripAvailabilityToEntity(TripAvailability availability) {
        JpaTripAvailability jpaTripAvailability = new JpaTripAvailability();

        if (availability.isOnMondays()) jpaTripAvailability.onMondays();
        if (availability.isOnTuesdays()) jpaTripAvailability.onTuesdays();
        if (availability.isOnWednesdays()) jpaTripAvailability.onWednesdays();
        if (availability.isOnThursdays()) jpaTripAvailability.onThursdays();
        if (availability.isOnFridays()) jpaTripAvailability.onFridays();
        if (availability.isOnSaturdays()) jpaTripAvailability.onSaturdays();
        if (availability.isOnSundays()) jpaTripAvailability.onSundays();

        jpaTripAvailability.startDate(availability.startDate());
        jpaTripAvailability.endDate(availability.endDate());
        jpaTripAvailability.exceptionDays(availability.exceptionDays());

        return jpaTripAvailability;
    }
}
