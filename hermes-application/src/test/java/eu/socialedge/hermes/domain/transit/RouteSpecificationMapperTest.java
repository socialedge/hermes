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
package eu.socialedge.hermes.domain.transit;

import eu.socialedge.hermes.domain.transit.dto.RouteSpecification;
import eu.socialedge.hermes.domain.transit.dto.RouteSpecificationMapper;
import eu.socialedge.hermes.domain.infrastructure.StationId;

import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class RouteSpecificationMapperTest {

    private RouteSpecificationMapper routeDataMapper = new RouteSpecificationMapper();

    @Test
    public void testToData() {
        Route route = new Route(RouteId.of("routeId"), Arrays.asList(StationId.of("station1"), StationId.of("station2")));

        RouteSpecification data = routeDataMapper.toDto(route);

        assertEquals(route.id().toString(), data.id);
        assertEquals(route.stationIds(), data.stationIds.stream().map(StationId::of).collect(Collectors.toList()));
    }

    @Test
    public void testFromData() {
        RouteSpecification data = new RouteSpecification();
        data.id = "routeId";
        data.stationIds = Arrays.asList("station1", "station2");

        Route route = routeDataMapper.fromDto(data);

        assertEquals(data.id, route.id().toString());
        assertEquals(data.stationIds.stream().map(StationId::of).collect(Collectors.toList()), route.stationIds());
    }
}
