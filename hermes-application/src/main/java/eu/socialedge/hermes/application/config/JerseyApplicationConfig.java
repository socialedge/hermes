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
package eu.socialedge.hermes.application.config;

import eu.socialedge.hermes.application.filter.CORSFilter;
import eu.socialedge.hermes.application.provider.gson.GsonBodyConverter;
import eu.socialedge.hermes.application.provider.gson.serializer.EmailJsonSerializer;
import eu.socialedge.hermes.application.provider.gson.serializer.EntityCodeJsonSerializer;
import eu.socialedge.hermes.application.provider.gson.serializer.PhoneJsonSerializer;
import eu.socialedge.hermes.application.provider.gson.serializer.TripJsonSerializer;
import eu.socialedge.hermes.application.provider.gson.serializer.ZoneOffsetJsonSerializer;
import eu.socialedge.hermes.domain.operator.Email;
import eu.socialedge.hermes.domain.operator.Phone;
import eu.socialedge.hermes.domain.shared.Identifier;
import eu.socialedge.hermes.domain.timetable.Trip;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

import java.time.ZoneOffset;

import javax.ws.rs.ApplicationPath;

@Configuration
@ApplicationPath("/api")
public class JerseyApplicationConfig extends ResourceConfig {

    private static final String JSON_JS_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public JerseyApplicationConfig() {
        packages("eu.socialedge.hermes.application.resource");

        register(gsonBodyConverter());

        register(CORSFilter.class);
    }

    public static GsonBodyConverter gsonBodyConverter() {
        return GsonBodyConverter.build(builder -> {
            builder.registerTypeHierarchyAdapter(Identifier.class, new EntityCodeJsonSerializer());
            builder.registerTypeHierarchyAdapter(ZoneOffset.class, new ZoneOffsetJsonSerializer());
            builder.registerTypeHierarchyAdapter(Email.class, new EmailJsonSerializer());
            builder.registerTypeHierarchyAdapter(Phone.class, new PhoneJsonSerializer());
            builder.registerTypeHierarchyAdapter(Trip.class, new TripJsonSerializer());
            builder.enableComplexMapKeySerialization();
            builder.setDateFormat(JSON_JS_DATE_FORMAT);
            builder.setPrettyPrinting();
        });
    }
}