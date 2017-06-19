/*
* Hermes - The Municipal Transport Timetable System
* Copyright (c) 2017 SocialEdge
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

import com.neovisionaries.i18n.LanguageCode;
import eu.socialedge.hermes.backend.schedule.infrasturcture.config.ScheduleConfiguration;
import eu.socialedge.hermes.backend.transit.domain.Agency;
import eu.socialedge.hermes.backend.transit.domain.GoogleMapsShapeFactory;
import eu.socialedge.hermes.backend.transit.domain.ShapeFactory;
import eu.socialedge.hermes.backend.transit.domain.repository.AgencyRepository;
import eu.socialedge.hermes.backend.transit.infrastructire.config.TransitConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import java.net.URL;
import java.util.TimeZone;

@Configuration
@Import({TransitConfiguration.class, ScheduleConfiguration.class})
public class DomainConfig {

    @Bean
    @Value("${ext.google-maps.api}")
    public ShapeFactory shapeFactory(String apiKey) {
        return new GoogleMapsShapeFactory(apiKey);
    }

    @Deprecated
    @Configuration @Profile("dev")
    public static class InitDevAgency {

        @Autowired
        AgencyRepository agencyRepository;

        @Value("${domain.agency.name}")
        String name;

        @Value("${domain.agency.lang}")
        String lang;

        @Value("${domain.agency.phone}")
        String phone;

        @Value("${domain.agency.timeZone}")
        String timeZone;

        @Value("${domain.agency.url}")
        URL url;

        @PostConstruct
        void init() {
            agencyRepository.save(new Agency(name, LanguageCode.getByCode(lang), phone, TimeZone.getTimeZone(timeZone), url));
        }
    }
}
