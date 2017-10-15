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

package eu.socialedge.hermes.backend.application.api.service;

import eu.socialedge.hermes.backend.application.api.TasksApiDelegate;
import eu.socialedge.hermes.backend.application.api.dto.ScheduleGenReqDTO;
import eu.socialedge.hermes.backend.application.api.dto.ScheduleGenTaskDTO;
import eu.socialedge.hermes.backend.application.api.mapping.AvailabilityMapper;
import eu.socialedge.hermes.backend.schedule.domain.gen.basic.BasicScheduleGenerator;
import eu.socialedge.hermes.backend.schedule.domain.gen.basic.DwellTimeResolver;
import eu.socialedge.hermes.backend.schedule.repository.ScheduleRepository;
import eu.socialedge.hermes.backend.transit.domain.service.LineRepository;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import tec.uom.se.quantity.Quantities;

import javax.measure.quantity.Speed;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.OffsetDateTime;

@Component
public class TaskService implements TasksApiDelegate {

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private AvailabilityMapper availabilityMapper;

    @Autowired
    private DwellTimeResolver dwellTimeResolver;

    /**
     * @deprecated Must be replaced with task queue + ScheduleGenTaskDTO -> TaskDTO
     */
    @Override
    @Deprecated
    public ResponseEntity<ScheduleGenTaskDTO> generateSchedule(ScheduleGenReqDTO spec) {
        val started = Instant.now();

        val line = lineRepository.findOne(spec.getLineId());
        if (line == null) {
            return ResponseEntity.notFound().build();
        }

        val scheduleBuilder = BasicScheduleGenerator.builder()
            .dwellTimeResolver(dwellTimeResolver)
            .line(line)
            .startTimeInbound(LocalTime.parse(spec.getStartTimeInbound()))
            .endTimeInbound(LocalTime.parse(spec.getEndTimeInbound()))
            .startTimeOutbound(LocalTime.parse(spec.getStartTimeOutbound()))
            .endTimeOutbound(LocalTime.parse(spec.getEndTimeOutbound()))
            .averageSpeed(Quantities.getQuantity(spec.getAverageSpeed()).asType(Speed.class))
            .headway(Duration.parse(spec.getHeadway()))
            .minLayover(Duration.parse(spec.getMinLayover()))
            .availability(availabilityMapper.toDomain(spec.getAvailability()))
            .description(spec.getDescription())
            .build();

        val generatedSchedule = scheduleBuilder.generate();
        val persistedSchedule = scheduleRepository.save(generatedSchedule);

        val finished = Instant.now();

        return ResponseEntity.ok().body(
            new ScheduleGenTaskDTO()
                .status("DONE")
                .scheduleId(persistedSchedule.getId())
                .started(OffsetDateTime.from(started))
                .finished(OffsetDateTime.from(finished))
        );
    }

    @Override
    public ResponseEntity<ScheduleGenTaskDTO> getScheduleGenerationStatus(String id) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
