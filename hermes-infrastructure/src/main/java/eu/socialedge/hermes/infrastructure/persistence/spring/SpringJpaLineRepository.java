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
package eu.socialedge.hermes.infrastructure.persistence.spring;

import eu.socialedge.hermes.domain.infrastructure.Line;
import eu.socialedge.hermes.domain.infrastructure.LineRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Component
public class SpringJpaLineRepository extends SpringJpaRepository<String, Line> implements LineRepository {
    @Repository
    private interface InternalJpaRepository extends JpaRepository<Line, String> {}

    @Inject
    public SpringJpaLineRepository(JpaRepository<Line, String> internalRepository) {
        super(internalRepository);
    }
}
