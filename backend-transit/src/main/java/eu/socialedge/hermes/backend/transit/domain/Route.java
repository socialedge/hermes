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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * Transit Routes define {@link Station} waypoints for a journey
 * taken by a vehicle along a transit line.
 */
@ToString
@Accessors(fluent = true)
@Entity @Access(AccessType.FIELD)
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
public class Route extends Identifiable<Long> {

    @Getter
    @Column(name = "code", nullable = false)
    private @NotBlank String code;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type")
    private @NotNull VehicleType vehicleType;

    @Getter @Setter
    @ManyToOne
    @JoinColumn(name = "shape_id")
    private Shape shape;

    @ManyToMany
    @JoinTable(name = "route_station",
        joinColumns = @JoinColumn(name = "route_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "station_id", referencedColumnName = "id"))
    @OrderColumn
    private List<Station> stations = new ArrayList<>();

    public Route(String code, VehicleType vehicleType, List<Station> stations, Shape shape) {
        this.code = notBlank(code);
        this.vehicleType = notNull(vehicleType);

        if (nonNull(stations))
            this.stations = new ArrayList<>(stations);

        if (!containsAllStations(shape))
            throw new IllegalArgumentException("Shape must contain locations for all stops in trip");

        this.shape = shape;
    }

    public void code(String code) {
        this.code = notBlank(code);
    }

    public void vehicleType(VehicleType vehicleType) {
        this.vehicleType = notNull(vehicleType);
    }

    public boolean addStation(Station station) {
        if (stations.contains(station))
            return false;

        return stations.add(station);
    }

    public boolean addStation(Station station, int index) {
        if (stations.contains(station))
            return false;

        stations.add(index, station);
        return true;
    }

    public void removeStation(Station station) {
        stations.remove(station);
    }

    public List<Station> stations() {
        return Collections.unmodifiableList(stations);
    }

    /**
     * Validates if all waypoints of this route are plotted
     * on the given shape
     *
     * @param shape a shape to check
     * @return true if all waypoints of this route are plotted on the given shape
     */
    private boolean containsAllStations(Shape shape) {
        if (stations.isEmpty())
            return true;
        val shapeVertices = shape.shapePoints().stream()
            .map(ShapePoint::location)
            .collect(Collectors.toList());

        return stations.stream()
            .map(Station::location)
            .allMatch(shapeVertices::contains);
    }
}
