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
package eu.socialedge.hermes.backend.application.config;

import eu.socialedge.hermes.backend.timetable.domain.TimetableGenerationService;
import eu.socialedge.hermes.backend.timetable.domain.convert.DocumentConverter;
import eu.socialedge.hermes.backend.timetable.domain.convert.PdfDocumentConverter;
import eu.socialedge.hermes.backend.timetable.domain.gen.TimetableFactory;
import eu.socialedge.hermes.backend.timetable.domain.gen.velocity.VelocityHtmlTimetableFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class TimetableConfig {

    @Bean
    public DocumentConverter pdfDocumentConverter(@Value("${ext.restpack.apiToken}") String apiToken) {
        return new PdfDocumentConverter(apiToken);
    }

    @Bean
    public TimetableFactory timetableFactory(@Value("${gen.templates.schedule}") String templateName) {
        return new VelocityHtmlTimetableFactory(templateName);
    }

    @Bean
    public TimetableGenerationService timetableGenerationService(TimetableFactory timetableFactory,
                                                                 List<DocumentConverter> documentConverters) {
        return new TimetableGenerationService(timetableFactory, documentConverters);
    }
}