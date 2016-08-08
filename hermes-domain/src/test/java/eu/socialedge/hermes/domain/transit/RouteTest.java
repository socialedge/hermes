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

import eu.socialedge.hermes.domain.infrastructure.StationId;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.assertFalse;

public class RouteTest {

    List<StationId> waypoints = new ArrayList<>();
    private Route route;

    @Before
    public void setUp() {
        waypoints.add(randomWaypoint());
        waypoints.add(randomWaypoint());
        waypoints.add(randomWaypoint());

        route = new Route(RouteId.of("routeId"), waypoints);
    }

    @Test
    public void testHasStationShouldReturnFalseIfNotContains() {
        assertFalse(route.hasStation(randomWaypoint()));
    }

    private StationId randomWaypoint() {
        return StationId.of("station" + ThreadLocalRandom.current().nextInt());
    }
}
