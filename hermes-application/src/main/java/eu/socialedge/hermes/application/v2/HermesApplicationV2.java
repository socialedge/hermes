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
package eu.socialedge.hermes.application.v2;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"eu.socialedge.hermes.application.v2",
                              "eu.socialedge.hermes.infrastructure.persistence.v2"})
@EnableJpaRepositories("eu.socialedge.hermes.infrastructure.persistence.v2.jpa.repository.entity")

public class HermesApplicationV2 {
    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(HermesApplicationV2.class)
                .run(args);
    }
}
