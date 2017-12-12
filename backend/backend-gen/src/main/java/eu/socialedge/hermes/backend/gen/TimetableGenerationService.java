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

import eu.socialedge.hermes.backend.schedule.domain.Schedule;
import eu.socialedge.hermes.backend.transit.domain.infra.Station;
import eu.socialedge.hermes.backend.transit.domain.service.Line;

import java.util.List;

public interface TimetableGenerationService {

    /**
     * Generates {@link Document} containing all schedules for single station on a line.
     * Schedules must be within one {@link eu.socialedge.hermes.backend.transit.domain.service.Line}
     *
     * @param line Line containing specified station
     * @param station Station for which schedules file is generated. Must be present in all schedules
     * @param schedules List of schedules from one line which are included in resulting file. Must be created for same line
     * @return {@link Document} containing all schedules for single station on a line
     */
    Document generateSingleLineStationTimetable(Line line, Station station, Iterable<Schedule> schedules);

    /**
     * Generates list containing {@link Document} schedules for single station from all lines
     *
     * @param station Station for which schedules files are generated and packed into zip. Must be present in all schedules
     * @param schedules List of schedules to be included in resulting zip.
     * @return List containing {@link Document} schedules for single station from all lines
     */
    List<Document> generateStationTimetables(Station station, Iterable<Schedule> schedules);

    /**
     * Generates list containing {@link Document} schedules for all stations within single line
     * Resulting documents's names have prefixes"inbound" or "outbound" - depending on station direction.
     *
     * @param schedules List of schedules to be included in resulting zip. Must be created for same line
     * @param line Line containing all stations for which schedules files should be generated
     * @return List containing {@link Document} schedules for all stations within single line
     */
    List<Document> generateLineTimetables(Iterable<Schedule> schedules, Line line);
}
