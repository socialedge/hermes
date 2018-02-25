/*
 * Hermes - The Municipal Transport Timetable System
 * Copyright (c) 2016-2017 SocialEdge
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
package eu.socialedge.hermes.backend.transit.domain.service;

import eu.socialedge.hermes.backend.transit.domain.VehicleType;
import eu.socialedge.hermes.backend.transit.domain.provider.Agency;
import lombok.*;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * Lines represents a group of {@link Route}s that are displayed
 * to riders as a single service.
 *
 * @see <a href="https://goo.gl/mVBa95">
 *     Google Static Transit (GTFS) - route.txt File</a>
 */
@Document
@ToString @EqualsAndHashCode(of = "id")
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
public class Line {

    @Id
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
    private Route outboundRoute;

    @Getter @Setter
    private URL url;

    public Line(String id, String name, String description, VehicleType vehicleType,
                Route inboundRoute, Route outboundRoute, Agency agency, URL url) {
        this.id = isNotBlank(id) ? id : UUID.randomUUID().toString();
        this.name = notBlank(name);
        this.description = description;
        this.vehicleType = notNull(vehicleType);
        this.inboundRoute = notNull(inboundRoute);
        this.outboundRoute = outboundRoute;
        this.agency = notNull(agency);
        this.url = url;
    }

    public Line(String id, String name, String description, VehicleType vehicleType,
                Route loopRoute, Agency agency, URL url) {
        this(id, name, description, vehicleType, loopRoute, null, agency, url);
    }

    public Line(String name, VehicleType vehicleType, Route inboundRoute, Route outboundRoute, Agency agency) {
        this(null, name, null, vehicleType, inboundRoute, outboundRoute, agency, null);
    }

    public Line(String name, VehicleType vehicleType, Route loopRoute, Agency agency) {
        this(name, vehicleType, loopRoute, null, agency);
    }

    private Line(Builder builder) {
        this(builder.id, builder.name, builder.description, builder.vehicleType, builder.inboundRoute,
            builder.outboundRoute, builder.agency, builder.url);
    }

    public String getId() {
        return id;
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

    public boolean isBidirectionalLine() {
        return this.inboundRoute != null && this.outboundRoute != null;
    }

    public boolean isLoopLine() {
        return !isBidirectionalLine();
    }

    public static final class Builder {

        private String id;

        private String name;

        private String description;

        private VehicleType vehicleType;

        private Agency agency;

        private Route inboundRoute;

        private Route outboundRoute;

        private URL url;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder vehicleType(VehicleType vehicleType) {
            this.vehicleType = vehicleType;
            return this;
        }

        public Builder vehicleType(String vehicleType) {
            this.vehicleType = VehicleType.fromNameOrOther(vehicleType);
            return this;
        }

        public Builder agency(Agency agency) {
            this.agency = agency;
            return this;
        }

        public Builder inboundRoute(Route inboundRoute) {
            this.inboundRoute = inboundRoute;
            return this;
        }

        public Builder outboundRoute(Route outboundRoute) {
            this.outboundRoute = outboundRoute;
            return this;
        }

        public Builder loopRoute(Route loopRoute) {
            this.inboundRoute = loopRoute;
            this.outboundRoute = null;
            return this;
        }

        public Builder url(URL url) {
            this.url = url;
            return this;
        }

        public Builder url(String url) throws MalformedURLException {
            if (!isBlank(url))
                this.url = new URL(url);

            return this;
        }

        public Line build() {
            return new Line(this);
        }
    }
}
