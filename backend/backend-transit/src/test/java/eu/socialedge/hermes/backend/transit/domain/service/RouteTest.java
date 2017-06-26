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
 */

package eu.socialedge.hermes.backend.transit.domain.service;

import eu.socialedge.hermes.backend.transit.domain.Dwell;
import eu.socialedge.hermes.backend.transit.domain.Station;
import eu.socialedge.hermes.backend.transit.domain.VehicleType;
import eu.socialedge.hermes.backend.transit.domain.geo.Location;
import eu.socialedge.hermes.backend.transit.domain.service.Route;
import eu.socialedge.hermes.backend.transit.domain.service.Segment;
import lombok.val;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

public class RouteTest {

    private List<Segment> segments = new ArrayList<>();

    @Before
    public void setUp() {
        val station1 = new Station("stop1", new HashSet<>(singletonList(VehicleType.BUS)),
            new Location(1.1, 1.1), Arrays.asList(Dwell.allDayRegular(Duration.ofMinutes(1))));
        val station2 = new Station("stop2", new HashSet<>(singletonList(VehicleType.BUS)),
            new Location(1.2, 1.2), asList(Dwell.allDayRegular(Duration.ofMinutes(1))));
        val station3 = new Station("stop3", new HashSet<>(singletonList(VehicleType.BUS)),
            new Location(1.3, 1.3), asList(Dwell.allDayRegular(Duration.ofMinutes(1))));
        val station4 = new Station("stop3", new HashSet<>(singletonList(VehicleType.BUS)),
            new Location(1.4, 1.4), asList(Dwell.allDayRegular(Duration.ofMinutes(1))));

        segments.add(new Segment(station1, station2));
        segments.add(new Segment(station2, station3));
        segments.add(new Segment(station3, station4));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfSegmentsAreNotIntereconnected() {
        segments.add(new Segment(segments.get(0).getBegin(), segments.get(0).getEnd()));
        new Route(segments);
    }

    @Test
    public void shouldCreateRouteWithAllSegmentsInCorrectOrder() {
        val route = new Route(segments);
        val routeSegments = route.stream().collect(toList());

        assertEquals(segments, routeSegments);
    }

    @Test
    public void shouldReturnFirstStationAsHead() {
        val route = new Route(segments);

        assertEquals(segments.get(0).getBegin(), route.getHead());
    }

    @Test
    public void shouldReturnLastStationAsTail() {
        val route = new Route(segments);

        assertEquals(segments.get(segments.size() - 1).getEnd(), route.getTail());
    }
}
