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
import eu.socialedge.hermes.application.service.LineService;
import eu.socialedge.hermes.application.service.OperatorService;
import eu.socialedge.hermes.application.service.RouteService;
import eu.socialedge.hermes.domain.infrastructure.*;
import org.junit.Before;
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
public class LineServiceTest {

    @InjectMocks
    private LineService lineService;

    @Mock
    private LineRepository lineRepository;

    @Mock
    private OperatorService operatorService;

    @Mock
    private RouteService routeService;

    private final String lineCode = "lineCode";
    private final List<String> routesCodes = Arrays.asList("route1", "route2", "route3");
    private List<Route> routes = new ArrayList<>();
    private final Operator operator1 = new Operator("operator1");
    private final Operator operator2 = new Operator("operator2");
    private Line line1;
    private Line line2;

    @Before
    public void setUp() {
        routes = Arrays.asList(new Route("route1"), new Route("route2"), new Route("route3"));
        line1 = new Line(lineCode, TransportType.BUS);
        line1.setOperator(operator1);
        line2 = new Line(lineCode + "1", TransportType.TRAIN);
        line2.setOperator(operator2);
    }

    @Test
    public void testFetchLineSuccess() {
        when(lineRepository.get(lineCode)).thenReturn(Optional.of(line1));

        Line returnedLine = lineService.fetchLine(lineCode);

        assertEquals(line1, returnedLine);
        verify(lineRepository).get(lineCode);
    }

    @Test(expected = NullPointerException.class)
    public void testFetchLineLineCodeNull() {
        lineService.fetchLine(null);

        verifyZeroInteractions(lineRepository, operatorService, routeService);
    }

    @Test(expected = NotFoundException.class)
    public void testFetchLineNotFound() {
        when(lineRepository.get(lineCode)).thenReturn(Optional.empty());

        lineService.fetchLine(lineCode);

        verify(lineRepository).get(lineCode);
        verifyNoMoreInteractions(lineRepository, operatorService, routeService);
    }

    @Test
    public void testFetchAllLinesSuccess() {
        List<Line> lines = Arrays.asList(line1, line2);
        when(lineRepository.list()).thenReturn(lines);

        Collection<Line> returnedLines = lineService.fetchAllLines();

        assertTrue(returnedLines.equals(lines));
        verify(lineRepository).list();
        verifyNoMoreInteractions(lineRepository, operatorService, routeService);
    }

    @Test
    public void testFetchAllLinesEmptyResult() {
        when(lineRepository.list()).thenReturn(Collections.emptyList());

        assertTrue(lineService.fetchAllLines().isEmpty());
        verify(lineRepository).list();
        verifyNoMoreInteractions(lineRepository, operatorService, routeService);
    }

    @Test
    public void testFetchAllLinesByOperatorId() {
        List<Line> lines = Arrays.asList(line1, line2);
        when(lineRepository.findByOperatorId(1)).thenReturn(lines);

        Collection<Line> returnedLines = lineService.fetchAllLinesByOperatorId(1);

        assertTrue(returnedLines.equals(lines));
        verify(lineRepository).findByOperatorId(1);
        verifyNoMoreInteractions(lineRepository, operatorService, routeService);
    }

    @Test
    public void testFetchAllLinesByOperatorIdEmptyResult() {
        when(lineRepository.findByOperatorId(1)).thenReturn(Collections.emptyList());

        assertTrue(lineService.fetchAllLinesByOperatorId(1).isEmpty());
        verify(lineRepository).findByOperatorId(1);
        verifyNoMoreInteractions(lineRepository, operatorService, routeService);
    }

    @Test
    public void testAttachRouteSuccess() {
        when(lineRepository.get(lineCode)).thenReturn(Optional.of(line1));
        when(routeService.fetchRoutes(routesCodes)).thenReturn(routes);

        routes.stream().forEach(line1::addRoute);

        lineService.attachRoute(lineCode, routesCodes);

        verify(lineRepository).get(lineCode);
        verify(lineRepository).store(line1);
        verify(routeService).fetchRoutes(routesCodes);
        verifyNoMoreInteractions(lineRepository, operatorService, routeService);
    }

    @Test
    public void testAttachRouteRoutesNotFound() {
        when(lineRepository.get(lineCode)).thenReturn(Optional.of(line1));
        when(routeService.fetchRoutes(routesCodes)).thenReturn(Collections.emptyList());

        lineService.attachRoute(lineCode, routesCodes);

        verify(lineRepository).get(lineCode);
        verify(lineRepository).store(line1);
        verify(routeService).fetchRoutes(routesCodes);
        verifyNoMoreInteractions(lineRepository, operatorService, routeService);
    }

