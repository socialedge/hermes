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
    public Shape create(List<Location> points) {
        val resultElements = calculateDistanceMatrix(notEmpty(points)).rows[0].elements;

        val shapePoints = new ArrayList<ShapePoint>();
        for (int i = 0; i < resultElements.length; i++) {
            val element = resultElements[i];
            val pointLocation = points.get(i);

            if (element.status != DistanceMatrixElementStatus.OK) {
                throw new TransitRuntimeException("Couldn't recognize location " + pointLocation);
            }

            val quantity = Quantities.getQuantity(element.distance.inMeters, Units.METRE);
            shapePoints.add(new ShapePoint(pointLocation, quantity));
        }

        return new Shape(shapePoints);
    }

    private DistanceMatrix calculateDistanceMatrix(List<Location> points) {
        val origin = toLatLng(points.get(0));
        val destinations = points.stream()
            .map(GoogleMapsShapeFactory::toLatLng)
            .toArray(LatLng[]::new);

        try {
            return DistanceMatrixApi.newRequest(geoApiContext)
                .origins(origin)
                .destinations(destinations)
                .await();
        } catch (Exception e) {
            throw new TransitRuntimeException("Exception occurred during distance matrix calculation", e);
        }
    }

    private static LatLng toLatLng(Location location) {
        return new LatLng(location.latitude(), location.longitude());
    }
}
