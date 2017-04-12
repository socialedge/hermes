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

import eu.socialedge.hermes.backend.transit.domain.ext.Identifiable;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.Collection;
import java.util.Set;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * Lines represents a group of {@link Route}s that are displayed
 * to riders as a single service.
 */
@Getter
@ToString
@Accessors(fluent = true)
@Entity @Access(AccessType.FIELD)
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
public class Line extends Identifiable<Long>  {

    private static final String DEFAULT_NAME_FORMAT = "%s (%s-%s)";

    @Column(name = "code", nullable = false)
    private final @NotBlank String code;

    @Column(name = "name")
    private @NotBlank String name;

    @Setter
    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "agency_id")
    private @NotNull Agency agency;

    @ManyToOne
    @JoinColumn(name = "inbound_route_id")
    private @NotNull Route inboundRoute;

    @ManyToOne
    @JoinColumn(name = "outbound_route_id")
    private @NotNull Route outboundRoute;

    @Setter
    @Column(name = "url")
    private URL url;

    public Line(String code, String name, String description, Route inboundRoute, Route outboundRoute, Agency agency, URL url) {
        this.code = notBlank(code);
        this.name = notBlank(name);
        this.description = description;
        this.inboundRoute = notNull(inboundRoute);
        this.outboundRoute = notNull(outboundRoute);
        this.agency = notNull(agency);
        this.url = url;
    }

    public Line(String code, String name, Route inboundRoute, Route outboundRoute, Agency agency) {
        this(code, name, null, inboundRoute, outboundRoute, agency, null);
    }

    public void name(String name) {
        this.name = notBlank(name);
    }

    public void agency(Agency agency) {
        this.agency = notNull(agency);
    }

    public void inboundRoute(Route inboundRoute) {
        this.inboundRoute = notNull(inboundRoute);
    }

    public void outboundRoute(Route outboundRoute) {
        this.inboundRoute = notNull(outboundRoute);
    }
}
