/*
 * Hermes - The Municipal Transport Timetable System
 * Copyright (c) 2016-2017 SocialEdge
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

package eu.socialedge.hermes.backend.application.api.v2.mapping;

import eu.socialedge.hermes.backend.application.api.dto.LocationDTO;
import eu.socialedge.hermes.backend.transit.domain.geo.Location;
import lombok.val;
import org.springframework.stereotype.Component;

@Component
public class LocationMapper implements Mapper<Location, LocationDTO> {

    @Override
    public LocationDTO toDTO(Location loc) {
        if (loc == null)
            return null;

        val dto = new LocationDTO();

        dto.setLatitude(loc.getLatitude());
        dto.setLongitude(loc.getLongitude());

        return dto;
    }

    @Override
    public Location toDomain(LocationDTO dto) {
        return Location.of(dto.getLatitude(), dto.getLongitude());
    }
}
