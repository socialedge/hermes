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
package eu.socialedge.hermes.application.domain.transit;

import eu.socialedge.hermes.domain.infrastructure.StationId;
import eu.socialedge.hermes.domain.transit.Route;
import eu.socialedge.hermes.domain.transit.RouteId;

import java.util.stream.Collectors;

public class RouteDataMapper {

    public static RouteData toData(Route route) {
        RouteData data = new RouteData();

        data.routeId = route.id().toString();
        data.stationIds = route.stationIds().stream().map(StationId::toString).collect(Collectors.toList());

        return data;
    }

    public static Route fromData(RouteData data) {
        return new Route(RouteId.of(data.routeId),
                data.stationIds.stream().map(StationId::of).collect(Collectors.toList()));
    }
}
