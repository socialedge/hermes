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
package eu.socialedge.hermes.infrastructure.persistence.v2.jpa.repository.domain;

import eu.socialedge.hermes.domain.v2.RepositoryException;
import eu.socialedge.hermes.domain.v2.operator.Agency;
import eu.socialedge.hermes.domain.v2.operator.AgencyId;
import eu.socialedge.hermes.domain.v2.operator.AgencyRepository;
import eu.socialedge.hermes.infrastructure.persistence.v2.jpa.entity.EntityMapper;
import eu.socialedge.hermes.infrastructure.persistence.v2.jpa.entity.JpaAgency;
import eu.socialedge.hermes.infrastructure.persistence.v2.jpa.repository.entity.SpringJpaAgencyRepository;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.net.MalformedURLException;

@Component
public class SpringAgencyRepository extends SpringRepository<Agency, AgencyId,
                                                          JpaAgency, String>
                                            implements AgencyRepository {
    @Inject
    public SpringAgencyRepository(SpringJpaAgencyRepository jpaRepository) {
        super(jpaRepository);
    }

    @Override
    protected AgencyId extractDomainId(Agency domainObject) {
        return domainObject.agencyId();
    }

    @Override
    protected String mapToJpaEntityId(AgencyId domainId) {
        return domainId.toString();
    }

    @Override
    protected Agency mapToDomainObject(JpaAgency jpaEntity) {
        try {
            return EntityMapper.mapEntityToAgency(jpaEntity);
        } catch (MalformedURLException e) {
            throw new RepositoryException("Failed to map JPA entity to domain Agency object", e);
        }
    }

    @Override
    protected JpaAgency mapToJpaEntity(Agency domainObject) {
        return EntityMapper.mapAgencyToEntity(domainObject);
    }
}
