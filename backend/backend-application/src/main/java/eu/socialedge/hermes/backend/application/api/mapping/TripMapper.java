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

package eu.socialedge.hermes.backend.application.api.mapping;

import eu.socialedge.hermes.backend.application.api.dto.StopDTO;
import eu.socialedge.hermes.backend.application.api.dto.TripDTO;
import eu.socialedge.hermes.backend.schedule.domain.Stop;
import eu.socialedge.hermes.backend.schedule.domain.Trip;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TripMapper implements Mapper<Trip, TripDTO> {

    private final Mapper<Stop, StopDTO> stopMapper;

    @Autowired
    public TripMapper(Mapper<Stop, StopDTO> stopMapper) {
        this.stopMapper = stopMapper;
    }

    @Override
    public TripDTO toDTO(Trip trip) {
        if (trip == null)
            return null;

        return new TripDTO()
            .headsign(trip.getHeadsign())
            .stops(stopMapper.toDTO(trip.getStops()));
    }

    @Override
    public Trip toDomain(TripDTO dto) {
        if (dto == null)
            return null;

        val headsign = dto.getHeadsign();
        val stops = stopMapper.toDomain(dto.getStops());

        return Trip.of(headsign, stops);
    }
}
