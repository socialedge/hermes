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

package eu.socialedge.hermes.backend.export;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import eu.socialedge.hermes.backend.export.data.StationScheduleTemplate;
import lombok.val;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import eu.socialedge.hermes.backend.schedule.domain.Schedule;
import eu.socialedge.hermes.backend.transit.domain.infra.Station;
import eu.socialedge.hermes.backend.transit.domain.service.Line;

// TODO javadoc
// TODO unit tests
public class SchedulePdfGenerator {
    private static final String TEMPLATES_FOLDER = "templates";

    static {
        Velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        Velocity.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        Velocity.init();
    }

    private final String apiToken;
    private final String url;
    private final String templateName;

    public SchedulePdfGenerator(String apiToken, String url, String templateName) {
        this.apiToken = apiToken;
        this.url = url;
        this.templateName = templateName;
    }

    public byte[] generateSingleLineStationPdf(Station station, List<Schedule> schedules) {
        // TODO check lines
        val line = schedules.get(0).getLine();
        return createPdf(StationScheduleTemplate.create(station, schedules, line));
    }

    public byte[] generateStationSchedulesZip(Station station, List<Schedule> schedules) {
        val lineSchedules = schedules.stream().collect(Collectors.groupingBy(Schedule::getLine));
        val stationSchedules = new HashMap<String, byte[]>(lineSchedules.size());
        for (Map.Entry<Line, List<Schedule>> lineSchedule : lineSchedules.<Line, List<Schedule>>entrySet()) {
            val fileName = formatFileName(lineSchedule.getKey().getName(), station.getName());
            val file = generateSingleLineStationPdf(station, lineSchedule.getValue());
            stationSchedules.put(fileName, file);
        }
        return packToZip(stationSchedules);
    }

    public byte[] generateLineSchedulesZip(List<Schedule> schedules) {
        val line = schedules.get(0).getLine();

        val inboundStations = line.getInboundRoute().getStations();
        val outboundStations = line.getOutboundRoute().getStations();
        val lineSchedules = new HashMap<String, byte[]>(inboundStations.size() + outboundStations.size());
        for (val station : inboundStations) {
            val fileName = "inbound/" + formatFileName(line.getName(), station.getName());
            val stationScheduleFile = generateSingleLineStationPdf(station, schedules);
            lineSchedules.put(fileName, stationScheduleFile);
        }
        for (Station station : outboundStations) {
            val fileName = "outbound/" + formatFileName(line.getName(), station.getName());
            val stationScheduleFile = generateSingleLineStationPdf(station, schedules);
            lineSchedules.put(fileName, stationScheduleFile);
        }

        return packToZip(lineSchedules);
    }

    private byte[] packToZip(Map<String, byte[]> files) {
        try (val baos = new ByteArrayOutputStream();
             val zos = new ZipOutputStream(baos)) {

            for (val file : files.entrySet()) {
                zos.putNextEntry(new ZipEntry(file.getKey()));
                zos.write(file.getValue());
                zos.closeEntry();
            }
            zos.finish();

            return baos.toByteArray();
        }
        catch (IOException ioe) {
            throw new RuntimeException("Exception while zip packaging", ioe);
        }
    }

    private byte[] createPdf(StationScheduleTemplate entity) {
        String entityString = entityToTemplateString(entity).replaceAll("\"", "\\\\\"");
        entityString = String.format("{\"html\": \"%s\"}", entityString).replaceAll("[\n|\t|\r]", "");
        System.out.println(entityString);
//        OkHttpClient client = new OkHttpClient();
//        MediaType mediaType = MediaType.parse("application/json");
//        RequestBody body = RequestBody.create(mediaType, entityString);
//        Request request = new Request.Builder()
//            .url(url)
//            .post(body)
//            .addHeader("x-access-token", apiToken)
//            .addHeader("content-type", "application/json")
//            .addHeader("cache-control", "no-cache")
//            .build();
//
//        try {
//            Response response = client.newCall(request).execute();
//            if (response.isSuccessful()) {
//                return response.body().bytes();
//            } else {
//                throw new PdfGenerationException("Pdf generation failed: " + response.body().string());
//            }
//        } catch (IOException e) {
//            throw new PdfGenerationException("Pdf generation failed:", e);
//        }
        return null;
    }

    private String entityToTemplateString(StationScheduleTemplate entity) {
        val writer = new StringWriter();
        val context = new VelocityContext();
        context.put("entity", entity);
        getTemplate(templateName).merge(context, writer);
        return writer.toString();
    }

    private static Template getTemplate(String templateName) {
        try {
            return Velocity.getTemplate("/" + TEMPLATES_FOLDER + "/" + templateName);
        } catch (ResourceNotFoundException enfe) {
            throw new RuntimeException("Template '" + templateName + "' not found", enfe);
        } catch (ParseErrorException pee) {
            throw new RuntimeException("Template '" + templateName + "' is not parsable", pee);
        }
    }

    private static String formatFileName(String lineName, String stationName) {
        return String.format("(%s) - %s.pdf", lineName, stationName);
    }
}
