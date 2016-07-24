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
package eu.socialedge.hermes.domain.timetable;

import eu.socialedge.hermes.application.exception.NotFoundException;
import eu.socialedge.hermes.application.service.RouteService;
import eu.socialedge.hermes.domain.infrastructure.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RouteServiceTest {

    @InjectMocks
    private RouteService routeService;

    @Mock
    private RouteRepository routeRepository;

    private final String routeCode1 = "routeCode1";
    private final String routeCode2 = "routeCode2";
    private final Waypoint waypoint1 = new Waypoint(new Station("station1", "stationName1",
            TransportType.BUS, new Position(1, 1)), 1);
    private final Waypoint waypoint2 = new Waypoint(new Station("station2", "stationName2",
            TransportType.TRAM, new Position(2, 2)), 2);
    private final List<Waypoint> waypoints = Arrays.asList(waypoint1, waypoint2);
    private final Route route1 = new Route(routeCode1);
    private final Route route2 = new Route(routeCode2);


    @Test
    public void testFetchRouteSuccess() {
        when(routeRepository.get(routeCode1)).thenReturn(Optional.of(route1));

        Route route = routeService.fetchRoute(routeCode1);

        assertEquals(route1, route);
        verify(routeRepository).get(routeCode1);
        verifyNoMoreInteractions(routeRepository);
    }

    @Test(expected = NullPointerException.class)
    public void testFetchRouteNullRouteCode() {
        routeService.fetchRoute(null);

        verifyZeroInteractions(routeRepository);
    }

    @Test(expected = NotFoundException.class)
    public void testFetchRouteNotFound() {
        when(routeRepository.get(routeCode1)).thenReturn(Optional.empty());

        routeService.fetchRoute(routeCode1);

        verify(routeRepository).get(routeCode1);
        verifyNoMoreInteractions(routeRepository);
    }

    @Test
    public void testFetchRoutesSuccess() {
        when(routeRepository.get(routeCode1)).thenReturn(Optional.of(route1));
        when(routeRepository.get(routeCode2)).thenReturn(Optional.of(route2));

        Collection<Route> routes = routeService.fetchRoutes(Arrays.asList(routeCode1, routeCode2));

        assertEquals(2, routes.size());
        assertTrue(routes.contains(route1));
        assertTrue(routes.contains(route2));
        verify(routeRepository).get(routeCode1);
        verify(routeRepository).get(routeCode2);
        verifyNoMoreInteractions(routeRepository);
    }

    @Test(expected = NotFoundException.class)
    public void testFetchRoutesOneNotFound() {
        when(routeRepository.get(routeCode1)).thenReturn(Optional.of(route1));
        when(routeRepository.get(routeCode2)).thenReturn(Optional.empty());

        routeService.fetchRoutes(Arrays.asList(routeCode1, routeCode2));
        verify(routeRepository).get(routeCode1);
        verify(routeRepository).get(routeCode2);
        verifyNoMoreInteractions(routeRepository);
    }

    @Test
    public void testFetchRoutesEmptyList() {
        assertTrue(routeService.fetchRoutes(Collections.emptyList()).isEmpty());
        verifyZeroInteractions(routeRepository);
    }

    @Test(expected = NullPointerException.class)
    public void testFetchRoutesNullInput() {
        routeService.fetchRoutes(null);

        verifyZeroInteractions(routeRepository);
    }

    @Test
    public void testFetchAllRoutesSuccess() {
        List<Route> routes = Arrays.asList(route1, route2);
        when(routeRepository.list()).thenReturn(routes);

        Collection<Route> retrievedRoutes = routeService.fetchAllRoutes();

        assertEquals(routes, retrievedRoutes);
        verify(routeRepository).list();
        verifyNoMoreInteractions(routeRepository);
    }

    @Test
    public void testFetchAllRoutesEmptyResult() {
        when(routeRepository.list()).thenReturn(Collections.emptyList());

        assertTrue(routeService.fetchAllRoutes().isEmpty());
        verify(routeRepository).list();
        verifyNoMoreInteractions(routeRepository);
    }

    @Test
    public void testCreateLineSuccess() {
        when(routeRepository.store(route1)).then(invocation -> {
            Route route = (Route) invocation.getArguments()[0];

            assertEquals(routeCode1, route.getCodeId());
            assertEquals(waypoints, route.getWaypoints());

            return route;
        });

        routeService.createLine(routeCode1, waypoints);

        verify(routeRepository).store(route1);
        verifyNoMoreInteractions(routeRepository);
    }

    @Test(expected = NullPointerException.class)
    public void testCreateLineRouteCodeNull() {
        routeService.createLine(null, waypoints);

        verifyZeroInteractions(routeRepository);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateLineRouteCodeEmpty() {
        routeService.createLine("", waypoints);

        verifyZeroInteractions(routeRepository);
    }

    @Test
    public void testCreateLineWaypointsNull() {
        when(routeRepository.store(route1)).thenReturn(route1);

        Route route = routeService.createLine(routeCode1, null);

        assertTrue(route.getWaypoints().isEmpty());
        verify(routeRepository).store(route1);
        verifyNoMoreInteractions(routeRepository);
    }

    @Test
    public void testRemoveRouteSuccess() {
        when(routeRepository.get(routeCode1)).thenReturn(Optional.of(route1));

        routeService.removeRoute(routeCode1);

        verify(routeRepository).get(routeCode1);
        verify(routeRepository).remove(route1);
        verifyNoMoreInteractions(routeRepository);
    }

    @Test(expected = NotFoundException.class)
    public void testRemoveRouteNotFound() {
        when(routeRepository.get(routeCode1)).thenReturn(Optional.empty());

        routeService.removeRoute(routeCode1);

        verify(routeRepository).get(routeCode1);
        verifyNoMoreInteractions(routeRepository);
    }

    @Test
    public void testCreateWaypointSuccess() {
        when(routeRepository.get(routeCode1)).thenReturn(Optional.of(route1));
        when(routeRepository.store(route1)).then(invocation -> {
            Route route = (Route) invocation.getArguments()[0];

            assertEquals(route1, route);
            assertTrue(route.getWaypoints().contains(waypoint1));

            return route;
        });

        routeService.createWaypoint(routeCode1, waypoint1.getStation(), waypoint1.getPosition());

        verify(routeRepository).get(routeCode1);
        verify(routeRepository).store(route1);
        verifyNoMoreInteractions(routeRepository);
    }

    @Test(expected = NotFoundException.class)
    public void testCreateWaypointRouteNotFound() {
        when(routeRepository.get(routeCode1)).thenReturn(Optional.empty());

        routeService.createWaypoint(routeCode1, waypoint1.getStation(), waypoint1.getPosition());

        verify(routeRepository).get(routeCode1);
        verifyNoMoreInteractions(routeRepository);
    }


    @Test(expected = NullPointerException.class)
    public void testCreateWaypointStationNull() {
        when(routeRepository.get(routeCode1)).thenReturn(Optional.of(route1));

        routeService.createWaypoint(routeCode1, null, waypoint1.getPosition());

        verify(routeRepository).get(routeCode1);
        verifyNoMoreInteractions(routeRepository);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWaypointPositionZero() {
        routeService.createWaypoint(routeCode1, waypoint1.getStation(), 0);
    }

    @Test
    public void testRemoveWaypointSuccess() {
        route1.appendWaypoint(waypoint1.getStation());
        when(routeRepository.get(routeCode1)).thenReturn(Optional.of(route1));
        when(routeRepository.store(route1)).then(invocation -> {
            Route route = (Route) invocation.getArguments()[0];

            assertTrue(route.getWaypoints().isEmpty());

            return route;
        });

        routeService.removeWaypoint(routeCode1, waypoint1.getStation().getCodeId());

        verify(routeRepository).get(routeCode1);
        verify(routeRepository).store(route1);
        verifyNoMoreInteractions(routeRepository);
    }

    @Test(expected = NotFoundException.class)
    public void testRemoveWaypointRouteNotFound() {
        when(routeRepository.get(routeCode1)).thenReturn(Optional.empty());

        routeService.removeWaypoint(routeCode1, waypoint1.getStation().getCodeId());

        verify(routeRepository).get(routeCode1);
        verifyNoMoreInteractions(routeRepository);
    }

    @Test(expected = NotFoundException.class)
    public void testRemoveWaypointStationNotFound() {
        when(routeRepository.get(routeCode1)).thenReturn(Optional.of(route1));

        routeService.removeWaypoint(routeCode1, waypoint1.getStation().getCodeId());

        verify(routeRepository).get(routeCode1);
        verifyNoMoreInteractions(routeRepository);
    }
}
