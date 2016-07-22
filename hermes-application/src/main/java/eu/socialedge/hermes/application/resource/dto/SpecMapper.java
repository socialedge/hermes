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
package eu.socialedge.hermes.application.resource.dto;

import eu.socialedge.hermes.domain.infrastructure.*;
import eu.socialedge.hermes.domain.timetable.Departure;
import eu.socialedge.hermes.domain.timetable.Schedule;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;


public class SpecMapper {
    private static final Validator JSR303_VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();
    private static final boolean VALIDATE_BY_DEFAULT = false;

    public static LineSpec lineSpec(Line line, boolean validateSpec) {
        LineSpec lineSpec = new LineSpec() {{
            codeId = line.getCodeId();
            transportType = line.getTransportType().name();

            if (!line.getRoutes().isEmpty())
                routes = routeSpecs(line.getRoutes(), validateSpec);

            if (line.getOperator() != null)
                operator = operatorSpec(line.getOperator(), validateSpec);
        }};

        if (validateSpec) validate(lineSpec, LineSpec.class);

        return lineSpec;
    }

    public static LineSpec lineSpec(Line line) {
        return lineSpec(line, VALIDATE_BY_DEFAULT);
    }

    public static Collection<LineSpec> lineSpecs(Collection<Line> lines, boolean validateSpec) {
        return lines.stream().map(l -> lineSpec(l, validateSpec)).collect(Collectors.toList());
    }

    public static Collection<LineSpec> lineSpecs(Collection<Line> lines) {
        return lineSpecs(lines, VALIDATE_BY_DEFAULT);
    }

    public static LineRefSpec lineRefSpec(Line line, boolean validateSpec) {
        LineRefSpec lineRefSpec = new LineRefSpec() {{
            codeId = line.getCodeId();
            transportType = line.getTransportType().name();

            if (!line.getRoutes().isEmpty())
                routeCodes = line.getRoutes().stream().map(Route::getCodeId).collect(Collectors.toSet());

            if (line.getOperator() != null)
                operatorId = line.getOperator().getId();
        }};

        if (validateSpec) validate(lineRefSpec, LineRefSpec.class);

        return lineRefSpec;
    }

    public static LineRefSpec lineRefSpec(Line line) {
        return lineRefSpec(line, VALIDATE_BY_DEFAULT);
    }

    public static Collection<LineRefSpec> lineRefSpecs(Collection<Line> lines, boolean validateSpec) {
        return lines.stream().map(l -> lineRefSpec(l, validateSpec)).collect(Collectors.toList());
    }

    public static Collection<LineRefSpec> lineRefSpecs(Collection<Line> lines) {
        return lineRefSpecs(lines, VALIDATE_BY_DEFAULT);
    }

    public static RouteSpec routeSpec(Route route, boolean validateSpec) {
        RouteSpec routeSpec = new RouteSpec() {{
            codeId = route.getCodeId();

            if (!route.getWaypoints().isEmpty())
                waypoints = waypointSpecs(route.getWaypoints(), validateSpec);
        }};

        if (validateSpec) validate(routeSpec, RouteSpec.class);

        return routeSpec;
    }

    public static RouteSpec routeSpec(Route route) {
        return routeSpec(route, VALIDATE_BY_DEFAULT);
    }

    public static Collection<RouteSpec> routeSpecs(Collection<Route> routes, boolean validateSpec) {
        return routes.stream().map(r -> routeSpec(r, validateSpec)).collect(Collectors.toList());
    }

    public static Collection<RouteSpec> routeSpecs(Collection<Route> routes) {
        return routeSpecs(routes, VALIDATE_BY_DEFAULT);
    }

    public static DepartureSpec departureSpec(Departure departure, boolean validateSpec) {
        DepartureSpec departureSpec = new DepartureSpec() {{
            station = stationSpec(departure.getStation(), validateSpec);
            time = departure.getTime();
        }};

        if (validateSpec) validate(departureSpec, DepartureSpec.class);

        return departureSpec;
    }

    public static DepartureSpec departureSpec(Departure departure) {
        return departureSpec(departure, VALIDATE_BY_DEFAULT);
    }

    public static Collection<DepartureSpec> departureSpecs(Collection<Departure> departures, boolean validateSpec) {
        return departures.stream().map(d -> departureSpec(d, validateSpec)).collect(Collectors.toList());
    }

