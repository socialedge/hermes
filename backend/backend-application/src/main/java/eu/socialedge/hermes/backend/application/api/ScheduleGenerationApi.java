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
package eu.socialedge.hermes.backend.application.api;

import eu.socialedge.hermes.backend.schedule.domain.Schedule;
import eu.socialedge.hermes.backend.schedule.domain.gen.basic.BasicScheduleGenerator;
import eu.socialedge.hermes.backend.schedule.domain.gen.basic.DwellTimeResolver;
import eu.socialedge.hermes.backend.schedule.repository.ScheduleRepository;
import eu.socialedge.hermes.backend.transit.domain.service.LineRepository;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class ScheduleGenerationApi {

    private static final String BASIC_GENERATOR_HEADER = "generator=basic";

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private DwellTimeResolver dwellTimeResolver;

    @RequestMapping(path = "/tasks/schedules", method = POST)
    public ResponseEntity<Schedule> generateSchedule(@RequestBody @NotNull @Valid ScheduleGenerationRequest spec,
                                           UriComponentsBuilder uriComponentsBuilder) {
        return generateBasicSchedule(spec, uriComponentsBuilder);
    }

    @RequestMapping(path = "/tasks/schedules", method = POST, headers = BASIC_GENERATOR_HEADER)
    public ResponseEntity<Schedule> generateBasicSchedule(@RequestBody @NotNull @Valid ScheduleGenerationRequest spec,
                                                          UriComponentsBuilder uriComponentsBuilder) {

        val line = lineRepository.findOne(spec.getLineId());
        if (line == null) {
            return ResponseEntity.notFound().build();
        }

        val scheduleBuilder = BasicScheduleGenerator.builder()
            .dwellTimeResolver(dwellTimeResolver)
            .line(line)
            .startTimeInbound(spec.getStartTimeInbound())
            .endTimeInbound(spec.getEndTimeInbound())
            .startTimeOutbound(spec.getStartTimeOutbound())
            .endTimeOutbound(spec.getEndTimeOutbound())
            .averageSpeed(spec.getAverageSpeed())
            .headway(spec.getHeadway())
            .minLayover(spec.getMinLayover())
            .availability(spec.getAvailability())
            .description(spec.getDescription())
            .build();

        val generatedSchedule = scheduleBuilder.generate();
        val persistedSchedule = scheduleRepository.save(generatedSchedule);

        val scheduleUri = uriComponentsBuilder.path("/tasks/schedules").path(persistedSchedule.getId()).build().toUri();
        return ResponseEntity.created(scheduleUri).build();
    }
}
