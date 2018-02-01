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
import eu.socialedge.hermes.backend.application.api.mapping.util.Entities;
import eu.socialedge.hermes.backend.schedule.domain.Stop;
import eu.socialedge.hermes.backend.transit.domain.infra.Station;
import lombok.val;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
public class StopMapper implements Mapper<Stop, StopDTO> {

    @Override
    public StopDTO toDTO(Stop stop) {
        if (stop == null)
            return null;

        return new StopDTO()
            .name(stop.getStation().getName())
            .stationId(stop.getStation().getId())
            .arrival(stop.getArrival().toString())
            .departure(stop.getDeparture().toString());
    }

    @Override
    public Stop toDomain(StopDTO dto) {
        if (dto == null)
            return null;

        try {
            val arrival = LocalTime.parse(dto.getArrival());
            val departure = LocalTime.parse(dto.getDeparture());
            val stationProxy = Entities.proxy(Station.class, new ObjectId(dto.getStationId()));

            return Stop.of(arrival, departure, stationProxy);
        } catch (ReflectiveOperationException e) {
            throw new MappingException("Failed to create proxy Station entity", e);
        }
    }
}