    public static Collection<DepartureSpec> departureSpecs(Collection<Departure> departures) {
        return departureSpecs(departures, VALIDATE_BY_DEFAULT);
    }

    public static DepartureRefSpec departureRefSpec(Departure departure, boolean validateSpec) {
        DepartureRefSpec departureRefSpec = new DepartureRefSpec() {{
            stationCodeId = departure.getStation().getCodeId();
            time = departure.getTime();
        }};

        if (validateSpec) validate(departureRefSpec, DepartureRefSpec.class);

        return departureRefSpec;
    }

    public static DepartureRefSpec departureRefSpec(Departure departure) {
        return departureRefSpec(departure, VALIDATE_BY_DEFAULT);
    }

    public static Collection<DepartureRefSpec> departureRefSpecs(Collection<Departure> departures, boolean validateSpec) {
        return departures.stream().map(d -> departureRefSpec(d, validateSpec)).collect(Collectors.toList());
    }

    public static Collection<DepartureRefSpec> departureRefSpecs(Collection<Departure> departures) {
        return departureRefSpecs(departures, VALIDATE_BY_DEFAULT);
    }

    public static StationSpec stationSpec(Station station, boolean validateSpec) {
        StationSpec stationSpec = new StationSpec() {{
            codeId = station.getCodeId();
            name = station.getName();
            transportType = station.getTransportType().name();
            position = positionSpec(station.getPosition(), validateSpec);
        }};

        if (validateSpec) validate(stationSpec, StationSpec.class);

        return stationSpec;
    }

    public static StationSpec stationSpec(Station station) {
        return stationSpec(station, VALIDATE_BY_DEFAULT);
    }

    public static Collection<StationSpec> stationSpecs(Collection<Station> stations, boolean validateSpec) {
        return stations.stream().map(s -> stationSpec(s, validateSpec)).collect(Collectors.toList());
    }

    public static Collection<StationSpec> stationSpecs(Collection<Station> stations) {
        return stationSpecs(stations, VALIDATE_BY_DEFAULT);
    }

    public static PositionSpec positionSpec(Position position, boolean validateSpec) {
        PositionSpec positionSpec = new PositionSpec() {{
            latitude = position.getLatitude();
            longitude = position.getLongitude();
        }};

        if (validateSpec) validate(positionSpec, PositionSpec.class);

        return positionSpec;
    }

    public static PositionSpec positionSpec(Position position) {
        return positionSpec(position, VALIDATE_BY_DEFAULT);
    }

    public static WaypointSpec waypointSpec(Waypoint waypoint, boolean validateSpec) {
        WaypointSpec waypointSpec = new WaypointSpec() {{
            station = stationSpec(waypoint.getStation(), validateSpec);
            position = waypoint.getPosition();
        }};

        if (validateSpec) validate(waypointSpec, WaypointSpec.class);

        return waypointSpec;
    }

    public static WaypointSpec waypointSpec(Waypoint waypoint) {
        return waypointSpec(waypoint, VALIDATE_BY_DEFAULT);
    }

    public static WaypointRefSpec waypointRefSpec(Waypoint waypoint, boolean validateSpec) {
        WaypointRefSpec waypointRefSpec = new WaypointRefSpec() {{
            stationCodeId = waypoint.getStation().getCodeId();
            position = waypoint.getPosition();
        }};

        if (validateSpec) validate(waypointRefSpec, WaypointRefSpec.class);

        return waypointRefSpec;
    }

    public static WaypointRefSpec waypointRefSpec(Waypoint waypoint) {
        return waypointRefSpec(waypoint, VALIDATE_BY_DEFAULT);
    }

    public static Collection<WaypointSpec> waypointSpecs(Collection<Waypoint> waypoints, boolean validateSpec) {
        return waypoints.stream().map(wp -> waypointSpec(wp, validateSpec)).collect(Collectors.toList());
    }

    public static Collection<WaypointSpec> waypointSpecs(Collection<Waypoint> waypoints) {
        return waypointSpecs(waypoints, VALIDATE_BY_DEFAULT);
    }

