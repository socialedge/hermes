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
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.Validate.notNull;
import static tec.uom.se.unit.Units.METRE;

public class GMapsTravelDistanceMeter implements TravelDistanceMeter {

    /**
     * Google limitation for maximum origins or destinations per request.
     *
     * @see <a href="https://goo.gl/Q7cZRk">
     *     Google Maps Distance Matrix API Usage Limits</a>
     */
    private static final int LOCATIONS_LIMIT = 10;

    private static final TravelMode DEFAULT_TRAVEL_MODE = TravelMode.DRIVING;

    private final GeoApiContext geoApiContext;

    private final TravelMode travelMode;

    /**
     * Creates TravelDistanceMeter with the transportation mode to use for
     * calculating directions and set up GeoApiContext
     * <p>
     * <strong>ACHTUNG!</strong> TRANSIT mode may build different routes depending on
     * departure time, i.e. distance can vary depending on daytime.
     *
     * @param apiKey     Google API key
     * @param travelMode the transportation mode to use for calculating directions
     */
    public GMapsTravelDistanceMeter(String apiKey, TravelMode travelMode) {
        this.geoApiContext = new GeoApiContext().setApiKey(apiKey);
        this.travelMode = notNull(travelMode);
    }

    public GMapsTravelDistanceMeter(String apiKey) {
        this(apiKey, DEFAULT_TRAVEL_MODE);
    }

    @Override
    public Map<Pair<Location, Location>, Quantity<Length>> calculate(List<Location> origins, List<Location> destinations) {
        if (origins.size() != destinations.size())
            throw new IllegalArgumentException("Each origin must have a destination (origins.size() == dests.size())");

        if (origins.isEmpty() || destinations.isEmpty())
            return Collections.emptyMap();

        val distances = new LinkedHashMap<Pair<Location, Location>, Quantity<Length>>();

        val originsChunks = partition(origins, LOCATIONS_LIMIT);
        val destinationChunks = partition(destinations, LOCATIONS_LIMIT);

        for (int i = 0; i < originsChunks.size(); i++) {
            val originsChunk = originsChunks.get(i);
            val destinationChunk = destinationChunks.get(i);

            distances.putAll(calculatePathDistances(originsChunk, destinationChunk));
        }

        return distances;
    }

    private Map<Pair<Location, Location>, Quantity<Length>> calculatePathDistances(List<Location> origins, List<Location> destinations) {
        val pathDistances = new LinkedHashMap<Pair<Location, Location>, Quantity<Length>>();

        val pathDistanceMatrix = calculateDistanceMatrix(locationsToLatLngs(origins), locationsToLatLngs(destinations));
        for (int i = 0; i < pathDistanceMatrix.rows.length; i++) {
            val originDistanceMatrix = pathDistanceMatrix.rows[i];
            val originDiagonalDestDistanceElement = originDistanceMatrix.elements[i];

            if (originDiagonalDestDistanceElement.status != DistanceMatrixElementStatus.OK)
                throw new TravelDistanceMeterException("Couldn't recognize location "
                    + originDiagonalDestDistanceElement.toString());

            val originDestDistanceRaw = originDiagonalDestDistanceElement.distance.inMeters;

            val originLatLng = origins.get(i);
            val destLatLng = destinations.get(i);
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
                .mode(travelMode)
                .await();
        } catch (Exception e) {
            throw new ShapeFactoryException("Exception occurred during distance matrix calculation", e);
        }
    }

    private static LatLng locationToLatLng(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    private static LatLng[] locationsToLatLngs(List<Location> locations) {
        return locations.stream().map(GMapsTravelDistanceMeter::locationToLatLng).toArray(LatLng[]::new);
    }

    private static <T> List<List<T>> partition(List<T> list, int partitionSize) {
        if (partitionSize >= list.size()) {
            return new ArrayList<List<T>>() {{
                add(list);
            }};
        }

        val partitionId = new AtomicInteger();
        val itemsInPartitionLeft = new AtomicInteger(partitionSize);

        return new ArrayList<>(list.stream().collect(groupingBy(el -> {
            if (itemsInPartitionLeft.decrementAndGet() > 0)
                return partitionId.get();

            itemsInPartitionLeft.set(partitionSize);
            return partitionId.getAndIncrement();
        }, LinkedHashMap::new, toList())).values());
    }
}
