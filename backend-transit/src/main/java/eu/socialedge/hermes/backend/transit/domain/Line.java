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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.nonNull;
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

    private static final String DEFAULT_NAME_FORMAT = "%s (%s-%s)";

    @Getter
    @Column(name = "code", nullable = false)
    private final @NotBlank String code;

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
    private final @NotNull Station origin;

    @OneToMany
    private final @NotNull Station destination;

    @OneToMany
    @JoinColumn(name = "line_id")
    private Set<Route> routes = new HashSet<>();

    @Getter @Setter
    @Column(name = "url")
    private URL url;

    public Line(String code, String name, String description, Station origin, Station destination,
                Agency agency, Set<Route> routes, URL url) {
        this.code = notBlank(code);
        this.name = notBlank(name);
        this.description = description;
        this.origin = notNull(origin);
        this.destination = notNull(destination);
        this.agency = notNull(agency);
        this.url = url;

        if (nonNull(routes)) {
            if (!hasGivenEndStations(routes, origin, destination))
                throw new IllegalArgumentException("Route end station must coincide with given");

            this.routes = new HashSet<>(routes);
        }
    }

    public Line(String code, Station origin, Station destination, Agency agency, Set<Route> routes) {
        this(code, defaultName(code, origin, destination), null, origin, destination, agency, routes, null);
    }

    public Line(String code, Station origin, Station destination, Agency agency) {
        this(code, origin, destination, agency, null);
    }

    public void name(String name) {
        this.name = notBlank(name);
    }

    public void agency(Agency agency) {
        this.agency = notNull(agency);
    }

    public boolean addRoute(Route route) {
        return hasGivenEndStations(route, origin, destination) && routes.add(route);
    }

    public void removeRoute(Route route) {
        routes.remove(route);
    }

    public Set<Route> routes() {
        return Collections.unmodifiableSet(routes);
    }

    private static boolean hasGivenEndStations(Route route, Station origin, Station destination) {
        val routeStations = route.stations();

        val routeOrigin = routeStations.get(0);
        val routeDestination = routeStations.get(routeStations.size() - 1);

        return origin.equals(routeOrigin) && destination.equals(routeDestination);
    }

    private static <T extends Collection<Route>> boolean hasGivenEndStations(T routes, Station origin, Station destination) {
        return routes.stream().allMatch(r -> hasGivenEndStations(r, origin, destination));
    }

    private static String defaultName(String code, Station origin, Station destination) {
        return String.format(DEFAULT_NAME_FORMAT, code, origin.name(), destination.name());
    }
}
