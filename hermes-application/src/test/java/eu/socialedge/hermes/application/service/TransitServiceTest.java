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

import eu.socialedge.hermes.application.domain.transit.LineSpecification;
import eu.socialedge.hermes.application.domain.transit.TransitService;
import eu.socialedge.hermes.domain.operator.AgencyId;
import eu.socialedge.hermes.domain.transit.Line;
import eu.socialedge.hermes.domain.transit.LineId;
import eu.socialedge.hermes.domain.transit.LineRepository;
import eu.socialedge.hermes.domain.transit.RouteId;
import eu.socialedge.hermes.domain.transport.VehicleType;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import javax.ws.rs.NotFoundException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TransitServiceTest {

    @InjectMocks
    private TransitService transitService;

    @Mock
    private LineRepository lineRepository;

    @Test
    public void testFetchAllLinesReturnCollection() throws Exception {
        List<Line> lineList = Arrays.asList(randomLine(), randomLine(), randomLine());
        when(lineRepository.list()).thenReturn(lineList);

        Collection<Line> fetchResult = transitService.fetchAllLines();

        assertEquals(lineList, fetchResult);
    }

    @Test
    public void testFetchAllLinesEmptyResult() throws Exception {
        when(lineRepository.list()).thenReturn(Collections.emptyList());

        Collection<Line> fetchResult = transitService.fetchAllLines();

        assertTrue(fetchResult.isEmpty());
        verify(lineRepository).list();
        verifyNoMoreInteractions(lineRepository);
    }

    @Test(expected = NotFoundException.class)
    public void testFetchLineNotFound() throws Exception {
        final LineId lineId = LineId.of("lineId");
        when(lineRepository.get(lineId)).thenReturn(Optional.empty());

        transitService.fetchLine(lineId);
    }

    @Test
    public void testCreateLineWithAllFields() {
        LineSpecification spec = lineSpecification();

        Mockito.doAnswer(invocation -> {
            Line line = (Line) invocation.getArguments()[0];

            assertLineEqualsToSpec(spec, line);

            return null;
        }).when(lineRepository).add(any(Line.class));

        transitService.createLine(spec);

        verify(lineRepository).add(any(Line.class));
        verifyNoMoreInteractions(lineRepository);
    }

    @Test
    public void testUpdateLineAllFields() throws Exception {
        Line lineToUpdate = randomLine();
        LineSpecification spec = lineSpecification();
        spec.lineId = lineToUpdate.id().toString();
        when(lineRepository.get(lineToUpdate.id())).thenReturn(Optional.of(lineToUpdate));
        doAnswer(invocation -> {
            Line line = (Line) invocation.getArguments()[0];

            assertLineEqualsToSpec(spec, line);

            return null;
        }).when(lineRepository).update(lineToUpdate);

        transitService.updateLine(lineToUpdate.id(), spec);

        verify(lineRepository).get(lineToUpdate.id());
        verify(lineRepository).update(lineToUpdate);
        verifyNoMoreInteractions(lineRepository);
    }

    @Test
    public void testUpdateLineAllFieldsBlankOrNull() throws Exception {
        Line lineToUpdate = randomLine();
        LineSpecification spec = new LineSpecification();
        spec.lineId = lineToUpdate.id().toString();
        spec.name = "";
        spec.routeIds = Collections.emptySet();
        spec.agencyId = "";
        when(lineRepository.get(lineToUpdate.id())).thenReturn(Optional.of(lineToUpdate));
        doAnswer(invocation -> {
            Line line = (Line) invocation.getArguments()[0];

            assertEquals(lineToUpdate.id(), line.id());
            assertEquals(lineToUpdate.name(), line.name());
            assertEquals(lineToUpdate.agencyId(), line.agencyId());
            assertEquals(lineToUpdate.attachedRouteIds(), line.attachedRouteIds());

            return null;
        }).when(lineRepository).update(lineToUpdate);

        transitService.updateLine(lineToUpdate.id(), spec);

        verify(lineRepository).get(lineToUpdate.id());
        verify(lineRepository).update(lineToUpdate);
        verifyNoMoreInteractions(lineRepository);
    }

    @Test(expected = NotFoundException.class)
    public void testUpdateLineNotFound() {
        final LineId lineId = LineId.of("lineId");
        when(lineRepository.get(lineId)).thenReturn(Optional.empty());

        transitService.updateLine(lineId, lineSpecification());

        verify(lineRepository).get(lineId);
        verifyNoMoreInteractions(lineRepository);
    }

    @Test
    public void testDeleteLine() {
        final LineId lineId = LineId.of("lineId");
        when(lineRepository.remove(lineId)).thenReturn(true);

        transitService.deleteLine(lineId);

        verify(lineRepository).remove(lineId);
        verifyNoMoreInteractions(lineRepository);
    }

    private void assertLineEqualsToSpec(LineSpecification spec, Line line) {
        assertEquals(spec.lineId, line.id().toString());
        assertEquals(spec.name, line.name());
        assertEquals(spec.agencyId, line.agencyId().toString());
        assertEquals(spec.routeIds, line.attachedRouteIds().stream().map(RouteId::toString).collect(Collectors.toSet()));
    }

    private Line randomLine() throws Exception {
        int id = ThreadLocalRandom.current().nextInt(100, 1000);
        Set<RouteId> routes = new HashSet<RouteId>() {{
            add(RouteId.of("route"));
        }};
        return new Line(LineId.of("line" + id), AgencyId.of("agency" + id), "name", VehicleType.BUS, routes);
    }

    private LineSpecification lineSpecification() {
        LineSpecification spec = new LineSpecification();
        spec.lineId = "lineId";
        spec.name = "name";
        spec.vehicleType = "BUS";
        spec.agencyId = "agencyId";
        spec.routeIds = new HashSet<String>() {{
            add("route1");
            add("route2");
            add("route3");
        }};

        return spec;
    }
}
