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

import eu.socialedge.hermes.domain.v2.infrastructure.StationId;
import eu.socialedge.hermes.domain.v2.timetable.Stop;
import eu.socialedge.hermes.infrastructure.persistence.v2.jpa.entity.JpaStop;
import eu.socialedge.hermes.infrastructure.persistence.v2.jpa.repository.entity.SpringJpaStationRepository;

import org.springframework.stereotype.Component;

import java.time.LocalTime;

import javax.inject.Inject;

@Component
public class StopEntityMapper implements EntityMapper<Stop, JpaStop> {

    private final SpringJpaStationRepository jpaStationRepository;

    @Inject
    public StopEntityMapper(SpringJpaStationRepository jpaStationRepository) {
        this.jpaStationRepository = jpaStationRepository;
    }

    @Override
    public JpaStop mapToEntity(Stop stop) {
        JpaStop jpaStop = new JpaStop();

        jpaStop.station(jpaStationRepository.findOne(stop.stationId().toString()));
        jpaStop.arrival(stop.arrival());
        jpaStop.departure(stop.departure());

        return jpaStop;
    }

    @Override
    public Stop mapToDomain(JpaStop jpaStop) {
        StationId stationId = StationId.of(jpaStop.station().stationId());
        LocalTime arrival = jpaStop.arrival();
        LocalTime departure = jpaStop.departure();

        return new Stop(stationId, arrival, departure);
    }
}
