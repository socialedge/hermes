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
import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class RouteDataMapperTest {

    private RouteMapper routeDataMapper = new RouteMapper();

    @Test
    public void testToData() {
        Route route = new Route(RouteId.of("routeId"), Arrays.asList(StationId.of("station1"), StationId.of("station2")));

        RouteData data = routeDataMapper.toDto(route);

        assertEquals(route.id().toString(), data.routeId);
        assertEquals(route.stationIds(), data.stationIds.stream().map(StationId::of).collect(Collectors.toList()));
    }

    @Test
    public void testFromData() {
        RouteData data = new RouteData();
        data.routeId = "routeId";
        data.stationIds = Arrays.asList("station1", "station2");

        Route route = routeDataMapper.fromDto(data);

        assertEquals(data.routeId, route.id().toString());
        assertEquals(data.stationIds.stream().map(StationId::of).collect(Collectors.toList()), route.stationIds());
    }
}
