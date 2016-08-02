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

import eu.socialedge.hermes.domain.v2.shared.transport.VehicleType;
import eu.socialedge.hermes.domain.v2.operator.AgencyId;
import eu.socialedge.hermes.domain.v2.transit.Line;
import eu.socialedge.hermes.domain.v2.transit.LineId;
import eu.socialedge.hermes.domain.v2.transit.TripId;
import eu.socialedge.hermes.infrastructure.persistence.v2.jpa.entity.JpaAgency;
import eu.socialedge.hermes.infrastructure.persistence.v2.jpa.entity.JpaLine;
import eu.socialedge.hermes.infrastructure.persistence.v2.jpa.entity.JpaTrip;
import eu.socialedge.hermes.infrastructure.persistence.v2.jpa.repository.entity.SpringJpaAgencyRepository;
import eu.socialedge.hermes.infrastructure.persistence.v2.jpa.repository.entity.SpringJpaTripRepository;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class LineEntityMapper implements EntityMapper<Line, JpaLine> {

    private final SpringJpaAgencyRepository jpaAgencyRepository;
    private final SpringJpaTripRepository jpaTripRepository;

    @Inject
    public LineEntityMapper(SpringJpaAgencyRepository jpaAgencyRepository,
                            SpringJpaTripRepository jpaTripRepository) {
        this.jpaAgencyRepository = jpaAgencyRepository;
        this.jpaTripRepository = jpaTripRepository;
    }

    @Override
    public JpaLine mapToEntity(Line line) {
        String lineId = line.lineId().toString();
        JpaAgency jpaAgency = findAgencyById(line.agencyId());
        VehicleType vehicleType = line.vehicleType();
        Set<JpaTrip> jpaRoutes = line.tripIds().stream()
                .map(this::findTripById)
                .collect(Collectors.toSet());

        JpaLine jpaLine = new JpaLine();

        jpaLine.lineId(lineId);
        jpaLine.agency(jpaAgency);
        jpaLine.vehicleType(vehicleType);
        jpaLine.trips(jpaRoutes);

        return jpaLine;
    }

    @Override
    public Line mapToDomain(JpaLine jpaLine) {
        LineId lineId = LineId.of(jpaLine.lineId());
        String name = jpaLine.name();
        AgencyId agencyId = AgencyId.of(jpaLine.agency().agencyId());
        VehicleType vehicleType = jpaLine.vehicleType();
        Set<TripId> routeIds = jpaLine.trips().stream()
                .map(JpaTrip::tripId)
                .map(TripId::of)
                .collect(Collectors.toSet());

        return new Line(lineId, name, agencyId, vehicleType, routeIds);
    }

    private JpaAgency findAgencyById(AgencyId agencyId) {
        return jpaAgencyRepository.findOne(agencyId.toString());
    }

    private JpaTrip findTripById(TripId tripId) {
        return jpaTripRepository.findOne(tripId.toString());
    }
}
