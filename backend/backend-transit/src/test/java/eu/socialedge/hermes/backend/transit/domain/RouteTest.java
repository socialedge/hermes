package eu.socialedge.hermes.backend.transit.domain;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import tec.uom.se.quantity.Quantities;
import tec.uom.se.unit.Units;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;

public class RouteTest {

    private List<Station> stations = new ArrayList<>();

    @Before
    public void setUp() {
        stations.add(new Station("stop1", new HashSet<>(singletonList(VehicleType.BUS)),
            new Location(1.1, 1.1)));

        stations.add(new Station("stop2", new HashSet<>(singletonList(VehicleType.BUS)),
            new Location(1.2, 1.2)));

        stations.add(new Station("stop3", new HashSet<>(singletonList(VehicleType.BUS)),
            new Location(1.3, 1.3)));
    }

    @Ignore
    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionForShapeWithoutOneOfStops() {
        List<ShapePoint> shapePoints = stations.stream()
            .skip(1)
            .map(Station::getLocation)
            .map(location -> new ShapePoint(location, Quantities.getQuantity(1, Units.METRE)))
            .collect(Collectors.toList());


        new Route("code", VehicleType.BUS, stations, new Shape(shapePoints));
    }

    @Test
    public void shouldCreateNewTripWithoutExceptionsIfShapeMatchesWithStops() {
        List<ShapePoint> shapePoints = stations.stream()
            .map(Station::getLocation)
            .map(location -> new ShapePoint(location, Quantities.getQuantity(1, Units.METRE)))
            .collect(Collectors.toList());

        new Route("code", VehicleType.BUS, stations, new Shape(shapePoints));
    }
}
