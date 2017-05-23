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
import com.google.maps.model.TravelMode;
import javafx.util.Pair;
import lombok.val;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.Validate.notEmpty;
import static tec.uom.se.unit.Units.METRE;

public class GoogleMapsShapeFactory implements ShapeFactory {

    private static final TravelMode TRAVEL_MODE = TravelMode.TRANSIT;

    private static final int LOCATIONS_LIMIT = 10;

    private final GeoApiContext geoApiContext;

    public GoogleMapsShapeFactory(String apiKey) {
        this.geoApiContext = new GeoApiContext().setApiKey(apiKey);
    }

    @Override
    public Shape create(List<Location> waypoints) {
        val googleReadyWaypoints = notEmpty(waypoints).stream()
            .map(l -> new LatLng(l.getLatitude(), l.getLongitude()))
            .toArray(LatLng[]::new);

        List<ShapePoint> shapePoints = calculateDistanceTraveled(googleReadyWaypoints).stream()
            .map(latLngDistTraveled -> {
                val distanceTraveled = latLngDistTraveled.getValue();
                val googleLatLng = latLngDistTraveled.getKey();
                val location = new Location(googleLatLng.lat, googleLatLng.lng);

                return new ShapePoint(location, distanceTraveled);
            }).collect(toList());

        return new Shape(shapePoints);
    }

    private List<Pair<LatLng, Quantity<Length>>> calculateDistanceTraveled(LatLng[] waypoints) {
        val waypointDistTraveled = new ArrayList<Pair<LatLng, Quantity<Length>>>(waypoints.length);
        Quantity<Length> distTraveled = Quantities.getQuantity(0, METRE);

        // Add origin waypoint of the path
        waypointDistTraveled.add(new Pair<>(waypoints[0], distTraveled));

        for (int wpChunkStartIndex = 0; wpChunkStartIndex < waypoints.length; wpChunkStartIndex += LOCATIONS_LIMIT - 1) {
            val wpChunkEndIndex = (wpChunkStartIndex + LOCATIONS_LIMIT) > waypoints.length
                                        ? waypoints.length : (wpChunkStartIndex + LOCATIONS_LIMIT);

            val wpChunk = Arrays.copyOfRange(waypoints, wpChunkStartIndex, wpChunkEndIndex);

            val wpChunkDistanceMatrix = calculateDistanceMatrix(wpChunk);

            for(int j = 0; j < wpChunk.length - 1; j++) {
                val origDestDistanceMatrix = wpChunkDistanceMatrix.rows[j].elements[j];

                if (origDestDistanceMatrix.status != DistanceMatrixElementStatus.OK)
                    throw new ShapeFactoryException("Couldn't recognize location " + waypoints[j + 1]);

                val origDestDistance = origDestDistanceMatrix.distance.inMeters;
                distTraveled = distTraveled.add(Quantities.getQuantity(origDestDistance, METRE));

                waypointDistTraveled.add(new Pair<>(waypoints[j + 1], distTraveled));
            }
        }

        return waypointDistTraveled;
    }

    private DistanceMatrix calculateDistanceMatrix(LatLng[] waypoints) {
        val latLngOrigins = Arrays.copyOfRange(waypoints, 0, waypoints.length - 1);
        val latLngDestinations = Arrays.copyOfRange(waypoints, 1, waypoints.length);

        try {
            return DistanceMatrixApi.newRequest(geoApiContext)
                .origins(latLngOrigins)
                .destinations(latLngDestinations)
                .mode(TRAVEL_MODE)
                .await();
        } catch (Exception e) {
            throw new ShapeFactoryException("Exception occurred during distance matrix calculation", e);
        }
    }
}
