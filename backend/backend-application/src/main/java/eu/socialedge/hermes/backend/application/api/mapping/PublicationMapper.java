/*
 * Hermes - The Municipal Transport Timetable System
 * Copyright (c) 2016-2018 SocialEdge
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

import eu.socialedge.hermes.backend.application.api.dto.PublicationDTO;
import eu.socialedge.hermes.backend.application.api.mapping.util.Entities;
import eu.socialedge.hermes.backend.schedule.domain.Schedule;
import eu.socialedge.hermes.backend.timetable.domain.File;
import eu.socialedge.hermes.backend.timetable.domain.FileType;
import eu.socialedge.hermes.backend.timetable.domain.Publication;
import eu.socialedge.hermes.backend.transit.domain.infra.Station;
import eu.socialedge.hermes.backend.transit.domain.service.Line;
import lombok.val;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

@Component
public class PublicationMapper implements Mapper<Publication, PublicationDTO> {

    @Override
    public PublicationDTO toDTO(Publication publication) {
        if (publication == null)
            return null;

        val lineId = nonNull(publication.getLine()) ? publication.getLine().getId() : (String) null;
        val stationId = nonNull(publication.getStation()) ? publication.getStation().getId() : (String) null;

        return new PublicationDTO()
            .id(publication.getId())
            .name(publication.getFile().name())
            .date(publication.getDate())
            .lineId(lineId)
            .stationId(stationId)
            .scheduleIds(publication.getSchedules().stream().map(Schedule::getId).collect(toList()));
    }

    @Override
    public Publication toDomain(PublicationDTO dto) {
        if (dto == null)
            return null;

        try {
            val scheduleIds = dto.getScheduleIds();
            val line = dto.getLineId() != null ? Entities.proxy(Line.class, dto.getLineId()) : (Line) null;
            val station = dto.getStationId() != null ? Entities.proxy(Station.class, dto.getStationId()) : (Station) null;
            val schedules = Entities.proxy(Schedule.class, scheduleIds);
            return new Publication(dto.getId(), dto.getDate(), new File(dto.getName(), dto.getFile(), FileType.UNKNOWN), schedules, line, station);
        } catch (ReflectiveOperationException e) {
            throw new MappingException("Failed to create proxy Publication entity", e);
        }
    }

}
