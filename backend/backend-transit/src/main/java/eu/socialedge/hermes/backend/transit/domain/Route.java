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
import org.apache.commons.lang3.Validate;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.Validate.*;

/**
 * Transit Routes define {@link Station} waypoints for a journey
 * taken by a vehicle along a transit line.
 */
@ToString
@Entity @Access(AccessType.FIELD)
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
public class Route extends Identifiable<Long> {

    @Getter
    @Column(name = "code", nullable = false)
    private @NotBlank String code;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false)
    private @NotNull VehicleType vehicleType;

    @Getter
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "shape_id")
    private Shape shape;

    @ManyToMany
    @JoinTable(name = "route_station",
        joinColumns = @JoinColumn(name = "route_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "station_id", referencedColumnName = "id"))
    @OrderColumn
    private @NotEmpty List<Station> stations = new ArrayList<>();

    public Route(String code, VehicleType vehicleType, List<Station> stations) {
        this.code = notBlank(code);
        this.vehicleType = notNull(vehicleType);
        this.stations = new ArrayList<>(notEmpty(stations));
    }

    public Route(String code, VehicleType vehicleType, List<Station> stations, Shape shape) {
        this(code, vehicleType, stations);

        if (!containsAllStations(notNull(shape)))
            throw new IllegalArgumentException("Shape must contain locations for all stops in trip");

        this.shape = shape;
    }

    public void setCode(String code) {
        this.code = notBlank(code);
    }

    public void setVehicleType(VehicleType vehicleType) {
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

    public List<Station> getStations() {
        return Collections.unmodifiableList(stations);
    }

    public void setShape(Shape shape) {
        if (!containsAllStations(notNull(shape)))
            throw new IllegalArgumentException("Shape must contain locations for all stops in trip");

        this.shape = shape;
    }

    public Station headStation() {
        return stations.get(0);
    }

    public Station tailStation() {
        return stations.get(stations.size() - 1);
    }

    /**
     * Validates if all waypoints of this route are plotted
     * on the given shape
     *
     * @param shape a shape to check
     * @return true if all waypoints of this route are plotted on the given shape
     */
    private boolean containsAllStations(Shape shape) {
        Validate.notNull(shape);

        if (stations.isEmpty())
            return true;
        val shapeVertices = shape.getShapePoints().stream()
            .map(ShapePoint::getLocation)
            .collect(Collectors.toList());

        return stations.stream()
            .map(Station::getLocation)
            .allMatch(shapeVertices::contains);
    }
}
