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
package eu.socialedge.hermes.domain.timetable.dto;

import eu.socialedge.hermes.domain.SpecificationMapper;
import eu.socialedge.hermes.domain.infrastructure.StationId;
import eu.socialedge.hermes.domain.timetable.Stop;

import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
public class StopSpecificationMapper implements SpecificationMapper<StopSpecification, Stop> {

    @Override
    public StopSpecification toDto(Stop stop) {
        StopSpecification stopSpec = new StopSpecification();

        stopSpec.stationId = stop.stationId().toString();
        stopSpec.arrival = stop.arrival().toString();
        stopSpec.departure = stop.departure().toString();

        return stopSpec;
    }

    @Override
    public Stop fromDto(StopSpecification spec) {
        return new Stop(StationId.of(spec.stationId),
                        LocalTime.parse(spec.arrival),
                        LocalTime.parse(spec.departure));
    }
}
