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

import eu.socialedge.hermes.backend.application.api.GenApiDelegate;
import eu.socialedge.hermes.backend.application.api.dto.ScheduleDTO;
import eu.socialedge.hermes.backend.application.api.dto.ScheduleGenReqDTO;
import eu.socialedge.hermes.backend.application.api.mapping.AvailabilityMapper;
import eu.socialedge.hermes.backend.application.api.mapping.Mapper;
import eu.socialedge.hermes.backend.schedule.domain.Schedule;
import eu.socialedge.hermes.backend.schedule.domain.gen.basic.BasicScheduleGenerator;
import eu.socialedge.hermes.backend.schedule.domain.gen.basic.DwellTimeResolver;
import eu.socialedge.hermes.backend.schedule.repository.ScheduleRepository;
import eu.socialedge.hermes.backend.transit.domain.service.LineRepository;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import tec.uom.se.quantity.Quantities;

import javax.measure.quantity.Speed;
import java.time.Duration;
import java.time.LocalTime;

@Component
public class GenService implements GenApiDelegate {

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private AvailabilityMapper availabilityMapper;

    @Autowired
    private DwellTimeResolver dwellTimeResolver;

    @Autowired
    private Mapper<Schedule, ScheduleDTO> scheduleMapper;

    @Override
    public ResponseEntity<ScheduleDTO> generateSchedule(ScheduleGenReqDTO spec) {
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

        return ResponseEntity.ok().body(scheduleMapper.toDTO(persistedSchedule));
    }
}
