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

import eu.socialedge.hermes.application.filter.CORSFilter;
import eu.socialedge.hermes.application.provider.serialization.gson.GsonBodyConverter;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.ws.rs.ApplicationPath;

@Configuration
@ApplicationPath("/api")
@ComponentScan("eu.socialedge.hermes")
public class JerseyApplicationConfig extends ResourceConfig {

    public JerseyApplicationConfig() {
        packages("eu.socialedge.hermes.application.domain");

        register(gsonBodyConverter());
        packages("eu.socialedge.hermes.application.provider.mapping.exception");

        register(CORSFilter.class);
    }

    public static GsonBodyConverter gsonBodyConverter() {
        return GsonBodyConverter.build(builder -> {
            builder.enableComplexMapKeySerialization();
            builder.setPrettyPrinting();
        });
    }
}