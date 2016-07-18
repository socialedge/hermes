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

import eu.socialedge.hermes.application.exception.NotFoundException;
import eu.socialedge.hermes.domain.infrastructure.Line;
import eu.socialedge.hermes.domain.infrastructure.LineRepository;
import eu.socialedge.hermes.domain.infrastructure.Route;
import eu.socialedge.hermes.domain.infrastructure.TransportType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.List;

import static org.apache.commons.lang3.Validate.notNull;

@Component
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;

    private final RouteService routeService;
    private final OperatorService operatorService;

    @Inject
    public LineService(LineRepository lineRepository, RouteService routeService, OperatorService operatorService) {
        this.lineRepository = lineRepository;
        this.routeService = routeService;
        this.operatorService = operatorService;
    }

    @Transactional
    public Line createLine(String lineCode, int operatorId, TransportType type, Collection<String> routeCodes) {
        Line line = new Line(lineCode, type);

        if (routeCodes != null && !routeCodes.isEmpty()) {
            Collection<Route> routes = routeService.fetchRoutes(routeCodes);
            line.setRoutes(routes);
        }

        if (operatorId > 0)
            line.setOperator(operatorService.fetchOperator(operatorId));

        return lineRepository.store(line);
    }

    public Line fetchLine(String lineCode) {
        return lineRepository.get(notNull(lineCode)).orElseThrow(() ->
                new NotFoundException("No line was found with code + " + lineCode));
    }

    public Collection<Line> fetchAllLines() {
        return lineRepository.list();
    }

    public Collection<Line> fetchAllLinesByOperatorId(int operatorId) {
        return lineRepository.findByOperatorId(operatorId);
    }

    @Transactional
    public void attachRoute(String lineCode, List<String> routeCodes) {
        Line line = fetchLine(lineCode);

        Collection<Route> routes = routeService.fetchRoutes(routeCodes);
        line.getRoutes().addAll(routes);
        lineRepository.store(line);
    }

    @Transactional
    public Response detachRoute(String lineCode, List<String> routeCodes) {
        Line line = fetchLine(lineCode);

        Collection<Route> routes = routeService.fetchRoutes(routeCodes);
        line.getRoutes().removeAll(routes);
        lineRepository.store(line);

        return Response.ok().build();
    }

    @Transactional
    public void updateLine(String lineCode, int operatorId, Collection<String> routeCodes) {
        Line line = fetchLine(lineCode);
        boolean wasUpdated = false;

        if (operatorId > 0) {
            line.setOperator(operatorService.fetchOperator(operatorId));
            wasUpdated = true;
        }

        if (routeCodes != null && !routeCodes.isEmpty()) {
            Collection<Route> routes = routeService.fetchRoutes(routeCodes);
            line.setRoutes(routes);
            wasUpdated = true;
        }

        if (wasUpdated)
            lineRepository.store(line);
    }

    @Transactional
    public void removeLine(String lineCode) {
        lineRepository.remove(fetchLine(lineCode));
    }
}
