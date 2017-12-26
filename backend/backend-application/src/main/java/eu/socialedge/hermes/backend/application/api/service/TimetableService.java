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
import eu.socialedge.hermes.backend.timetable.domain.Book;
import eu.socialedge.hermes.backend.timetable.domain.Document;
import eu.socialedge.hermes.backend.timetable.domain.TimetableGenerationService;
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

@Service
public class TimetableService implements TimetablesApiDelegate {

    private final TimetableGenerationService timetableGenerationService;
    private final LineRepository lineRepository;
    private final ScheduleRepository scheduleRepository;
    private final StationRepository stationRepository;

    public TimetableService(TimetableGenerationService timetableGenerationService, LineRepository lineRepository,
                            ScheduleRepository scheduleRepository, StationRepository stationRepository) {
        this.timetableGenerationService = timetableGenerationService;
        this.lineRepository = lineRepository;
        this.scheduleRepository = scheduleRepository;
        this.stationRepository = stationRepository;
    }

    @Override
    public ResponseEntity<Resource> generateSchedulePdf(String lineId, String stationId, List<String> scheduleIds) {
        val line = lineRepository.findOne(lineId);
        val station = stationRepository.findOne(stationId);
        val schedules = scheduleRepository.findAll(scheduleIds);
        if (line == null || station == null) {
            return ResponseEntity.notFound().build();
        }
        val document = timetableGenerationService.generateSingleLineStationTimetable(line, station, schedules);

        val filename = encode(document.nameWithExtension());
        val headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/pdf"));
        headers.setContentDispositionFormData(filename, filename);
        return new ResponseEntity<>(new ByteArrayResource(document.content()), headers, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Resource> generateSchedulesZip(List<String> scheduleIds, String lineId, String stationId) {
        val schedules = scheduleRepository.findAll(scheduleIds);

        List<Document> documents;
        String filename;
        if (stationId != null && stationRepository.exists(stationId)) {
            val station = stationRepository.findOne(stationId);
            documents = timetableGenerationService.generateStationTimetables(station, schedules);
            filename = station.getName();
        } else if (lineId != null && lineRepository.exists(lineId)) {
            val line = lineRepository.findOne(lineId);
            documents = timetableGenerationService.generateLineTimetables(schedules, line);
            filename = line.getName();
        } else {
            return ResponseEntity.badRequest().build();
        }

        val book = Book.of(documents);
        val zipResult = book.toZip();

        filename = encode(filename + ".zip");
        val headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/zip"));
        headers.setContentDispositionFormData(filename, filename);
        return new ResponseEntity<>(new ByteArrayResource(zipResult), headers, HttpStatus.OK);
    }

    private static String encode(String filename) {
        try {
            return URLEncoder.encode(filename,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            return filename;
        }
    }
}
