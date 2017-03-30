package eu.socialedge.hermes.backend.transit.domain;

import org.junit.Before;
import org.junit.Test;
import tec.uom.se.quantity.Quantities;
import tec.uom.se.unit.Units;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TripTest {

    private Set<Stop> stops = new HashSet<>();
    private Route route;

    @Before
    public void setUp() {
        route = new Route("code", "name", "description", VehicleType.BUS, new Agency(), null);

        stops.add(new Stop(LocalTime.now(), LocalTime.MAX,
            new Station("stop1", new HashSet<>(Arrays.asList(VehicleType.BUS)), new Location(1.1, 1.1))));

        stops.add(new Stop(LocalTime.now(), LocalTime.MAX,
            new Station("stop2", new HashSet<>(Arrays.asList(VehicleType.BUS)), new Location(1.2, 1.2))));

        stops.add(new Stop(LocalTime.now(), LocalTime.MAX,
            new Station("stop3", new HashSet<>(Arrays.asList(VehicleType.BUS)), new Location(1.3, 1.3))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionForShapeWithoutOneOfStops() {
        List<ShapePoint> shapePoints = stops.stream()
            .skip(1)
            .map(Stop::stop)
            .map(Station::location)
            .map(location -> new ShapePoint(location, Quantities.getQuantity(1, Units.METRE)))
            .collect(Collectors.toList());

        new Trip(Direction.INBOUND, route, "1", stops, new Shape(shapePoints));
    }

    @Test
    public void shouldCreateNewTripWithoutExceptionsIfShapeMatchesWithStops() {
        List<ShapePoint> shapePoints = stops.stream()
            .map(Stop::stop)
            .map(Station::location)
            .map(location -> new ShapePoint(location, Quantities.getQuantity(1, Units.METRE)))
            .collect(Collectors.toList());

        new Trip(Direction.INBOUND, route, "1", stops, new Shape(shapePoints));
    }
}
