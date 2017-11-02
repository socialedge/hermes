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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import eu.socialedge.hermes.backend.schedule.domain.Availability;
import eu.socialedge.hermes.backend.schedule.domain.Schedule;
import eu.socialedge.hermes.backend.schedule.domain.Stop;
import eu.socialedge.hermes.backend.schedule.domain.Trip;
import eu.socialedge.hermes.backend.transit.domain.infra.Station;
import eu.socialedge.hermes.backend.transit.domain.service.Line;

public class SchedulePdfGenerator {
    private static final String TEMPLATES_FOLDER = "templates";

    static {
        Velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        Velocity.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        Velocity.init();
    }

    private final String apiToken;
    private final String url;
    private final Template template;

    public SchedulePdfGenerator(String apiToken, String url, String templateName) {
        this.apiToken = apiToken;
        this.url = url;
        template = getTemplate(templateName);
    }

    public byte[] generate(Schedule schedule) {
        List<byte[]> inboundStationSchedules = new ArrayList<>();
        List<Station> inboundStations = schedule.getLine().getInboundRoute().getStations();
        for (Station station : inboundStations) {
            StationScheduleTemplateDto dto = generateScheduleDto(schedule.getInboundTrips(),
                inboundStations, station, schedule.getLine(), schedule.getAvailability());
            byte[] stationPdf = convertToPdf(entityToTemplateString(dto));
            inboundStationSchedules.add(stationPdf);
        }

        List<byte[]> outboundStationSchedules = new ArrayList<>();
        List<Station> outboundStations = schedule.getLine().getOutboundRoute().getStations();
        for (Station station : outboundStations) {
            StationScheduleTemplateDto dto = generateScheduleDto(schedule.getOutboundTrips(),
                outboundStations, station, schedule.getLine(), schedule.getAvailability());
            byte[] stationPdf = convertToPdf(entityToTemplateString(dto));
            outboundStationSchedules.add(stationPdf);
        }

        return packToZip(inboundStationSchedules, outboundStationSchedules);
    }

    private StationScheduleTemplateDto generateScheduleDto(List<Trip> trips, List<Station> stations, Station station,
            Line line, Availability availability) {

        String lineId = line.getName();
        String vehicleType = line.getVehicleType().name();
        String firstStation = stations.get(0).getName();
        String currentStation = station.getName();
        String availabilityString = availability.getAvailabilityDays().stream().map(Enum::toString).collect(Collectors.joining(", "));
        String startDateString = availability.getStartDate().format(DateTimeFormatter.ofPattern("dd.LLLL.yyyy"));
        List<String> followingStations = stations.subList(stations.indexOf(station), stations.size() - 1).stream()
            .map(Station::getName).collect(Collectors.toList());
        Map<Integer, Set<Integer>> times = getStationStops(trips, station).stream()
            .map(Stop::getArrival)
            .collect(Collectors.groupingBy(LocalTime::getHour, TreeMap::new, Collectors.mapping(LocalTime::getMinute, Collectors.toSet())));

        return new StationScheduleTemplateDto(lineId, vehicleType, followingStations, firstStation, currentStation,
            availabilityString, times, startDateString);
    }

    private byte[] packToZip(List<byte[]> inboundDirection, List<byte[]> outboundDirection) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(bos)) {

            for (int i = 0; i < inboundDirection.size(); i++) {
                zos.putNextEntry(new ZipEntry("inbound/name" + i + ".pdf"));
                zos.write(inboundDirection.get(i));
                zos.closeEntry();
            }
            for (int i = 0; i < outboundDirection.size(); i++) {
                zos.putNextEntry(new ZipEntry("outbound/name" + i + ".pdf"));
                zos.write(outboundDirection.get(i));
                zos.closeEntry();
            }
            zos.finish();

            return bos.toByteArray();
        }
        catch (IOException ioe) {
            throw new RuntimeException("Exception while zip packaging", ioe);
        }
    }

    private byte[] convertToPdf(String entityString) {
        entityString = entityString.replaceAll("\"", "\\\\\"");
        entityString = String.format("{\"html\": \"%s\"}", entityString).replaceAll("[\n|\t|\r]", "");
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, entityString);
        Request request = new Request.Builder()
            .url(url)
            .post(body)
            .addHeader("x-access-token", apiToken)
            .addHeader("content-type", "application/json")
            .addHeader("cache-control", "no-cache")
            .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().bytes();
            } else {
                throw new PdfGenerationException("Pdf generation failed: " + response.body().string());
            }
        } catch (IOException e) {
            throw new PdfGenerationException("Pdf generation failed:", e);
        }
    }

    private static List<Stop> getStationStops(List<Trip> trips, Station station) {
        return trips.stream().flatMap(trip -> trip.getStops().stream())
            .filter(stop -> stop.getStation().equals(station)).collect(Collectors.toList());
    }

    private String entityToTemplateString(StationScheduleTemplateDto entity) {
        StringWriter writer = new StringWriter();
        VelocityContext context = new VelocityContext();
        context.put("entity", entity);
        template.merge(context, writer);
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
}
