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

import eu.socialedge.hermes.backend.application.api.dto.SegmentDTO;
import eu.socialedge.hermes.backend.application.api.dto.SegmentVertexDTO;
import eu.socialedge.hermes.backend.application.api.v2.mapping.util.Entities;
import eu.socialedge.hermes.backend.transit.domain.infra.Station;
import eu.socialedge.hermes.backend.transit.domain.service.Segment;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tec.uom.se.quantity.Quantities;

import static java.util.stream.Collectors.toList;
import static tec.uom.se.unit.Units.METRE;

@Component
public class SegmentMapper implements Mapper<Segment, SegmentDTO> {

    private final LocationMapper locMapper;

    @Autowired
    public SegmentMapper(LocationMapper locMapper) {
        this.locMapper = locMapper;
    }

    @Override
    public SegmentDTO toDTO(Segment segment) {
        if (segment == null)
            return null;

        val segmentStartDto = new SegmentVertexDTO()
            .stationId(segment.getBegin().getId())
            .name(segment.getBegin().getName())
            .location(locMapper.toDTO(segment.getBegin().getLocation()));

        val segmentEndDto = new SegmentVertexDTO()
            .stationId(segment.getEnd().getId())
            .name(segment.getEnd().getName())
            .location(locMapper.toDTO(segment.getEnd().getLocation()));

        val segmentWaypointLocsDTO = segment.getWaypoints()
            .stream().map(locMapper::toDTO).collect(toList());

        return new SegmentDTO()
            .begin(segmentStartDto)
            .end(segmentEndDto)
            .length(segment.getLength().getValue().doubleValue())
            .waypoints(segmentWaypointLocsDTO);
    }

    @Override
    public Segment toDomain(SegmentDTO dto) {
        if (dto == null)
            return null;

        try {
            val begin = Entities.proxy(Station.class, dto.getBegin().getStationId());
            val end = Entities.proxy(Station.class, dto.getEnd().getStationId());
            val length = dto.getLength();
            val waypoints = locMapper.toDomain(dto.getWaypoints());

            if (length == null)
                return Segment.of(begin, end, waypoints);

            return Segment.of(begin, end, Quantities.getQuantity(length, METRE), waypoints);
        } catch (ReflectiveOperationException e) {
            throw new MappingException("Failed to create proxy station entity", e);
        }
    }
}
