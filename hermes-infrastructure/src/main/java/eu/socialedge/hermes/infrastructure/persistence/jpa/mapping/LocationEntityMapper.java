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
package eu.socialedge.hermes.infrastructure.persistence.jpa.mapping;

import eu.socialedge.hermes.domain.geo.Location;
import eu.socialedge.hermes.infrastructure.persistence.jpa.entity.JpaLocation;
import org.springframework.stereotype.Component;

@Component
public class LocationEntityMapper implements EntityMapper<Location, JpaLocation> {

    @Override
    public JpaLocation mapToEntity(Location location) {
        JpaLocation jpaLocation = new JpaLocation();

        jpaLocation.latitude(location.latitude());
        jpaLocation.longitude(location.longitude());

        return jpaLocation;
    }

    @Override
    public Location mapToDomain(JpaLocation entity) {
        return Location.of(entity.latitude(), entity.longitude());
    }
}