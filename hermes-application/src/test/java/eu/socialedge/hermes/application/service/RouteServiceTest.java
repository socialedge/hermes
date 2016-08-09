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
package eu.socialedge.hermes.application.service;

import eu.socialedge.hermes.application.resource.spec.RouteSpecification;
import eu.socialedge.hermes.domain.infrastructure.StationId;
import eu.socialedge.hermes.domain.transit.Route;
import eu.socialedge.hermes.domain.transit.RouteId;
import eu.socialedge.hermes.domain.transit.RouteRepository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import javax.ws.rs.NotFoundException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RouteServiceTest {

    @InjectMocks
    private RouteService routeService;

    @Mock
    private RouteRepository routeRepository;

    @Test
    public void testFetchAllRoutesReturnCollection() throws Exception {
        List<Route> routeList = Arrays.asList(randomRoute(), randomRoute(), randomRoute());
        when(routeRepository.list()).thenReturn(routeList);

        Collection<Route> fetchResult = routeService.fetchAllRoutes();

        assertEquals(routeList, fetchResult);
    }

    @Test
    public void testFetchAllRoutesEmptyResult() throws Exception {
        when(routeRepository.list()).thenReturn(Collections.emptyList());

        Collection<Route> fetchResult = routeService.fetchAllRoutes();

        assertTrue(fetchResult.isEmpty());
        verify(routeRepository).list();
        verifyNoMoreInteractions(routeRepository);
    }

    @Test
    public void testFetchRouteNotFound() throws Exception {
        final RouteId routeId = RouteId.of("routeId");
        when(routeRepository.get(routeId)).thenReturn(Optional.empty());

        Optional<Route> fetchResultOpt = routeService.fetchRoute(routeId);

        assertFalse(fetchResultOpt.isPresent());
        verify(routeRepository).get(routeId);
        verifyNoMoreInteractions(routeRepository);
    }

    @Test
    public void testCreateRouteWithAllFields() {
        RouteSpecification spec = routeSpecification();

        Mockito.doAnswer(invocation -> {
            Route route = (Route) invocation.getArguments()[0];

            assertRouteEqualsToSpec(spec, route);

            return null;
        }).when(routeRepository).save(any(Route.class));

        routeService.createRoute(spec);

        verify(routeRepository).save(any(Route.class));
        verifyNoMoreInteractions(routeRepository);
    }

    @Test
    public void testUpdateRouteAllFields() throws Exception {
        Route routeToUpdate = randomRoute();
        RouteSpecification spec = routeSpecification();
        spec.routeId = routeToUpdate.id().toString();
        when(routeRepository.get(routeToUpdate.id())).thenReturn(Optional.of(routeToUpdate));
        doAnswer(invocation -> {
            Route route = (Route) invocation.getArguments()[0];

            assertRouteEqualsToSpec(spec, route);

            return null;
        }).when(routeRepository).save(routeToUpdate);

        routeService.updateRoute(routeToUpdate.id(), spec);

        verify(routeRepository).get(routeToUpdate.id());
        verify(routeRepository).save(routeToUpdate);
        verifyNoMoreInteractions(routeRepository);
    }

    @Test
    public void testUpdateRouteAllFieldsBlankOrNull() throws Exception {
        Route routeToUpdate = randomRoute();
        RouteSpecification spec = new RouteSpecification();
        spec.routeId = routeToUpdate.id().toString();
        spec.stationIds = Collections.emptyList();
        when(routeRepository.get(routeToUpdate.id())).thenReturn(Optional.of(routeToUpdate));
        doAnswer(invocation -> {
            Route route = (Route) invocation.getArguments()[0];

            assertEquals(routeToUpdate.id(), route.id());
            assertEquals(routeToUpdate.stream().collect(Collectors.toList()),
                        route.stream().collect(Collectors.toList()));

            return null;
        }).when(routeRepository).save(routeToUpdate);

        routeService.updateRoute(routeToUpdate.id(), spec);

        verify(routeRepository).get(routeToUpdate.id());
        verify(routeRepository).save(routeToUpdate);
        verifyNoMoreInteractions(routeRepository);
    }

    @Test(expected = NotFoundException.class)
    public void testUpdateRouteNotFound() {
        final RouteId routeId = RouteId.of("routeId");
        when(routeRepository.get(routeId)).thenReturn(Optional.empty());

        routeService.updateRoute(routeId, routeSpecification());

        verify(routeRepository).get(routeId);
        verifyNoMoreInteractions(routeRepository);
    }

    @Test
    public void testDeleteRoute() {
        final RouteId routeId = RouteId.of("routeId");
        when(routeRepository.remove(routeId)).thenReturn(true);

        boolean deleteResult = routeService.deleteRoute(routeId);

        assertTrue(deleteResult);
        verify(routeRepository).remove(routeId);
        verifyNoMoreInteractions(routeRepository);
    }

    private void assertRouteEqualsToSpec(RouteSpecification spec, Route route) {
        assertEquals(spec.routeId, route.id().toString());
        assertEquals(spec.stationIds, route.stream().map(StationId::toString).collect(Collectors.toList()));
    }

    private Route randomRoute() throws Exception {
        int id = ThreadLocalRandom.current().nextInt(100, 1000);
        List<StationId> stationIds = new ArrayList<StationId>() {{
            add(StationId.of("station"));
        }};
        return new Route(RouteId.of("route" + id), stationIds);
    }

    private RouteSpecification routeSpecification() {
        RouteSpecification spec = new RouteSpecification();
        spec.routeId = "routeId";
        spec.stationIds = new ArrayList<String>() {{
            add("station1");
            add("station2");
            add("station3");
        }};

        return spec;
    }
}
