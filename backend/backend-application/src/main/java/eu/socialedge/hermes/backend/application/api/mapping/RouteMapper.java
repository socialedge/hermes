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

import eu.socialedge.hermes.backend.application.api.dto.RouteDTO;
import eu.socialedge.hermes.backend.transit.domain.service.Route;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Component
public class RouteMapper implements Mapper<Route, RouteDTO> {

    private final SegmentMapper segmentMapper;

    @Autowired
    public RouteMapper(SegmentMapper segmentMapper) {
        this.segmentMapper = segmentMapper;
    }

    @Override
    public RouteDTO toDTO(Route route) {
        if (route == null)
            return null;

        val dto = new RouteDTO();

        for (val seg : route) {
            dto.add(segmentMapper.toDTO(seg));
        }

        return dto;
    }

    @Override
    public Route toDomain(RouteDTO dto) {
        if (dto == null)
            return null;

        return dto.stream()
            .map(segmentMapper::toDomain)
            .collect(Collectors.collectingAndThen(toList(), Route::new));
    }
}
