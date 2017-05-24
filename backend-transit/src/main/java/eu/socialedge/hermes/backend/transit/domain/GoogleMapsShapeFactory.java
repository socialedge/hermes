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
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.Validate.notEmpty;
import static tec.uom.se.unit.Units.METRE;

public class GoogleMapsShapeFactory implements ShapeFactory {

    /*
    TravelMode.DRIVING is used to make sure only roads will be used.
    TRANSIT mode may build different routes depending on departure time,
    therefore it isn't used.
     */
    private static final TravelMode TRAVEL_MODE = TravelMode.DRIVING;

    private static final int LOCATIONS_LIMIT = 10;

    private final GeoApiContext geoApiContext;

    public GoogleMapsShapeFactory(String apiKey) {
        this.geoApiContext = new GeoApiContext().setApiKey(apiKey);
    }

    @Override
    public Shape create(List<Location> waypoints) {
        val waypointsLatLng = notEmpty(waypoints).stream()
            .map(l -> new LatLng(l.getLatitude(), l.getLongitude()))
            .toArray(LatLng[]::new);

        List<ShapePoint> shapePoints = calculatePathDistanceTraveled(waypointsLatLng)
            .entrySet().stream().map(latLngDistTraveled -> {
                val distanceTraveled = latLngDistTraveled.getValue();
                val googleLatLng = latLngDistTraveled.getKey();
                val location = new Location(googleLatLng.lat, googleLatLng.lng);

                return new ShapePoint(location, distanceTraveled);
            }).collect(toList());

        return new Shape(shapePoints);
    }

    private Map<LatLng, Quantity<Length>> calculatePathDistanceTraveled(LatLng[] waypoints) {
        val pathDistanceTraveled = new LinkedHashMap<LatLng, Quantity<Length>>(waypoints.length);
        Quantity<Length> distTraveled = Quantities.getQuantity(0, METRE);

        // Add origin waypoint of the path
        pathDistanceTraveled.put(waypoints[0], distTraveled);

        for (int wpChunkFromIndex = 0, wpEndIndex = waypoints.length - 1;
             wpChunkFromIndex < wpEndIndex;
             wpChunkFromIndex += LOCATIONS_LIMIT) {

            // wpChunkToIndex is exclusive index
            val wpChunkToIndex = (wpChunkFromIndex + LOCATIONS_LIMIT) > wpEndIndex
                ? waypoints.length : (wpChunkFromIndex + LOCATIONS_LIMIT + 1);

            val wpChunk = Arrays.copyOfRange(waypoints, wpChunkFromIndex, wpChunkToIndex);
            val pathDistances = calculatePathDistances(wpChunk); // wpChunk.length = 11

            for (val origDestDistance : pathDistances.entrySet()) {
                val originDestLatLngPair = origDestDistance.getKey();
                val destLatLng = originDestLatLngPair.getValue();

                val originDestDistance = origDestDistance.getValue();
                distTraveled = distTraveled.add(originDestDistance);

                pathDistanceTraveled.put(destLatLng, distTraveled);
            }
        }

        return pathDistanceTraveled;
    }

    private Map<Pair<LatLng, LatLng>, Quantity<Length>> calculatePathDistances(LatLng[] waypoints) {
        if (waypoints.length == 0)
            return Collections.emptyMap();

        val pathDistances = new LinkedHashMap<Pair<LatLng, LatLng>, Quantity<Length>>();

        val latLngOrigins = Arrays.copyOfRange(waypoints, 0, waypoints.length - 1); // latLngOrigins.length = 10
        val latLngDestinations = Arrays.copyOfRange(waypoints, 1, waypoints.length); // latLngDestinations.length = 10
        val pathDistanceMatrix = calculateDistanceMatrix(latLngOrigins, latLngDestinations);

        for (int i = 0; i < pathDistanceMatrix.rows.length; i++) {
            val originDistanceMatrix = pathDistanceMatrix.rows[i];
            val originDiagonalDestDistanceElement = originDistanceMatrix.elements[i];

            if (originDiagonalDestDistanceElement.status != DistanceMatrixElementStatus.OK)
                throw new ShapeFactoryException("Couldn't recognize location " + latLngOrigins[i]);

            val originDestDistanceRaw = originDiagonalDestDistanceElement.distance.inMeters;

            val originLatLng = latLngOrigins[i];
            val destLatLng = latLngDestinations[i];
            val originDestDistance = Quantities.getQuantity(originDestDistanceRaw, METRE);

            pathDistances.put(Pair.of(originLatLng, destLatLng), originDestDistance);
        }

        return pathDistances;
    }

    private DistanceMatrix calculateDistanceMatrix(LatLng[] origins, LatLng[] destinations) {
        if (origins.length > LOCATIONS_LIMIT)
            throw new IllegalArgumentException("Too many origins LatLng. " +
                "Allowed = " + LOCATIONS_LIMIT + ", actual = " + origins.length);
        else if (destinations.length > LOCATIONS_LIMIT) {
            throw new IllegalArgumentException("Too many destinations LatLng. " +
                "Allowed = " + LOCATIONS_LIMIT + ", actual = " + destinations.length);
        }

        try {
            return DistanceMatrixApi.newRequest(geoApiContext)
                .origins(origins)
                .destinations(destinations)
                .mode(TRAVEL_MODE)
                .await();
        } catch (Exception e) {
            throw new ShapeFactoryException("Exception occurred during distance matrix calculation", e);
        }
    }
}
