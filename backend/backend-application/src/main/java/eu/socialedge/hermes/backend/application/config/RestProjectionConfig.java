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

import eu.socialedge.hermes.backend.application.api.projection.*;
import eu.socialedge.hermes.backend.schedule.domain.Schedule;
import eu.socialedge.hermes.backend.transit.domain.service.Line;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;

import javax.annotation.PostConstruct;

@Configuration
public class RestProjectionConfig {

    private final RepositoryRestConfiguration repositoryRestConf;

    @Autowired
    public RestProjectionConfig(RepositoryRestConfiguration repositoryRestConf) {
        this.repositoryRestConf = repositoryRestConf;
    }

    @PostConstruct
    void registerRestProjections() {
        val restDataProjections = repositoryRestConf.getProjectionConfiguration();

        restDataProjections.addProjection(RichLineProjection.class, Line.class);
        restDataProjections.addProjection(RichScheduleProjection.class, Schedule.class);
        restDataProjections.addProjection(InboundSchedule.class, Schedule.class);
        restDataProjections.addProjection(OutboundSchedule.class, Schedule.class);
        restDataProjections.addProjection(ScheduleMetadata.class, Schedule.class);
    }
}
