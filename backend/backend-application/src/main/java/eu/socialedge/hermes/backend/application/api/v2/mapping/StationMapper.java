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

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.stream.Collectors;

import eu.socialedge.hermes.backend.application.api.dto.StationDTO;
import eu.socialedge.hermes.backend.transit.domain.infra.Station;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.socialedge.hermes.backend.transit.domain.VehicleType;
import lombok.val;

@Component
public class StationMapper implements Mapper<Station, StationDTO> {

    private final LocationMapper locationMapper;

    @Autowired
    public StationMapper(LocationMapper locationMapper) {
        this.locationMapper = locationMapper;
    }

    @Override
    public StationDTO toDTO(Station station) {
        if (station == null)
            return null;

        val dto = new StationDTO();

        dto.setId(station.getId());
        dto.setName(station.getName());
        dto.setDescription(station.getDescription());
        dto.setLocation(locationMapper.toDTO(station.getLocation()));
        dto.setVehicleType(station.getVehicleTypes().stream().map(VehicleType::name).collect(Collectors.toList()));
        dto.setDwell(station.getDwell().toString());

        return dto;
    }

    @Override
    public Station toDomain(StationDTO dto) {
        if (dto == null)
            return null;

        try {
            val id = dto.getId();
            val name = dto.getName();
            val desc = dto.getDescription();
            val location = locationMapper.toDomain(dto.getLocation());
            val vt = dto.getVehicleType().stream().map(VehicleType::fromNameOrOther).collect(Collectors.toSet());
            val dwell = isBlank(dto.getDwell()) ? (Duration) null : Duration.parse(dto.getDwell());

            return new Station(id, name, desc, vt, location, dwell);
        } catch (DateTimeParseException e) {
            throw new MappingException("Failed to map dto to station entity", e);
        }
    }
}
