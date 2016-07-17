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
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class DTOMapper {
    private static final ModelMapper MODEL_MAPPER = new ModelMapper();

    public static LineDTO lineResponse(Line line) {
        LineDTO lineDTO = MODEL_MAPPER.map(line, LineDTO.class);
        lineDTO.setRouteCodes(line.getRoutes().stream().map(Route::getCodeId).collect(Collectors.toSet()));
        return lineDTO;
    }

    public static Collection<LineDTO> lineResponse(Collection<Line> lines) {
        return lines.stream().map(DTOMapper::lineResponse).collect(Collectors.toList());
    }

    public static OperatorDTO operatorResponse(Operator operator) {
        return MODEL_MAPPER.map(operator, OperatorDTO.class);
    }

    public static Collection<OperatorDTO> operatorResponse(Collection<Operator> operators) {
        return operators.stream().map(DTOMapper::operatorResponse).collect(Collectors.toList());
    }

    public static RouteDTO routeResponse(Route route) {
        return MODEL_MAPPER.map(route, RouteDTO.class);
    }

    public static Collection<RouteDTO> routeResponse(Collection<Route> routes) {
        return routes.stream().map(DTOMapper::routeResponse).collect(Collectors.toList());
    }

    public static ScheduleDTO scheduleResponse(Schedule schedule) {
        return MODEL_MAPPER.map(schedule, ScheduleDTO.class);
    }

    public static Collection<ScheduleDTO> scheduleResponse(Collection<Schedule> schedules) {
        return schedules.stream().map(DTOMapper::scheduleResponse).collect(Collectors.toList());
    }

    public static StationDTO stationResponse(Station schedule) {
        return MODEL_MAPPER.map(schedule, StationDTO.class);
    }

    public static Collection<StationDTO> stationResponse(Collection<Station> stations) {
        return stations.stream().map(DTOMapper::stationResponse).collect(Collectors.toList());
    }

    public static WaypointDTO waypointResponse(Waypoint waypoint) {
        return MODEL_MAPPER.map(waypoint, WaypointDTO.class);
    }

    public static Collection<WaypointDTO> waypointResponse(Collection<Waypoint> waypoints) {
        return waypoints.stream().map(DTOMapper::waypointResponse).collect(Collectors.toList());
    }

    public static PositionDTO positionResponse(Position position) {
        return MODEL_MAPPER.map(position, PositionDTO.class);
    }

    public static Collection<PositionDTO> positionResponse(Collection<Position> position) {
        return position.stream().map(DTOMapper::positionResponse).collect(Collectors.toList());
    }

    public static DepartureDTO departureResponse(Departure departure) {
        return MODEL_MAPPER.map(departure, DepartureDTO.class);
    }

    public static Collection<DepartureDTO> departureResponse(Collection<Departure> departures) {
        return departures.stream().map(DTOMapper::departureResponse).collect(Collectors.toList());

    }
}
