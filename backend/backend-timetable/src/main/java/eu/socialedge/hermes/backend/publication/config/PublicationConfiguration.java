/*
 * Hermes - The Municipal Transport Timetable System
 * Copyright (c) 2016-2018 SocialEdge
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
package eu.socialedge.hermes.backend.publication.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import eu.socialedge.hermes.backend.shared.infrastructure.persistence.MongoFilteringRepositoryFactoryBean;

@Configuration
@ComponentScan("eu.socialedge.hermes.backend.timetable.domain")
@EnableMongoRepositories(value = "eu.socialedge.hermes.backend.publication.repository",
    repositoryFactoryBeanClass = MongoFilteringRepositoryFactoryBean.class)
public class PublicationConfiguration {
}
