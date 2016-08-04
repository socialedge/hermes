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

import eu.socialedge.hermes.domain.operator.Agency;
import eu.socialedge.hermes.domain.operator.AgencyId;
import eu.socialedge.hermes.domain.operator.AgencyRepository;
import eu.socialedge.hermes.infrastructure.persistence.jpa.entity.JpaAgency;
import eu.socialedge.hermes.infrastructure.persistence.jpa.mapping.AgencyEntityMapper;
import eu.socialedge.hermes.infrastructure.persistence.jpa.repository.entity
        .SpringJpaAgencyRepository;

import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class SpringAgencyRepository extends SpringRepository<Agency, AgencyId,
                                                          JpaAgency, String>
                                            implements AgencyRepository {

    private final AgencyEntityMapper agencyEntityMapper;

    @Inject
    public SpringAgencyRepository(SpringJpaAgencyRepository jpaRepository,
                                  AgencyEntityMapper agencyEntityMapper) {
        super(jpaRepository);
        this.agencyEntityMapper = agencyEntityMapper;
    }

    @Override
    protected String mapToJpaEntityId(AgencyId agencyId) {
        return agencyId.toString();
    }

    @Override
    protected Agency mapToDomainObject(JpaAgency jpaAgency) {
        return agencyEntityMapper.mapToDomain(jpaAgency);
    }

    @Override
    protected JpaAgency mapToJpaEntity(Agency agency) {
        return agencyEntityMapper.mapToEntity(agency);
    }
}