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
import eu.socialedge.hermes.backend.publication.domain.Publication;
import eu.socialedge.hermes.backend.transit.domain.infra.Station;
import lombok.val;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import eu.socialedge.hermes.backend.application.api.mapping.util.Entities;
import eu.socialedge.hermes.backend.schedule.domain.Schedule;
import eu.socialedge.hermes.backend.transit.domain.service.Line;

import static java.util.stream.Collectors.toList;

@Component
public class PublicationMapper implements Mapper<Publication, PublicationDTO> {

    @Override
    public PublicationDTO toDTO(Publication publication) {
        if (publication == null)
            return null;

        return new PublicationDTO()
            .id(publication.getId())
            .name(publication.getName())
            .date(publication.getDate())
            .lineId(publication.getLine().getId())
            .stationId(publication.getStation().getId())
            .scheduleIds(publication.getSchedules().stream().map(Schedule::getId).collect(toList()));
    }

    @Override
    public Publication toDomain(PublicationDTO dto) {
        if (dto == null)
            return null;

        try {
            val scheduleIds = dto.getScheduleIds().stream().map(ObjectId::new).collect(toList());
            val line = dto.getLineId() != null ? Entities.proxy(Line.class, new ObjectId(dto.getLineId())) : (Line) null;
            val station = dto.getStationId() != null ? Entities.proxy(Station.class, new ObjectId(dto.getStationId())) : (Station) null;
            val schedules = Entities.proxy(Schedule.class, scheduleIds);
            return new Publication(dto.getId(), dto.getName(), dto.getDate(), dto.getFile(), schedules, line, station);
        } catch (ReflectiveOperationException e) {
            throw new MappingException("Failed to create proxy Line entity", e);
        }
    }

}
