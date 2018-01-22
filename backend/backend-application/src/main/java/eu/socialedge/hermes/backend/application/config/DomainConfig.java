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
package eu.socialedge.hermes.backend.application.config;

import eu.socialedge.hermes.backend.schedule.domain.gen.DefaultStopFactory;
import eu.socialedge.hermes.backend.schedule.domain.gen.DwellTimeResolver;
import eu.socialedge.hermes.backend.schedule.domain.gen.DefaultDwellTimeResolver;
import eu.socialedge.hermes.backend.schedule.domain.gen.StopFactory;
import eu.socialedge.hermes.backend.schedule.infrasturcture.config.ScheduleConfiguration;
import eu.socialedge.hermes.backend.transit.infrastructure.config.TransitConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({TransitConfiguration.class, ScheduleConfiguration.class})
public class DomainConfig {

    @Bean
    DwellTimeResolver dwellTimeResolver() {
        return new DefaultDwellTimeResolver();
    }

    @Bean
    StopFactory stopFactory() {
        return new DefaultStopFactory();
    }
}
