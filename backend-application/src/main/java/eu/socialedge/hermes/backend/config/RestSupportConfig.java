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
package eu.socialedge.hermes.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.PropertyAccessor.*;
import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType.HAL;

@Configuration
@EnableHypermediaSupport(type = HAL)
public class RestSupportConfig {

    @Configuration
    static class ContentNegotiationConfig extends WebMvcConfigurerAdapter {

        @Override
        public void configureContentNegotiation(ContentNegotiationConfigurer c) {
            c.defaultContentType(MediaTypes.HAL_JSON);
        }
    }

    @Configuration
    static class RepositoryRestConfig extends RepositoryRestConfigurerAdapter {

        @Override
        public void configureJacksonObjectMapper(ObjectMapper objectMapper) {
            objectMapper.setVisibility(SETTER, NONE);
            objectMapper.setVisibility(GETTER, NONE);
            objectMapper.setVisibility(IS_GETTER, NONE);
            objectMapper.setVisibility(FIELD, ANY);

            objectMapper.setSerializationInclusion(NON_EMPTY);

            objectMapper.enable(INDENT_OUTPUT);
        }
    }
}
