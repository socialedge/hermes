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
package eu.socialedge.hermes.backend.domain;

import eu.socialedge.hermes.backend.domain.ext.Identifiable;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * A stop is a location where vehicles stop to pick up or drop off passengers.
 *
 * TODO: Add support for nesting (stop type Station (gtfs))
 */
@ToString
@Entity @Access(AccessType.FIELD)
@Getter @Setter @Accessors(fluent = true)
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
public class Stop extends Identifiable<Long> {

    @Column(name = "code")
    private String code;

    @Column(name = "name", nullable = false)
    private @NotBlank String name;

    @Column(name = "description")
    private String description;

    @Embedded
    private Location location;

    public Stop(String code, String name, String description, Location location) {
        this.code = code;
        this.name = notBlank(name);
        this.description = description;
        this.location = notNull(location);
    }

    public Stop(String name, Location location) {
        this(null, name, null, location);
    }

    public void name(String name) {
        this.name = notBlank(name);
    }

    public void location(Location location) {
        this.location = notNull(location);
    }
}
