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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import eu.socialedge.hermes.backend.gen.serialization.ScheduleSerializer;
import eu.socialedge.hermes.backend.transit.domain.service.Line;

import eu.socialedge.hermes.backend.schedule.domain.Schedule;
import eu.socialedge.hermes.backend.transit.domain.infra.Station;
import lombok.val;

public class SchedulePdfGenerator {
    private final PdfGenerationService pdfGenerationService;
    private final ScheduleSerializer scheduleSerializer;

    public SchedulePdfGenerator(PdfGenerationService pdfGenerationService, ScheduleSerializer scheduleSerializer) {
        this.pdfGenerationService = pdfGenerationService;
        this.scheduleSerializer = scheduleSerializer;
    }

    /**
     * Generates byte[] representation of the pdf file containing all schedules for single station on a line.
     * Schedules must be within one {@link eu.socialedge.hermes.backend.transit.domain.service.Line}
     *
     * @param line Line containing specified station
     * @param station Station for which schedules file is generated. Must be present in all schedules
     * @param schedules List of schedules from one line which are included in resulting file. Must be created for same line
     * @return Byte array representation of the pdf file
     */
    public byte[] generateSingleLineStationPdf(Line line, Station station, Iterable<Schedule> schedules) {
        validateSchedules(schedules, line);
        return pdfGenerationService.generate(scheduleSerializer.serialize(line, station, schedules));
    }

    /**
     * Generates byte[] representation of zip archive containing pdf files schedules for single station from all lines
     *
     * @param station Station for which schedules files are generated and packed into zip. Must be present in all schedules
     * @param schedules List of schedules to be included in resulting zip.
     * @return Byte array representation of zip archive that contains pdf files for each line within provided schedules
     */
    public byte[] generateStationSchedulesZip(Station station, List<Schedule> schedules) {
        val lineSchedules = schedules.stream().collect(Collectors.groupingBy(Schedule::getLine));
        val stationSchedules = new HashMap<String, byte[]>(lineSchedules.size());
        for (val lineSchedule : lineSchedules.entrySet()) {
            val fileName = formatFileName(lineSchedule.getKey().getName(), station.getName());
            val file = generateSingleLineStationPdf(lineSchedule.getKey(), station, lineSchedule.getValue());
            stationSchedules.put(fileName, file);
        }
        return packToZip(stationSchedules);
    }

    /**
     * Generates byte[] representation of zip archive containing pdf file schedules for all stations within single line
     * Resulting zip contains two directories: "inbound", "outbound" - one for each line direction. Pdf files are placed
     * accordingly into these directories.
     *
     * @param schedules List of schedules to be included in resulting zip. Must be created for same line
     * @param line Line containing all stations for which schedules files should be generated
     * @return Byte array representation of zip archive that contains pdf files for each station within line
     */
    public byte[] generateLineSchedulesZip(List<Schedule> schedules, Line line) {
        val inboundStations = line.getInboundRoute().getStations();
        val outboundStations = line.getOutboundRoute().getStations();
        val lineSchedules = new HashMap<String, byte[]>(inboundStations.size() + outboundStations.size());
        for (val station : inboundStations) {
            val fileName = "inbound/" + formatFileName(line.getName(), station.getName());
            val stationScheduleFile = generateSingleLineStationPdf(line, station, schedules);
            lineSchedules.put(fileName, stationScheduleFile);
        }
        for (val station : outboundStations) {
            val fileName = "outbound/" + formatFileName(line.getName(), station.getName());
            val stationScheduleFile = generateSingleLineStationPdf(line, station, schedules);
            lineSchedules.put(fileName, stationScheduleFile);
        }

        return packToZip(lineSchedules);
    }

    private static byte[] packToZip(Map<String, byte[]> files) {
        try (val baos = new ByteArrayOutputStream(); val zos = new ZipOutputStream(baos)) {
            for (val file : files.entrySet()) {
                zos.putNextEntry(new ZipEntry(file.getKey()));
                zos.write(file.getValue());
                zos.closeEntry();
            }
            zos.finish();
            return baos.toByteArray();
        } catch (IOException ioe) {
            throw new PdfGenerationException("Exception while zip packaging", ioe);
        }
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
