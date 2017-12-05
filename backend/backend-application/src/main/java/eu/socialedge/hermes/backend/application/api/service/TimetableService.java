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
 *
 */
package eu.socialedge.hermes.backend.application.api.service;

import eu.socialedge.hermes.backend.application.api.TimetablesApiDelegate;
import eu.socialedge.hermes.backend.gen.SchedulePdfService;
import eu.socialedge.hermes.backend.schedule.repository.ScheduleRepository;
import eu.socialedge.hermes.backend.transit.domain.infra.StationRepository;
import eu.socialedge.hermes.backend.transit.domain.service.LineRepository;
import lombok.val;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TimetableService implements TimetablesApiDelegate {

    private final SchedulePdfService schedulePdfService;
    private final LineRepository lineRepository;
    private final ScheduleRepository scheduleRepository;
    private final StationRepository stationRepository;

    public TimetableService(SchedulePdfService schedulePdfService, LineRepository lineRepository,
                            ScheduleRepository scheduleRepository, StationRepository stationRepository) {
        this.schedulePdfService = schedulePdfService;
        this.lineRepository = lineRepository;
        this.scheduleRepository = scheduleRepository;
        this.stationRepository = stationRepository;
    }

    @Override
    public ResponseEntity<Resource> generateSchedulePdf(String lineId, String stationId, List<String> scheduleIds) {
        val line = lineRepository.findOne(lineId);
        val station = stationRepository.findOne(stationId);
        val schedules = scheduleIds.stream().map(scheduleRepository::findOne).collect(Collectors.toList());
        if (line == null || station == null || schedules.contains(null)) {
            return ResponseEntity.notFound().build();
        }

        val pdfResult = schedulePdfService.generateSingleLineStationPdf(line, station, schedules);

        val headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/pdf"));
        String filename = station.getName() + ".pdf";
        try {
            filename = URLEncoder.encode(filename,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            filename = "Schedule";
        }
        headers.setContentDispositionFormData(filename, filename);
        return new ResponseEntity<>(new ByteArrayResource(pdfResult), headers, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Resource> generateSchedulesZip(List<String> scheduleIds, String lineId, String stationId) {
        val schedules = scheduleIds.stream().map(scheduleRepository::findOne).collect(Collectors.toList());
        if (schedules.contains(null)) {
            return ResponseEntity.notFound().build();
        }

        byte[] zipResult;
        String filename;
        if (stationId != null && stationRepository.exists(stationId)) {
            val station = stationRepository.findOne(stationId);
            zipResult = schedulePdfService.generateStationSchedulesZip(station, schedules);
            filename = station.getName();
        } else if (lineId != null && lineRepository.exists(lineId)) {
            val line = lineRepository.findOne(lineId);
            zipResult = schedulePdfService.generateLineSchedulesZip(schedules, line);
            filename = line.getName();
        } else {
            return ResponseEntity.badRequest().build();
        }
        filename += ".zip";
        try {
            filename = URLEncoder.encode(filename,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            filename = "Schedules";
        }
        val headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/zip"));
        headers.setContentDispositionFormData(filename, filename);
        return new ResponseEntity<>(new ByteArrayResource(zipResult), headers, HttpStatus.OK);
    }
}
