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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * Lines represents a group of {@link Route}s that are displayed
 * to riders as a single service.
 */
@ToString
@Accessors(fluent = true)
@Entity @Access(AccessType.FIELD)
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
public class Line extends Identifiable<Long>  {

    @Getter
    @Column(name = "code", nullable = false)
    private @NotBlank String code;

    @Getter
    @Column(name = "name")
    private @NotBlank String name;

    @Getter @Setter
    @Column(name = "description")
    private String description;

    @Getter
    @ManyToOne
    @JoinColumn(name = "agency_id")
    private @NotNull Agency agency;

    @OneToMany
    @JoinColumn(name = "line_id")
    private Set<Route> routes;

    @Getter @Setter
    @Column(name = "url")
    private URL url;

    public Line(String code, String name, String description, Agency agency, Set<Route> routes, URL url) {
        this.code = notBlank(code);
        this.name = notBlank(name);
        this.description = description;
        this.agency = notNull(agency);
        this.routes = isNull(routes) ? new HashSet<>() : new HashSet<>(routes);
        this.url = url;
    }

    public Line(String code, String name, Agency agency, Set<Route> routes) {
        this(code, name, null, agency, routes, null);
    }

    public void code(String code) {
        this.code = notBlank(code);
    }

    public void name(String name) {
        this.name = notBlank(name);
    }

    public void agency(Agency agency) {
        this.agency = notNull(agency);
    }

    public boolean addRoute(Route route) {
        return routes.add(route);
    }

    public void removeRoute(Route route) {
        routes.remove(route);
    }

    public Set<Route> routes() {
        return Collections.unmodifiableSet(routes);
    }
}
