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
package eu.socialedge.hermes.backend.application.api.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import eu.socialedge.hermes.backend.application.api.PublicationsApiDelegate;
import eu.socialedge.hermes.backend.application.api.dto.PublicationDTO;
import eu.socialedge.hermes.backend.application.api.dto.PublicationSpecificationDTO;
import eu.socialedge.hermes.backend.application.api.mapping.PublicationMapper;
import eu.socialedge.hermes.backend.timetable.domain.File;
import eu.socialedge.hermes.backend.timetable.domain.FileType;
import eu.socialedge.hermes.backend.timetable.domain.Publication;
import eu.socialedge.hermes.backend.timetable.repository.PublicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import eu.socialedge.hermes.backend.schedule.repository.ScheduleRepository;
import eu.socialedge.hermes.backend.timetable.domain.Folder;
import eu.socialedge.hermes.backend.timetable.domain.TimetableGenerationService;
import eu.socialedge.hermes.backend.transit.domain.infra.StationRepository;
import eu.socialedge.hermes.backend.transit.domain.service.LineRepository;
import lombok.val;

import static java.util.stream.Collectors.toList;

@Service
public class PublicationService extends PagingAndSortingService<Publication, String, PublicationDTO> implements PublicationsApiDelegate {

    private final TimetableGenerationService timetableGenerationService;
    private final LineRepository lineRepository;
    private final ScheduleRepository scheduleRepository;
    private final StationRepository stationRepository;

    @Autowired
    public PublicationService(PublicationRepository repository, PublicationMapper mapper, TimetableGenerationService timetableGenerationService,
                              LineRepository lineRepository, ScheduleRepository scheduleRepository, StationRepository stationRepository) {
        super(repository, mapper);
        this.timetableGenerationService = timetableGenerationService;
        this.lineRepository = lineRepository;
        this.scheduleRepository = scheduleRepository;
        this.stationRepository = stationRepository;
    }

    @Override
    public ResponseEntity<List<PublicationDTO>> listAllPublications(Integer size, Integer page, String sort, String filtering) {
        return list(size, page, sort, filtering);
    }

    @Override
    public ResponseEntity<PublicationDTO> getPublication(String id) {
        return get(id);
    }

    @Override
    public ResponseEntity<Resource> getPublicationContent(String id) {
        val publication = repository.findOne(id);
        if (publication == null) {
            return ResponseEntity.notFound().build();
        }
        val file = publication.getFile();
        val filename = encode(file.nameWithExtension());
        val headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(file.type().mediaType()));
        headers.setContentDispositionFormData(filename, filename);
        return new ResponseEntity<>(new ByteArrayResource(file.content()), headers, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PublicationDTO> publishSchedules(PublicationSpecificationDTO dto) {
        if (dto.getLineId() != null && !lineRepository.exists(dto.getLineId())) {
            return ResponseEntity.notFound().build();
        }
        if (dto.getStationId() != null && !stationRepository.exists(dto.getStationId())) {
            return ResponseEntity.notFound().build();
        }
        val line = Optional.ofNullable(dto.getLineId()).map(lineRepository::findOne).orElse(null);
        val station = Optional.ofNullable(dto.getStationId()).map(stationRepository::findOne).orElse(null);
        val schedules = scheduleRepository.findAll(dto.getScheduleIds());
        File file;
        if (line != null && station != null) {
            file = timetableGenerationService.generateSingleLineStationTimetable(line, station, schedules);
        } else if (line != null) {
            val folder = timetableGenerationService.generateLineTimetables(schedules, line);
            file = new File(line.getName(), Folder.of(folder).toZip(), FileType.ZIP);
        } else if (station != null) {
            val folder = timetableGenerationService.generateStationTimetables(station, schedules);
            file = new File(station.getName(), Folder.of(folder).toZip(), FileType.ZIP);
        } else {
            return ResponseEntity.badRequest().build();
        }
        val schedulesList = StreamSupport.stream(schedules.spliterator(), false).collect(toList());
        val publication = new Publication(file, schedulesList, line, station);
        val persistedPublication = repository.save(publication);
        return new ResponseEntity<>(mapper.toDTO(persistedPublication), HttpStatus.OK);
    }

    private static String encode(String filename) {
        try {
            return URLEncoder.encode(filename,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            return filename;
        }
    }
}
