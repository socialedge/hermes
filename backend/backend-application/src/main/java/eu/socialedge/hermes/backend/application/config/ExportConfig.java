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

import eu.socialedge.hermes.backend.export.Dummy;
import eu.socialedge.hermes.backend.export.PdfExporter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExportConfig {

    @Bean
    public PdfExporter<Dummy> getDummyPdfExporter(@Value("${ext.restpack.apiToken}") String apiToken,
                                           @Value("${ext.restpack.url}") String url,
                                           @Value("${export.templates.schedule}") String templateName) {
        return new PdfExporter<>(apiToken, url, templateName);
    }
}
