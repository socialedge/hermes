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
package eu.socialedge.hermes.backend.core;

import eu.socialedge.hermes.backend.core.ext.Identifiable;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.net.URL;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * Routes are equivalent to "Lines" in public transportation systems.
 * Routes are made up of one or more Trips — remember that a Trip occurs
 * at a specific time and so a Route is time-independent.
 */
@ToString
@Entity @Access(AccessType.FIELD)
@Getter @Setter @Accessors(fluent = true)
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
public class Route extends Identifiable<Long> {

    @Column(name = "code", nullable = false)
    private @NotBlank String code;

    @Column(name = "name")
    private @NotBlank String name;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type")
    private @NotNull VehicleType vehicleType;

    @ManyToOne
    @JoinColumn(name = "agency_id")
    private Agency agency;

    @Column(name = "url")
    private URL url;

    public Route(String code, String name, String description, VehicleType vehicleType, Agency agency, URL url) {
        this.code = notBlank(code);
        this.name = notBlank(name);
        this.description = description;
        this.vehicleType = notNull(vehicleType);
        this.agency = notNull(agency);
        this.url = url;
    }

    public Route(String code, String name, VehicleType vehicleType, Agency agency) {
        this(code, name, null, vehicleType, agency, null);
    }

    public void code(String code) {
        this.code = notBlank(code);
    }

    public void name(String name) {
        this.name = notBlank(name);
    }

    public void vehicleType(VehicleType vehicleType) {
        this.vehicleType = notNull(vehicleType);
    }
}
