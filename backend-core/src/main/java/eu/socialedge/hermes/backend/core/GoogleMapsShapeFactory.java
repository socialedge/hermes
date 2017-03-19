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

import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixElementStatus;
import com.google.maps.model.LatLng;
import lombok.val;
import tec.uom.se.quantity.Quantities;
import tec.uom.se.unit.Units;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.Validate.notEmpty;

public class GoogleMapsShapeFactory implements ShapeFactory {

    private final GeoApiContext geoApiContext;

    public GoogleMapsShapeFactory(String apiKey) {
        this.geoApiContext = new GeoApiContext().setApiKey(apiKey);
    }

    @Override
    public Shape create(List<Location> waypoints) {
        val distanceMatrix = calculateDistanceMatrix(notEmpty(waypoints).get(0), waypoints);
        val distanceElements = distanceMatrix.rows[0].elements;

        val shapePoints = new ArrayList<ShapePoint>(waypoints.size());
        for (int i = 0; i < distanceElements.length; i++) {
            val element = distanceElements[i];
            val pointLocation = waypoints.get(i);

            if (element.status != DistanceMatrixElementStatus.OK) {
                throw new ShapeFactoryException("Couldn't recognize location " + pointLocation);
            }

            val quantity = Quantities.getQuantity(element.distance.inMeters, Units.METRE);
            shapePoints.add(new ShapePoint(pointLocation, quantity));
        }
        return new Shape(shapePoints);
    }

    private DistanceMatrix calculateDistanceMatrix(Location origin, List<Location> waypoints) {
        val destinations = waypoints.stream()
            .map(GoogleMapsShapeFactory::toLatLng)
            .toArray(LatLng[]::new);

        try {
            return DistanceMatrixApi.newRequest(geoApiContext)
                .origins(toLatLng(origin))
                .destinations(destinations)
                .await();
        } catch (Exception e) {
            throw new ShapeFactoryException("Exception occurred during distance matrix calculation", e);
        }
    }

    private static LatLng toLatLng(Location location) {
        return new LatLng(location.latitude(), location.longitude());
    }
}
