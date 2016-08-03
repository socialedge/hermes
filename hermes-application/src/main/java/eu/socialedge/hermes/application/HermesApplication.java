/**
 * Hermes - The Municipal Transport Timetable System
 * Copyright (c) 2016 SocialEdge
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
package eu.socialedge.hermes.application;

import eu.socialedge.hermes.application.config.JerseyApplicationConfig;
import eu.socialedge.hermes.infrastructure.persistence.jpa.config.JpaInfrastructureConfig;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Configuration @Import({JpaInfrastructureConfig.class, JerseyApplicationConfig.class})
public class HermesApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(HermesApplication.class)
                .run(args);
    }
}
