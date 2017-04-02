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

    private static final int LOCATIONS_LIMIT = 10;

    private final GeoApiContext geoApiContext;

    public GoogleMapsShapeFactory(String apiKey) {
        this.geoApiContext = new GeoApiContext().setApiKey(apiKey);
    }

    @Override
    public Shape create(List<Location> waypoints) {
        val shapePoints = new ArrayList<ShapePoint>(notEmpty(waypoints).size());
        shapePoints.add(new ShapePoint(waypoints.get(0), Quantities.getQuantity(0, Units.METRE)));

        int chunkNumber = 0;
        do {
            val endIndex = chunkNumber + LOCATIONS_LIMIT < waypoints.size() ?
                chunkNumber + LOCATIONS_LIMIT : waypoints.size();
            val waypointsChunk = waypoints.subList(chunkNumber, endIndex);
            val distanceMatrix = calculateDistanceMatrix(waypointsChunk);

            for (int i = 0; i < distanceMatrix.rows.length - 1; i++) {
                val element = distanceMatrix.rows[i].elements[i + 1];
                val pointLocation = waypointsChunk.get(i + 1);

                if (element.status != DistanceMatrixElementStatus.OK) {
                    throw new ShapeFactoryException("Couldn't recognize location " + pointLocation);
                }

                val localDistance = Quantities.getQuantity(element.distance.inMeters, Units.METRE);
                val distanceFromOrigin = localDistance.add(shapePoints.get(i).distanceTraveled());
                shapePoints.add(new ShapePoint(pointLocation, distanceFromOrigin));
            }
            chunkNumber += LOCATIONS_LIMIT - 1;
        } while (chunkNumber < waypoints.size() - 2);

        return new Shape(shapePoints);
    }

    private DistanceMatrix calculateDistanceMatrix(List<Location> waypoints) {
        val latLngWaypoints = waypoints.stream()
            .map(GoogleMapsShapeFactory::toLatLng)
            .toArray(LatLng[]::new);

        try {
            return DistanceMatrixApi.newRequest(geoApiContext)
                .origins(latLngWaypoints)
                .destinations(latLngWaypoints)
                .await();
        } catch (Exception e) {
            throw new ShapeFactoryException("Exception occurred during distance matrix calculation", e);
        }
    }

    private static LatLng toLatLng(Location location) {
        return new LatLng(location.latitude(), location.longitude());
    }
}
