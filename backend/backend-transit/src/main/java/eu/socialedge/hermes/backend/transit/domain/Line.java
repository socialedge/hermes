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

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * Lines represents a group of {@link Route}s that are displayed
 * to riders as a single service.
 */
@Document
@ToString
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
public class Line {

    @Id @Getter
    private final String id;

    @Getter
    private @NotBlank String name;

    @Getter @Setter
    private String description;

    @Getter
    private @NotNull VehicleType vehicleType;

    @DBRef @Getter
    private @NotNull Agency agency;

    @Getter
    private @NotNull Route inboundRoute;

    @Getter
    private @NotNull Route outboundRoute;

    @Getter @Setter
    private URL url;

    public Line(String id, String name, String description, VehicleType vehicleType, Route inboundRoute, Route outboundRoute, Agency agency, URL url) {
        this.id = defaultIfBlank(id, UUID.randomUUID().toString());
        this.name = notBlank(name);
        this.description = description;
        this.vehicleType = notNull(vehicleType);
        this.inboundRoute = notNull(inboundRoute);
        this.outboundRoute = notNull(outboundRoute);
        this.agency = notNull(agency);
        this.url = url;
    }

    public Line(String name, VehicleType vehicleType, Route inboundRoute, Route outboundRoute, Agency agency) {
        this(null, name, null, vehicleType, inboundRoute, outboundRoute, agency, null);
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

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = notNull(vehicleType);
    }
}
