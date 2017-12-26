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

import eu.socialedge.hermes.backend.gen.serialization.ScheduleSerializer;
import eu.socialedge.hermes.backend.schedule.domain.Schedule;
import eu.socialedge.hermes.backend.transit.domain.infra.Station;
import eu.socialedge.hermes.backend.transit.domain.service.Line;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ScheduleTimetableService {
    private final DocumentGenerator documentGenerator;
    private final ScheduleSerializer scheduleSerializer;

    public ScheduleTimetableService(DocumentGenerator documentGenerator, ScheduleSerializer scheduleSerializer) {
        this.documentGenerator = documentGenerator;
        this.scheduleSerializer = scheduleSerializer;
    }

    /**
     * Generates {@link Document} containing all schedules for single station on a line.
     * Schedules must be within one {@link eu.socialedge.hermes.backend.transit.domain.service.Line}
     *
     * @param line Line containing specified station
     * @param station Station for which schedules file is generated. Must be present in all schedules
     * @param schedules List of schedules from one line which are included in resulting file. Must be created for same line
     * @return {@link Document} containing all schedules for single station on a line
     */
    public Document generateSingleLineStationTimetable(Line line, Station station, Iterable<Schedule> schedules) {
        validateSchedules(schedules, line);
        return documentGenerator.generate(station.getName(), scheduleSerializer.serialize(line, station, schedules));
    }

    /**
     * Generates list containing {@link Document} schedules for single station from all lines
     *
     * @param station Station for which schedules files are generated and packed into zip. Must be present in all schedules
     * @param schedules List of schedules to be included in resulting zip.
     * @return List containing {@link Document} schedules for single station from all lines
     */
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

    /**
     * Generates list containing {@link Document} schedules for all stations within single line
     * Resulting documents's names have prefixes"inbound" or "outbound" - depending on station direction.
     *
     * @param schedules List of schedules to be included in resulting zip. Must be created for same line
     * @param line Line containing all stations for which schedules files should be generated
     * @return List containing {@link Document} schedules for all stations within single line
     */
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
