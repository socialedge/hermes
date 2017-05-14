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
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang3.Validate.notEmpty;

@ToString
@Entity @Access(AccessType.FIELD)
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
public class Shape extends Identifiable<Long> {

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "shape_points", joinColumns = @JoinColumn(name = "shape_id"))
    @OrderColumn(name = "shape_points_order")
    private List<ShapePoint> shapePoints;

    public Shape(Collection<ShapePoint> shapePoints) {
        this.shapePoints = new ArrayList<>(notEmpty(shapePoints));
    }

    public boolean addShapePoint(ShapePoint shapePoint) {
        return shapePoints.add(shapePoint);
    }

    public boolean removeShapePoint(ShapePoint shapePoint) {
        return shapePoints.remove(shapePoint);
    }

    public List<ShapePoint> getShapePoints() {
        return Collections.unmodifiableList(shapePoints);
    }
}
