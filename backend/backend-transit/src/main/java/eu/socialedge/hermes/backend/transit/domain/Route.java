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
import org.apache.commons.lang3.Validate;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * Transit Routes define {@link Station} waypoints for a journey
 * taken by a vehicle along a transit line.
 */
@Document
@ToString @EqualsAndHashCode
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
public class Route {

    @Getter
    private Shape shape;

    @DBRef
    private @NotEmpty List<Station> stations = new ArrayList<>();

    public Route(List<Station> stations) {
        this.stations = new ArrayList<>(notEmpty(stations));
    }

    public Route(List<Station> stations, Shape shape) {
        this.stations = new ArrayList<>(notEmpty(stations));

        if (!containsAllStations(notNull(shape)))
            throw new IllegalArgumentException("Shape must contain locations for all stops in trip");

        this.shape = shape;
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
