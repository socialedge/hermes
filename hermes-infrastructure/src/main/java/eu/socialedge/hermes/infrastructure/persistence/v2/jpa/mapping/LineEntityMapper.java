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
package eu.socialedge.hermes.infrastructure.persistence.v2.jpa.mapping;

import eu.socialedge.hermes.domain.v2.operator.AgencyId;
import eu.socialedge.hermes.domain.v2.transit.Line;
import eu.socialedge.hermes.domain.v2.transit.LineId;
import eu.socialedge.hermes.domain.v2.transit.RouteId;
import eu.socialedge.hermes.domain.v2.transport.VehicleType;
import eu.socialedge.hermes.infrastructure.persistence.v2.jpa.entity.JpaAgency;
import eu.socialedge.hermes.infrastructure.persistence.v2.jpa.entity.JpaLine;
import eu.socialedge.hermes.infrastructure.persistence.v2.jpa.entity.JpaRoute;
import eu.socialedge.hermes.infrastructure.persistence.v2.jpa.repository.entity.SpringJpaAgencyRepository;
import eu.socialedge.hermes.infrastructure.persistence.v2.jpa.repository.entity.SpringJpaRouteRepository;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

@Component
public class LineEntityMapper implements EntityMapper<Line, JpaLine> {

    private final SpringJpaAgencyRepository jpaAgencyRepository;
    private final SpringJpaRouteRepository jpaRouteRepository;

    @Inject
    public LineEntityMapper(SpringJpaAgencyRepository jpaAgencyRepository,
                            SpringJpaRouteRepository jpaRouteRepository) {
        this.jpaAgencyRepository = jpaAgencyRepository;
        this.jpaRouteRepository = jpaRouteRepository;
    }

    @Override
    public JpaLine mapToEntity(Line line) {
        String lineId = line.lineId().toString();
        JpaAgency jpaAgency = findAgencyById(line.agencyId());
        VehicleType vehicleType = line.vehicleType();
        Set<JpaRoute> jpaRoutes = line.routeIds().stream()
                .map(this::findRouteById)
                .collect(Collectors.toSet());

        JpaLine jpaLine = new JpaLine();

        jpaLine.lineId(lineId);
        jpaLine.agency(jpaAgency);
        jpaLine.vehicleType(vehicleType);
        jpaLine.routes(jpaRoutes);

        return jpaLine;
    }

    @Override
    public Line mapToDomain(JpaLine jpaLine) {
        LineId lineId = LineId.of(jpaLine.lineId());
        String name = jpaLine.name();
        AgencyId agencyId = AgencyId.of(jpaLine.agency().agencyId());
        VehicleType vehicleType = jpaLine.vehicleType();
        Set<RouteId> routeIds = jpaLine.routes().stream()
                .map(JpaRoute::routeId)
                .map(RouteId::of)
                .collect(Collectors.toSet());

        return new Line(lineId, name, agencyId, vehicleType, routeIds);
    }

    private JpaAgency findAgencyById(AgencyId agencyId) {
        return jpaAgencyRepository.findOne(agencyId.toString());
    }

    private JpaRoute findRouteById(RouteId routeId) {
        return jpaRouteRepository.findOne(routeId.toString());
    }
}
