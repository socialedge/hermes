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
package eu.socialedge.hermes.backend.schedule.domain.api;

import eu.socialedge.hermes.backend.schedule.domain.BasicScheduleGenerator;
import eu.socialedge.hermes.backend.schedule.domain.ScheduleSpecification;
import eu.socialedge.hermes.backend.schedule.domain.repository.ScheduleRepository;
import eu.socialedge.hermes.backend.transit.domain.Route;
import eu.socialedge.hermes.backend.transit.domain.repository.RouteRepository;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(path = "/schedules")
public class ScheduleGenerationApi {

    private static final String BASIC_GENERATOR = "generator=basic";

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @RequestMapping(method = POST, headers = BASIC_GENERATOR)
    public ResponseEntity generateBasicSchedule(@RequestBody @NotNull @Valid ScheduleSpecification spec) {
        val inboundRouteOpt = findRoute(spec.inboundRouteId());
        if (!inboundRouteOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        val outboundRouteOpt = findRoute(spec.outboundRouteId());
        if (!outboundRouteOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        val scheduleBuilder = BasicScheduleGenerator.builder()
            .routeInbound(inboundRouteOpt.get())
            .routeOutbound(outboundRouteOpt.get())
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

        val scheduleUri = UriComponentsBuilder.fromPath("/schedules/").path(persistedSchedule.id().toString()).build().toUri();
        return ResponseEntity.created(scheduleUri).build();
    }

    private Optional<Route> findRoute(long routeId) {
        return Optional.ofNullable(routeRepository.findOne(routeId));
    }
}
