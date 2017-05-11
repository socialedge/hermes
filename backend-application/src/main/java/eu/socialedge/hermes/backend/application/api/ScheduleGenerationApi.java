/*
 * Hermes - The Municipal Transport Timetable System
 * Copyright (c) 2017 SocialEdge
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
package eu.socialedge.hermes.backend.application.api;

import eu.socialedge.hermes.backend.application.util.ResourceElementsExtractor;
import eu.socialedge.hermes.backend.schedule.domain.BasicScheduleGenerator;
import eu.socialedge.hermes.backend.schedule.repository.ScheduleRepository;
import eu.socialedge.hermes.backend.transit.domain.Line;
import eu.socialedge.hermes.backend.transit.domain.repository.LineRepository;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@BasePathAwareController
public class ScheduleGenerationApi {

    private static final String BASIC_GENERATOR = "generator=basic";

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ResourceElementsExtractor resourceElementsExtractor;

    @RequestMapping(path = "/schedules", method = POST)
    public ResponseEntity generateSchedule(@RequestBody @NotNull @Valid ScheduleSpecification spec,
                                           UriComponentsBuilder uriComponentsBuilder) {
        val lineId = resourceElementsExtractor.extractResourceId(Line.class, String.class, spec.line());

        val line = lineRepository.findOne(Long.parseLong(lineId));
        if (line == null) {
            return ResponseEntity.notFound().build();
        }

        val scheduleBuilder = BasicScheduleGenerator.builder()
            .line(line)
            .startTimeInbound(spec.startTimeInbound())
            .endTimeInbound(spec.endTimeInbound())
            .startTimeOutbound(spec.startTimeOutbound())
            .endTimeOutbound(spec.endTimeOutbound())
            .averageSpeed(spec.averageSpeed())
            .headway(spec.headway())
            .dwellTime(spec.dwellTime())
            .minLayover(spec.minLayover())
            .availability(spec.availability())
            .description(spec.description())
            .build();

        val generatedSchedule = scheduleBuilder.generate();
        val persistedSchedule = scheduleRepository.save(generatedSchedule);

        val scheduleUri = uriComponentsBuilder.path("/schedules/").path(persistedSchedule.id().toString()).build().toUri();
        return ResponseEntity.created(scheduleUri).build();
    }

    @RequestMapping(path = "/schedules", method = POST, headers = BASIC_GENERATOR)
    public ResponseEntity generateBasicSchedule(@RequestBody @NotNull @Valid ScheduleSpecification spec,
                                                UriComponentsBuilder uriComponentsBuilder) {
        return generateSchedule(spec, uriComponentsBuilder);
    }
}
