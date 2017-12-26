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
package eu.socialedge.hermes.backend.timetable.domain;

import eu.socialedge.hermes.backend.schedule.domain.Schedule;
import eu.socialedge.hermes.backend.timetable.domain.convert.DocumentConversionException;
import eu.socialedge.hermes.backend.timetable.domain.convert.DocumentConverter;
import eu.socialedge.hermes.backend.timetable.domain.gen.TimetableFactory;
import eu.socialedge.hermes.backend.transit.domain.infra.Station;
import eu.socialedge.hermes.backend.transit.domain.service.Line;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.Arrays.asList;

public class TimetableGenerationService {

    private final TimetableFactory timetableFactory;
    private final List<DocumentConverter> documentConverters = new ArrayList<>();

    public TimetableGenerationService(TimetableFactory timetableFactory,
                                      List<DocumentConverter> documentConverters) {
        this.timetableFactory = timetableFactory;

        if (documentConverters != null && !documentConverters.isEmpty())
            this.documentConverters.addAll(documentConverters);
    }

    public TimetableGenerationService(TimetableFactory timetableFactory,
                                      DocumentConverter... documentConverters) {
        this(timetableFactory, asList(documentConverters));
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

        val sourceTimetableDoc = timetableFactory.create(line, station, schedules);
        return convert(sourceTimetableDoc);
    }

    /**
     * Generates list containing {@link Document} schedules for single station from all lines
     *
     * @param station Station for which schedules files are generated and packed into zip. Must be present in all schedules
     * @param schedules List of schedules to be included in resulting zip.
     * @return List containing {@link Document} schedules for single station from all lines
     */
    public List<Document> generateStationTimetables(Station station, Iterable<Schedule> schedules) {
        val lineSchedules = StreamSupport.stream(schedules.spliterator(), false)
            .collect(Collectors.groupingBy(Schedule::getLine));

        val stationTimetables = new ArrayList<Document>();
        for (val line : lineSchedules.keySet()) {
            val sourceTimetableDoc = timetableFactory.create(line, station, lineSchedules.get(line));
            stationTimetables.add(convert(sourceTimetableDoc));
        }
        return stationTimetables;
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
            val sourceTimetableDoc = timetableFactory.create(line, station, schedules);
            val sourceInboundTimetableDoc = sourceTimetableDoc.rename("inbound/" + sourceTimetableDoc.name());

            lineSchedules.add(convert(sourceInboundTimetableDoc));
        }
        for (val station : outboundStations) {
            val sourceTimetableDoc = timetableFactory.create(line, station, schedules);
            val sourceOutboundTimetableDoc = sourceTimetableDoc.rename("outbound/" + sourceTimetableDoc.name());

            lineSchedules.add(convert(sourceOutboundTimetableDoc));
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

    private Document convert(Document source) {
        if (documentConverters.isEmpty())
            return source;

        for (DocumentConverter converter : documentConverters) {
            if (converter.supports(source)) {
                return converter.convert(source);
            }
        }

        throw new DocumentConversionException("No supported document converter found");
    }
}
