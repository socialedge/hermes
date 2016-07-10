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
package eu.socialedge.hermes.repository.route;

import eu.socialedge.hermes.repository.SpringJpaRepository;
import eu.socialedge.hermes.domain.route.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Component
public class SpringJpaStationRepository extends SpringJpaRepository<Integer, Station> implements StationRepository{
    @Repository
    private interface InternalJpaRepository extends JpaRepository<Station, Integer> {}

    @Inject
    public SpringJpaStationRepository(JpaRepository<Station, Integer> internalRepository) {
        super(internalRepository);
    }
}
