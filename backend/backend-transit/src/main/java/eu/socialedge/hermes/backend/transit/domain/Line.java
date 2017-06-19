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
package eu.socialedge.hermes.backend.transit.domain;

import lombok.*;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.UUID;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * Lines represents a group of {@link Route}s that are displayed
 * to riders as a single service.
 */
@Document
@Getter
@ToString
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
public class Line {

    private static final String DEFAULT_NAME_FORMAT = "%s (%s-%s)";

    @Id
    private final String id;

    private final @NotBlank String code;

    private @NotBlank String name;

    @Setter
    private String description;

    @DBRef
    private @NotNull Agency agency;

    @DBRef
    private @NotNull Route inboundRoute;

    @DBRef
    private @NotNull Route outboundRoute;

    @Setter
    private URL url;

    public Line(String id, String code, String name, String description, Route inboundRoute, Route outboundRoute, Agency agency, URL url) {
        this.id = notBlank(id);
        this.code = notBlank(code);
        this.name = notBlank(name);
        this.description = description;
        this.inboundRoute = notNull(inboundRoute);
        this.outboundRoute = notNull(outboundRoute);
        this.agency = notNull(agency);
        this.url = url;
    }

    public Line(String code, String name, Route inboundRoute, Route outboundRoute, Agency agency) {
        this(UUID.randomUUID().toString(), code, name, null, inboundRoute, outboundRoute, agency, null);
    }

    public void setName(String name) {
        this.name = notBlank(name);
    }

    public void setAgency(Agency agency) {
        this.agency = notNull(agency);
    }

    public void setInboundRoute(Route inboundRoute) {
        this.inboundRoute = notNull(inboundRoute);
    }

    public void setOutboundRoute(Route outboundRoute) {
        this.outboundRoute = notNull(outboundRoute);
    }
}
