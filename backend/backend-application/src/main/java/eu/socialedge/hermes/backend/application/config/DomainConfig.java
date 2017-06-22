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

import eu.socialedge.hermes.backend.schedule.infrasturcture.config.ScheduleConfiguration;
import eu.socialedge.hermes.backend.transit.domain.DistanceAwareSegmentFactory;
import eu.socialedge.hermes.backend.transit.domain.GMapsTravelDistanceMeter;
import eu.socialedge.hermes.backend.transit.domain.TravelDistanceMeter;
import eu.socialedge.hermes.backend.transit.domain.repository.handlers.RouteSegmentLengthHook;
import eu.socialedge.hermes.backend.transit.infrastructire.config.TransitConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({TransitConfiguration.class, ScheduleConfiguration.class})
public class DomainConfig {

    @Bean
    @Value("${ext.google-maps.api}")
    public TravelDistanceMeter travelDistanceMeter(String apiKey) {
        return new GMapsTravelDistanceMeter(apiKey);
    }

    @Bean
    public DistanceAwareSegmentFactory segmentFactory(TravelDistanceMeter travelDistanceMeter) {
        return new DistanceAwareSegmentFactory(travelDistanceMeter);
    }

    @Bean
    public RouteSegmentLengthHook routeSegmentLengthHook(DistanceAwareSegmentFactory distanceAwareSegmentFactory) {
        return new RouteSegmentLengthHook(distanceAwareSegmentFactory);
    }
}
