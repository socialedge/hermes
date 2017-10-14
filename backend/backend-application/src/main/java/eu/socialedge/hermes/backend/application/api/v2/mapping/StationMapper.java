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

import eu.socialedge.hermes.backend.application.api.dto.StationDTO;
import eu.socialedge.hermes.backend.transit.domain.VehicleType;
import eu.socialedge.hermes.backend.transit.domain.infra.Station;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.stream.Collectors;

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

        val vehicleTypeNames = station.getVehicleTypes()
            .stream().map(VehicleType::name)
            .collect(Collectors.toList());

        return new StationDTO()
            .id(station.getId())
            .name(station.getName())
            .description(station.getDescription())
            .location(locationMapper.toDTO(station.getLocation()))
            .vehicleType(vehicleTypeNames)
            .dwell(station.getDwell().toString());
    }

    @Override
    public Station toDomain(StationDTO dto) {
        if (dto == null)
            return null;

        try {
            val vehicleTypes = dto.getVehicleType()
                .stream().map(VehicleType::fromNameOrOther)
                .collect(Collectors.toSet());

            return new Station.Builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .location(locationMapper.toDomain(dto.getLocation()))
                .vehicleType(vehicleTypes)
                .dwell(Duration.parse(dto.getDwell()))
                .build();
        } catch (DateTimeParseException e) {
            throw new MappingException("Failed to parse dwell time from string", e);
        }
    }
}
