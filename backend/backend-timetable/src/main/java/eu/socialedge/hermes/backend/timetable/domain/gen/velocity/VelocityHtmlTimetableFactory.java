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
package eu.socialedge.hermes.backend.timetable.domain.gen.velocity;

import eu.socialedge.hermes.backend.schedule.domain.Schedule;
import eu.socialedge.hermes.backend.timetable.domain.Document;
import eu.socialedge.hermes.backend.timetable.domain.gen.TimetableCreationException;
import eu.socialedge.hermes.backend.timetable.domain.gen.TimetableFactory;
import eu.socialedge.hermes.backend.transit.domain.infra.Station;
import eu.socialedge.hermes.backend.transit.domain.service.Line;
import lombok.val;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class VelocityHtmlTimetableFactory implements TimetableFactory {

    private static final String TEMPLATES_FOLDER = "templates";
    private static final String TIMETABLE_CHARSET = "UTF-8";

    private static final Document.Type OUTPUT_TIPE = Document.Type.HTML;

    static {
        Velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        Velocity.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        Velocity.init();
    }

    private final Template template;

    public VelocityHtmlTimetableFactory(String template) {
        this.template = getTemplate(template);
    }

    @Override
    public Document create(Line line, Station station, Iterable<Schedule> schedules) {
        try {
            val templateData = StationScheduleTemplate.from(line, station, schedules);

            val docContent = merge(template, templateData);

            return new Document(station.getName(), docContent, OUTPUT_TIPE);
        } catch (IOException e) {
            throw new TimetableCreationException("Failed to generate Timetable from Schedules given");
        }
    }

    private static byte[] merge(Template template, StationScheduleTemplate data) throws IOException {
        try (val baos = new ByteArrayOutputStream()) {
            val writer = new BufferedWriter(new OutputStreamWriter(baos, TIMETABLE_CHARSET));

            val context = new VelocityContext();
            context.put("entity", data);
            template.merge(context, writer);

            writer.flush();
            return baos.toByteArray();
        }
    }

    private static Template getTemplate(String templateName) {
        try {
            return Velocity.getTemplate("/" + TEMPLATES_FOLDER + "/" + templateName, TIMETABLE_CHARSET);
        } catch (ResourceNotFoundException rnfe) {
            throw new TimetableCreationException("Template '" + templateName + "' not found", rnfe);
        } catch (ParseErrorException pee) {
            throw new TimetableCreationException("Template '" + templateName + "' is not parsable", pee);
        }
    }
}
