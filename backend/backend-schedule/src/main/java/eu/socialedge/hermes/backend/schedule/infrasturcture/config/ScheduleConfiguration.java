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

package eu.socialedge.hermes.backend.schedule.infrasturcture.config;

import eu.socialedge.hermes.backend.shared.infrastructure.persistence.MongoFilteringRepositoryFactoryBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@ComponentScan("eu.socialedge.hermes.backend.schedule.domain")
@EnableMongoRepositories(value = "eu.socialedge.hermes.backend.schedule.repository",
    repositoryFactoryBeanClass = MongoFilteringRepositoryFactoryBean.class)
public class ScheduleConfiguration {
}
