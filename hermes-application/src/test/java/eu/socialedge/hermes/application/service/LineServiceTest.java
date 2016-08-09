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

import eu.socialedge.hermes.application.resource.spec.LineSpecification;
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
public class LineServiceTest {

    @InjectMocks
    private LineService lineService;

    @Mock
    private LineRepository lineRepository;

    @Test
    public void testFetchAllLinesReturnCollection() throws Exception {
        List<Line> lineList = Arrays.asList(randomLine(), randomLine(), randomLine());
        when(lineRepository.list()).thenReturn(lineList);

        Collection<Line> fetchResult = lineService.fetchAllLines();

        assertEquals(lineList, fetchResult);
    }

    @Test
    public void testFetchAllLinesEmptyResult() throws Exception {
        when(lineRepository.list()).thenReturn(Collections.emptyList());

        Collection<Line> fetchResult = lineService.fetchAllLines();

        assertTrue(fetchResult.isEmpty());
        verify(lineRepository).list();
        verifyNoMoreInteractions(lineRepository);
    }

    @Test
    public void testFetchLineNotFound() throws Exception {
        final LineId lineId = LineId.of("lineId");
        when(lineRepository.get(lineId)).thenReturn(Optional.empty());

        Optional<Line> fetchResultOpt = lineService.fetchLine(lineId);

        assertFalse(fetchResultOpt.isPresent());
        verify(lineRepository).get(lineId);
        verifyNoMoreInteractions(lineRepository);
    }

    @Test
    public void testCreateLineWithAllFields() {
        LineSpecification spec = lineSpecification();

        Mockito.doAnswer(invocation -> {
            Line line = (Line) invocation.getArguments()[0];

            assertLineEqualsToSpec(spec, line);

            return null;
        }).when(lineRepository).save(any(Line.class));

        lineService.createLine(spec);

        verify(lineRepository).save(any(Line.class));
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
        }).when(lineRepository).save(lineToUpdate);

        lineService.updateLine(lineToUpdate.id(), spec);

        verify(lineRepository).get(lineToUpdate.id());
        verify(lineRepository).save(lineToUpdate);
        verifyNoMoreInteractions(lineRepository);
    }

    @Test
    public void testUpdateLineAllFieldsBlankOrNull() throws Exception {
        Line lineToUpdate = randomLine();
        LineSpecification spec = new LineSpecification();
        spec.lineId = lineToUpdate.id().toString();
        spec.name = "";
        spec.routeIds = Collections.emptyList();
        spec.vehicleType = "";
        spec.agencyId = "";
        when(lineRepository.get(lineToUpdate.id())).thenReturn(Optional.of(lineToUpdate));
        doAnswer(invocation -> {
            Line line = (Line) invocation.getArguments()[0];

            assertEquals(lineToUpdate.id(), line.id());
            assertEquals(lineToUpdate.name(), line.name());
            assertEquals(lineToUpdate.agencyId(), line.agencyId());
            assertEquals(lineToUpdate.vehicleType(), line.vehicleType());
            assertEquals(lineToUpdate.routeIds(), line.routeIds());

            return null;
        }).when(lineRepository).save(lineToUpdate);

        lineService.updateLine(lineToUpdate.id(), spec);

        verify(lineRepository).get(lineToUpdate.id());
        verify(lineRepository).save(lineToUpdate);
        verifyNoMoreInteractions(lineRepository);
    }

    @Test(expected = NotFoundException.class)
    public void testUpdateLineNotFound() {
        final LineId lineId = LineId.of("lineId");
        when(lineRepository.get(lineId)).thenReturn(Optional.empty());

        lineService.updateLine(lineId, lineSpecification());

        verify(lineRepository).get(lineId);
        verifyNoMoreInteractions(lineRepository);
    }

    @Test
    public void testDeleteLine() {
        final LineId lineId = LineId.of("lineId");
        when(lineRepository.remove(lineId)).thenReturn(true);

        boolean deleteResult = lineService.deleteLine(lineId);

        assertTrue(deleteResult);
        verify(lineRepository).remove(lineId);
        verifyNoMoreInteractions(lineRepository);
    }

    private void assertLineEqualsToSpec(LineSpecification spec, Line line) {
        assertEquals(spec.lineId, line.id().toString());
        assertEquals(spec.name, line.name());
        assertEquals(spec.agencyId, line.agencyId().toString());
        assertEquals(spec.vehicleType, line.vehicleType().name());
        assertEquals(spec.routeIds, line.routeIds().stream().map(RouteId::toString).collect(Collectors.toList()));
    }

    private Line randomLine() throws Exception {
        int id = ThreadLocalRandom.current().nextInt(100, 1000);
        List<RouteId> routes = new ArrayList<RouteId>() {{
            add(RouteId.of("route"));
        }};
        return new Line(LineId.of("line" + id), "name", AgencyId.of("agency" + id), VehicleType.BUS, routes);
    }

    private LineSpecification lineSpecification() {
        LineSpecification spec = new LineSpecification();
        spec.lineId = "lineId";
        spec.name = "name";
        spec.agencyId = "agencyId";
        spec.vehicleType = "SLEEPER_RAIL";
        spec.routeIds = new ArrayList<String>() {{
            add("route1");
            add("route2");
            add("route3");
        }};

        return spec;
    }
}