    @Test
    public void testDetachRouteSuccess() {
        Route route = new Route("route4");
        routes.stream().forEach(line1::addRoute);
        line1.addRoute(route);

        when(lineRepository.get(lineCode)).thenReturn(Optional.of(line1));
        when(routeService.fetchRoutes(routesCodes)).thenReturn(routes);

        lineService.detachRoute(lineCode, routesCodes);

        assertEquals(1, line1.getRoutes().size());
        assertTrue(line1.getRoutes().contains(route));

        verify(lineRepository).get(lineCode);
        verify(lineRepository).store(line1);
        verify(routeService).fetchRoutes(routesCodes);
        verifyNoMoreInteractions(lineRepository, operatorService, routeService);
    }

    @Test
    public void testDetachRouteNothingToDetach() {
        line1.setRoutes(routes);

        when(lineRepository.get(lineCode)).thenReturn(Optional.of(line1));
        when(routeService.fetchRoutes(routesCodes)).thenReturn(Collections.emptyList());

        lineService.detachRoute(lineCode, routesCodes);

        assertEquals(routes, line1.getRoutes());

        verify(lineRepository).get(lineCode);
        verify(lineRepository).store(line1);
        verify(routeService).fetchRoutes(routesCodes);
        verifyNoMoreInteractions(lineRepository, operatorService, routeService);
    }

    @Test
    public void testUpdateLineFull() {
        when(lineRepository.get(lineCode)).thenReturn(Optional.of(line1));
        when(operatorService.fetchOperator(2)).thenReturn(operator2);
        when(routeService.fetchRoutes(routesCodes)).thenReturn(routes);
        when(lineRepository.store(any(Line.class))).then(invocation -> {
            Object firstParam = invocation.getArguments()[0];
            if (firstParam instanceof Line) {
                Line updatedLine = (Line) firstParam;

                assertEquals(lineCode, updatedLine.getCodeId());
                assertEquals(operator2, updatedLine.getOperator());
                assertEquals(routes, updatedLine.getRoutes());
            }
            return null;
        });

        lineService.updateLine(lineCode, 2, routesCodes);

        verify(lineRepository).get(lineCode);
        verify(lineRepository).store(line1);
        verify(operatorService).fetchOperator(2);
        verify(routeService).fetchRoutes(routesCodes);
        verifyNoMoreInteractions(lineRepository, operatorService, routeService);
    }

    @Test(expected = NotFoundException.class)
    public void testUpdateLineNotFoundLine() {
        when(lineRepository.get(lineCode)).thenThrow(new NotFoundException(""));

        lineService.updateLine(lineCode, 2, routesCodes);

        verify(lineRepository).get(lineCode);
        verifyNoMoreInteractions(lineRepository, operatorService, routeService);
    }

    @Test
    public void testUpdateLineWrongOperatorAndRoutes() {
        when(lineRepository.get(lineCode)).thenReturn(Optional.of(line1));

        lineService.updateLine(lineCode, 0, Collections.emptyList());

        verify(lineRepository).get(lineCode);
        verifyNoMoreInteractions(lineRepository, operatorService, routeService);
    }

    @Test
    public void testUpdateLineSameOperatorAndRoutes() {
        line1.setRoutes(routes);
        when(lineRepository.get(lineCode)).thenReturn(Optional.of(line1));
        when(operatorService.fetchOperator(1)).thenReturn(operator1);
        when(routeService.fetchRoutes(routesCodes)).thenReturn(routes);
        when(lineRepository.store(any(Line.class))).then(invocation -> {
            Object firstParam = invocation.getArguments()[0];
            if (firstParam instanceof Line) {
                Line updatedLine = (Line) firstParam;

                assertEquals(lineCode, updatedLine.getCodeId());
                assertEquals(line1.getOperator(), updatedLine.getOperator());
                assertEquals(line1.getRoutes(), updatedLine.getRoutes());
            }
            return null;
        });

        lineService.updateLine(lineCode, 1, routesCodes);

        verify(lineRepository).get(lineCode);
        verify(lineRepository).store(line1);
        verify(operatorService).fetchOperator(1);
        verify(routeService).fetchRoutes(routesCodes);
        verifyNoMoreInteractions(lineRepository, operatorService, routeService);
    }

    @Test
    public void testRemoveLineSuccess() {
        when(lineRepository.get(lineCode)).thenReturn(Optional.of(line1));

        lineService.removeLine(lineCode);

        verify(lineRepository).get(lineCode);
        verify(lineRepository).remove(line1);
        verifyNoMoreInteractions(lineRepository, operatorService, routeService);
    }

    @Test(expected = NotFoundException.class)
    public void testRemoveLineNotFound() {
        when(lineRepository.get(lineCode)).thenThrow(new NotFoundException(""));

        lineService.removeLine(lineCode);

        verify(lineRepository).get(lineCode);
        verifyNoMoreInteractions(lineRepository, operatorService, routeService);
    }

}
