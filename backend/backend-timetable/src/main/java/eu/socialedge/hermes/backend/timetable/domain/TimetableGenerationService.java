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
import eu.socialedge.hermes.backend.timetable.domain.convert.FileConversionException;
import eu.socialedge.hermes.backend.timetable.domain.convert.FileConverter;
import eu.socialedge.hermes.backend.timetable.domain.gen.TimetableFactory;
import eu.socialedge.hermes.backend.transit.domain.infra.Station;
import eu.socialedge.hermes.backend.transit.domain.service.Line;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.lang.String.format;
import static java.util.Arrays.asList;

public class TimetableGenerationService {

    private final TimetableFactory timetableFactory;
    private final List<FileConverter> fileConverters = new ArrayList<>();
    private static final String LINE_STATION_FILENAME_FORMAT = "(%s) - %s";

    public TimetableGenerationService(TimetableFactory timetableFactory,
                                      List<FileConverter> fileConverters) {
        this.timetableFactory = timetableFactory;

        if (fileConverters != null && !fileConverters.isEmpty())
            this.fileConverters.addAll(fileConverters);
    }

    public TimetableGenerationService(TimetableFactory timetableFactory,
                                      FileConverter... fileConverters) {
        this(timetableFactory, asList(fileConverters));
    }

    /**
     * Generates {@link File} containing all schedules for single station on a line.
     * Schedules must be within one {@link eu.socialedge.hermes.backend.transit.domain.service.Line}
     *
     * @param line Line containing specified station
     * @param station Station for which schedules file is generated. Must be present in all schedules
     * @param schedules List of schedules from one line which are included in resulting file. Must be created for same line
     * @return {@link File} containing all schedules for single station on a line
     */
    public File generateSingleLineStationTimetable(Line line, Station station, Iterable<Schedule> schedules) {
        validateSchedules(schedules, line);

        val sourceTimetableDoc = timetableFactory.create(line, station, schedules);
        return convert(sourceTimetableDoc);
    }

    /**
     * Generates list containing {@link File} schedules for single station from all lines
     *
     * @param station Station for which schedules files are generated and packed into zip. Must be present in all schedules
     * @param schedules List of schedules to be included in resulting zip.
     * @return List containing {@link File} schedules for single station from all lines
     */
    public List<File> generateStationTimetables(Station station, Iterable<Schedule> schedules) {
        val lineSchedules = StreamSupport.stream(schedules.spliterator(), false)
            .collect(Collectors.groupingBy(Schedule::getLine));

        val stationTimetables = new ArrayList<File>();
        for (val line : lineSchedules.keySet()) {
            val sourceTimetableDoc = timetableFactory.create(line, station, lineSchedules.get(line));
            val sourceTimetableDocRenamed = sourceTimetableDoc.rename(format(LINE_STATION_FILENAME_FORMAT, line.getName(), station.getName()));
            stationTimetables.add(convert(sourceTimetableDocRenamed));
        }
        return stationTimetables;
    }

    /**
     * Generates list containing {@link File} schedules for all stations within single line
     * Resulting files's names have prefixes "inbound" or "outbound" - depending on station direction.
     *
     * @param schedules List of schedules to be included in resulting zip. Must be created for same line
     * @param line Line containing all stations for which schedules files should be generated
     * @return List containing {@link File} schedules for all stations within single line
     */
    public List<File> generateLineTimetables(Iterable<Schedule> schedules, Line line) {
        validateSchedules(schedules, line);

        val inboundStations = line.getInboundRoute().getStations();
        val outboundStations = line.getOutboundRoute().getStations();
        val lineSchedules = new ArrayList<File>(inboundStations.size() + outboundStations.size());

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

    private File convert(File source) {
        if (fileConverters.isEmpty())
            return source;

        for (FileConverter converter : fileConverters) {
            if (converter.supports(source)) {
                return converter.convert(source);
            }
        }

        throw new FileConversionException("No supported file converter found");
    }
}
