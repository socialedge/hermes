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
package eu.socialedge.hermes.infrastructure.persistence.jpa.repository.domain;

import eu.socialedge.hermes.domain.infrastructure.Station;
import eu.socialedge.hermes.domain.infrastructure.StationId;
import eu.socialedge.hermes.domain.infrastructure.StationRepository;
import eu.socialedge.hermes.infrastructure.persistence.jpa.entity.JpaStation;
import eu.socialedge.hermes.infrastructure.persistence.jpa.mapping.StationEntityMapper;
import eu.socialedge.hermes.infrastructure.persistence.jpa.repository.entity
        .SpringJpaStationRepository;

import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class SpringStationRepository extends SpringRepository<Station, StationId,
                                                           JpaStation, String>
                                                        implements StationRepository {

    private final StationEntityMapper stationEntityMapper;

    @Inject
    public SpringStationRepository(SpringJpaStationRepository jpaRepository,
                                   StationEntityMapper stationEntityMapper) {
        super(jpaRepository);
        this.stationEntityMapper = stationEntityMapper;
    }

    @Override
    protected String mapToJpaEntityId(StationId domainId) {
        return domainId.toString();
    }

    @Override
    protected Station mapToDomainObject(JpaStation jpaStation) {
        return stationEntityMapper.mapToDomain(jpaStation);
    }

    @Override
    protected JpaStation mapToJpaEntity(Station station) {
        return stationEntityMapper.mapToEntity(station);
    }
}
