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

import eu.socialedge.hermes.backend.application.api.dto.AvailabilityDTO;
import eu.socialedge.hermes.backend.schedule.domain.Availability;
import lombok.val;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.util.ArrayList;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Component
public class AvailabilityMapper implements Mapper<Availability, AvailabilityDTO> {

    @Override
    public AvailabilityDTO toDTO(Availability availability) {
        if (availability == null)
            return null;

        val daysOfWeekNames = availability.getAvailabilityDays()
            .stream().map(Enum::name)
            .collect(toList());

        return new AvailabilityDTO()
            .dayOfWeek(daysOfWeekNames)
            .startDate(availability.getStartDate())
            .endDate(availability.getEndDate())
            .exceptionDays(new ArrayList<>(availability.getExceptionDays()));
    }

    @Override
    public Availability toDomain(AvailabilityDTO dto) {
        if (dto == null)
            return null;

        val daysOfWeek = dto.getDayOfWeek().stream()
            .map(String::toUpperCase)
            .map(DayOfWeek::valueOf)
            .collect(toSet());

        return new Availability.Builder()
            .from(dto.getStartDate())
            .to(dto.getEndDate())
            .daysOfWeek(daysOfWeek)
            .exceptionDays(dto.getExceptionDays())
            .build();
    }
}
