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

import eu.socialedge.hermes.domain.v2.routing.Line;
import eu.socialedge.hermes.domain.v2.routing.LineId;
import eu.socialedge.hermes.domain.v2.routing.LineRepository;
import eu.socialedge.hermes.infrastructure.persistence.v2.jpa.entity.EntityMapper;
import eu.socialedge.hermes.infrastructure.persistence.v2.jpa.entity.JpaLine;
import eu.socialedge.hermes.infrastructure.persistence.v2.jpa.repository.entity.SpringJpaAgencyRepository;
import eu.socialedge.hermes.infrastructure.persistence.v2.jpa.repository.entity.SpringJpaLineRepository;
import eu.socialedge.hermes.infrastructure.persistence.v2.jpa.repository.entity.SpringJpaRouteRepository;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class SpringLineRepository extends SpringRepository<Line, LineId,
                                                        JpaLine, String>
                                            implements LineRepository {

    private final SpringJpaAgencyRepository agencyRepository;
    private final SpringJpaRouteRepository routeRepository;

    @Inject
    public SpringLineRepository(SpringJpaLineRepository jpaRepository,
                                SpringJpaAgencyRepository agencyRepository,
                                SpringJpaRouteRepository routeRepository) {
        super(jpaRepository);
        this.agencyRepository = agencyRepository;
        this.routeRepository = routeRepository;
    }

    @Override
    protected LineId extractDomainId(Line domainObject) {
        return domainObject.lineId();
    }

    @Override
    protected String mapToJpaEntityId(LineId domainId) {
        return domainId.toString();
    }

    @Override
    protected Line mapToDomainObject(JpaLine jpaEntity) {
        return EntityMapper.mapEntityToLine(jpaEntity);
    }

    @Override
    protected JpaLine mapToJpaEntity(Line domainObject) {
        return EntityMapper.mapLineToEntity(domainObject,
                routeId -> routeRepository.findOne(routeId.toString()),
                agencyId -> agencyRepository.findOne(agencyId.toString()));
    }
}
