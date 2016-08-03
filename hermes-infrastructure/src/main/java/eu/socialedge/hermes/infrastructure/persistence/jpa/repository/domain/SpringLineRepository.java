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

import eu.socialedge.hermes.domain.transit.Line;
import eu.socialedge.hermes.domain.transit.LineId;
import eu.socialedge.hermes.domain.transit.LineRepository;
import eu.socialedge.hermes.infrastructure.persistence.jpa.entity.JpaLine;
import eu.socialedge.hermes.infrastructure.persistence.jpa.mapping.LineEntityMapper;
import eu.socialedge.hermes.infrastructure.persistence.jpa.repository.entity
        .SpringJpaLineRepository;

import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class SpringLineRepository extends SpringRepository<Line, LineId,
                                                        JpaLine, String>
                                            implements LineRepository {

    private final LineEntityMapper lineEntityMapper;

    @Inject
    public SpringLineRepository(SpringJpaLineRepository jpaRepository,
                                LineEntityMapper lineEntityMapper) {
        super(jpaRepository);
        this.lineEntityMapper = lineEntityMapper;
    }

    @Override
    protected LineId extractDomainId(Line line) {
        return line.lineId();
    }

    @Override
    protected String mapToJpaEntityId(LineId lineId) {
        return lineId.toString();
    }

    @Override
    protected Line mapToDomainObject(JpaLine jpaLine) {
        return lineEntityMapper.mapToDomain(jpaLine);
    }

    @Override
    protected JpaLine mapToJpaEntity(Line line) {
        return lineEntityMapper.mapToEntity(line);
    }
}