    public static Collection<WaypointRefSpec> waypointRefSpecs(Collection<Waypoint> waypoints, boolean validateSpec) {
        return waypoints.stream().map(wp -> waypointRefSpec(wp, validateSpec)).collect(Collectors.toList());
    }

    public static Collection<WaypointRefSpec> waypointRefSpecs(Collection<Waypoint> waypoints) {
        return waypointRefSpecs(waypoints, VALIDATE_BY_DEFAULT);
    }

    public static OperatorSpec operatorSpec(Operator operator, boolean validateSpec) {
        OperatorSpec operatorSpec = new OperatorSpec() {{
            id = operator.getId();
            name = operator.getName();
            description = operator.getDescription();

            if (operator.getWebsite() != null)
                website = operator.getWebsite().toString();

            if (operator.getPosition() != null)
                position = positionSpec(operator.getPosition(), validateSpec);
        }};

        if (validateSpec) validate(operatorSpec, OperatorSpec.class);

        return operatorSpec;
    }

    public static OperatorSpec operatorSpec(Operator operator) {
        return operatorSpec(operator, VALIDATE_BY_DEFAULT);
    }

    public static Collection<OperatorSpec> operatorSpecs(Collection<Operator> operators, boolean validateSpec) {
        return operators.stream().map(o -> operatorSpec(o, validateSpec)).collect(Collectors.toList());
    }

    public static Collection<OperatorSpec> operatorSpecs(Collection<Operator> operators) {
        return operatorSpecs(operators, VALIDATE_BY_DEFAULT);
    }

    public static ScheduleSpec scheduleSpec(Schedule schedule, boolean validateSpec) {
        ScheduleSpec scheduleSpec = new ScheduleSpec() {{
            id = schedule.getId();
            route = routeSpec(schedule.getRoute());
            name = schedule.getName();
            creationDate = schedule.getCreationDate();
            expirationDate = schedule.getExpirationDate();

            if (!schedule.getDepartures().isEmpty())
                departures = departureSpecs(schedule.getDepartures(), validateSpec);
        }};

        if (validateSpec) validate(scheduleSpec, ScheduleSpec.class);

        return scheduleSpec;
    }

    public static ScheduleSpec scheduleSpec(Schedule schedule) {
        return scheduleSpec(schedule, VALIDATE_BY_DEFAULT);
    }

    public static Collection<ScheduleSpec> scheduleSpecs(Collection<Schedule> schedules, boolean validateSpec) {
        return schedules.stream().map(s -> scheduleSpec(s, validateSpec)).collect(Collectors.toList());
    }

    public static Collection<ScheduleSpec> scheduleSpecs(Collection<Schedule> schedules) {
        return scheduleSpecs(schedules, VALIDATE_BY_DEFAULT);
    }

    public static ScheduleRefSpec scheduleRefSpec(Schedule schedule, boolean validateSpec) {
        ScheduleRefSpec scheduleRefSpec = new ScheduleRefSpec() {{
            id = schedule.getId();
            routeCode = schedule.getRoute().getCodeId();
            name = schedule.getName();
            creationDate = schedule.getCreationDate();
            expirationDate = schedule.getExpirationDate();

            if (!schedule.getDepartures().isEmpty())
                departures = departureRefSpecs(schedule.getDepartures(), validateSpec);
        }};

        if (validateSpec) validate(scheduleRefSpec, ScheduleRefSpec.class);

        return scheduleRefSpec;
    }

    public static ScheduleRefSpec scheduleRefSpec(Schedule schedule) {
        return scheduleRefSpec(schedule, VALIDATE_BY_DEFAULT);
    }

    public static Collection<ScheduleRefSpec> scheduleRefSpecs(Collection<Schedule> schedules, boolean validateSpec) {
        return schedules.stream().map(s -> scheduleRefSpec(s, validateSpec)).collect(Collectors.toList());
    }

    public static Collection<ScheduleRefSpec> scheduleRefSpecs(Collection<Schedule> schedules) {
        return scheduleRefSpecs(schedules, VALIDATE_BY_DEFAULT);
    }

    private static <T> void validate(T o, Class<T> clazz) {
        Set<ConstraintViolation<T>> errors = JSR303_VALIDATOR.validate(o);

        if (!errors.isEmpty())
            throw new ConstraintViolationException(errors);
    }
}
