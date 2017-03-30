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

    private Set<StopTime> stopTimes = new HashSet<>();
    private Route route;

    @Before
    public void setUp() {
        route = new Route("code", "name", "description", VehicleType.BUS, new Agency(), null);

        stopTimes.add(new StopTime(LocalTime.now(), LocalTime.MAX,
            new Stop("stop1", new HashSet<>(Arrays.asList(VehicleType.BUS)), new Location(1.1, 1.1))));

        stopTimes.add(new StopTime(LocalTime.now(), LocalTime.MAX,
            new Stop("stop2", new HashSet<>(Arrays.asList(VehicleType.BUS)), new Location(1.2, 1.2))));

        stopTimes.add(new StopTime(LocalTime.now(), LocalTime.MAX,
            new Stop("stop3", new HashSet<>(Arrays.asList(VehicleType.BUS)), new Location(1.3, 1.3))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionForShapeWithoutOneOfStops() {
        List<ShapePoint> shapePoints = stopTimes.stream()
            .skip(1)
            .map(StopTime::stop)
            .map(Stop::location)
            .map(location -> new ShapePoint(location, Quantities.getQuantity(1, Units.METRE)))
            .collect(Collectors.toList());

        new Trip(Direction.INBOUND, route, "1", stopTimes, new Shape(shapePoints));
    }

    @Test
    public void shouldCreateNewTripWithoutExceptionsIfShapeMatchesWithStops() {
        List<ShapePoint> shapePoints = stopTimes.stream()
            .map(StopTime::stop)
            .map(Stop::location)
            .map(location -> new ShapePoint(location, Quantities.getQuantity(1, Units.METRE)))
            .collect(Collectors.toList());

        new Trip(Direction.INBOUND, route, "1", stopTimes, new Shape(shapePoints));
    }
}
