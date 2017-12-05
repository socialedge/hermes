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

import eu.socialedge.hermes.backend.gen.PdfGenerationService;
import eu.socialedge.hermes.backend.gen.SchedulePdfGenerator;
import eu.socialedge.hermes.backend.gen.serialization.ScheduleSerializer;
import eu.socialedge.hermes.backend.gen.serialization.velocity.VelocityScheduleSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PdfGenerationConfig {

    @Bean
    public PdfGenerationService getPdfGenerator(@Value("${ext.restpack.apiToken}") String apiToken,
                                                @Value("${ext.restpack.url}") String url) {
        return new PdfGenerationService(apiToken, url);
    }

    @Bean
    public ScheduleSerializer getScheduleSerializer(@Value("${gen.templates.schedule}") String templateName) {
        return new VelocityScheduleSerializer(templateName);
    }

    @Bean
    public SchedulePdfGenerator getSchedulePdfGenerator(PdfGenerationService pdfGenerationService, ScheduleSerializer scheduleSerializer) {
        return new SchedulePdfGenerator(pdfGenerationService, scheduleSerializer);
    }
}
