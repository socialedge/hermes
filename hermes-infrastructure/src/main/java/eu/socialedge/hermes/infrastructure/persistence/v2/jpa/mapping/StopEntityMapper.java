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
import eu.socialedge.hermes.domain.v2.transit.Stop;
import eu.socialedge.hermes.domain.v2.transit.Stops;
import eu.socialedge.hermes.infrastructure.persistence.v2.jpa.entity.JpaStop;
import eu.socialedge.hermes.infrastructure.persistence.v2.jpa.repository.entity.SpringJpaStationRepository;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.time.LocalTime;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

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

    public SortedSet<JpaStop> mapToEntity(Stops stops) {
        return stops.stream().map(this::mapToEntity).collect(Collectors.toCollection(TreeSet::new));
    }

    @Override
    public Stop mapToDomain(JpaStop jpaStop) {
        StationId stationId = StationId.of(jpaStop.station().stationId());
        LocalTime arrival = jpaStop.arrival();
        LocalTime departure = jpaStop.departure();
        int position = jpaStop.position();

        return new Stop(stationId, arrival, departure, position);
    }

    public Stops mapToDomain(Collection<JpaStop> jpaStops) {
        return new Stops(jpaStops.stream().map(this::mapToDomain).collect(Collectors.toSet()));
    }
}
