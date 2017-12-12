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
package eu.socialedge.hermes.backend.gen;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import eu.socialedge.hermes.backend.gen.serialization.ScheduleSerializer;
import eu.socialedge.hermes.backend.transit.domain.service.Line;

import eu.socialedge.hermes.backend.schedule.domain.Schedule;
import eu.socialedge.hermes.backend.transit.domain.infra.Station;
import lombok.val;

public class ScheduleTimetableService implements TimetableGenerationService {
    private final DocumentGenerator documentGenerator;
    private final ScheduleSerializer scheduleSerializer;

    public ScheduleTimetableService(DocumentGenerator documentGenerator, ScheduleSerializer scheduleSerializer) {
        this.documentGenerator = documentGenerator;
        this.scheduleSerializer = scheduleSerializer;
    }

    @Override
    public Document generateSingleLineStationTimetable(Line line, Station station, Iterable<Schedule> schedules) {
        validateSchedules(schedules, line);
        return documentGenerator.generate(station.getName(), scheduleSerializer.serialize(line, station, schedules));
    }

    @Override
    public List<Document> generateStationTimetables(Station station, Iterable<Schedule> schedules) {
        val lineSchedules = StreamSupport.stream(schedules.spliterator(), false).collect(Collectors.groupingBy(Schedule::getLine));
        val stationSchedules = new ArrayList<Document>();
        for (val line : lineSchedules.keySet()) {
            val fileName = formatFileName(line.getName(), station.getName());
            val file = documentGenerator.generate(fileName, scheduleSerializer.serialize(line, station, lineSchedules.get(line)));
            stationSchedules.add(file);
        }
        return stationSchedules;
    }

    @Override
    public List<Document> generateLineTimetables(Iterable<Schedule> schedules, Line line) {
        validateSchedules(schedules, line);
        val inboundStations = line.getInboundRoute().getStations();
        val outboundStations = line.getOutboundRoute().getStations();
        val lineSchedules = new ArrayList<Document>(inboundStations.size() + outboundStations.size());
        for (val station : inboundStations) {
            val fileName = "inbound/" + formatFileName(line.getName(), station.getName());
            lineSchedules.add(documentGenerator.generate(fileName, scheduleSerializer.serialize(line, station, schedules)));
        }
        for (val station : outboundStations) {
            val fileName = "outbound/" + formatFileName(line.getName(), station.getName());
            lineSchedules.add(documentGenerator.generate(fileName, scheduleSerializer.serialize(line, station, schedules)));
        }

        return lineSchedules;
    }

    private static void validateSchedules(Iterable<Schedule> schedules, Line line) {
        for (val schedule : schedules) {
            if (!schedule.getLine().equals(line)) {
                throw new IllegalArgumentException("Schedules must be within one line for pdf generation");
            }
        }
    }

    private static String formatFileName(String lineName, String stationName) {
        return String.format("(%s) - %s.pdf", lineName, stationName);
    }
}