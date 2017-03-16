package eu.socialedge.hermes.backend.core;

import lombok.val;
import org.junit.Before;
import org.junit.Test;
import tec.uom.se.unit.Units;

import javax.measure.Quantity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class GoogleMapsShapeFactoryTest {

    private static final String API_KEY = "AIzaSyCluVkO-_MfzcNku1aocMtQp8ua8oUSE0o";

    private final ShapeFactory factory = new GoogleMapsShapeFactory(API_KEY);

    private List<Location> locations;

    @Before
    public void setUp() {
        locations = Arrays.asList(
            new Location(-33.865143, 151.209900),
            new Location(-31.953512, 115.857048),
            new Location(-34.206841, 142.136490));
    }

    @Test
    public void shouldReturnShapeWithCountOfShapePointsEqualToLocationsCount() {
        val result = factory.create(locations);

        assertNotNull(result);
        assertEquals(locations.size(), result.shapePoints().size());
    }

    @Test
    public void shouldReturnShapeWithSameLocationsInSameOrder() {
        val result = factory.create(locations);

        val resultLocations = result.shapePoints().stream()
            .map(ShapePoint::location)
            .collect(Collectors.toList());
        assertEquals(locations, resultLocations);
    }

    @Test
    public void shouldReturnShapeWithDistanceUnitsInMeters() {
        val result = factory.create(locations);

        val allMeters = result.shapePoints().stream()
            .map(ShapePoint::distanceTraveled)
            .map(Quantity::getUnit)
            .allMatch(Units.METRE::equals);
        assertTrue(allMeters);
    }

    @Test
    public void shouldReturnShapeWithCorrectDistances() {
        val distances = Stream.of(0, 3936679, 1014084).collect(Collectors.toList());

        val result = factory.create(locations);

        val resultDistances = result.shapePoints().stream()
            .map(ShapePoint::distanceTraveled)
            .map(Quantity::getValue)
            .map(Number::intValue)
            .collect(Collectors.toList());
        assertEquals(distances, resultDistances);
    }

    @Test
    public void shouldNotFailForInputOf50Locations() {
        locations = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            locations.add(new Location(-34.206841, 142.136490));
        }

        val result = factory.create(locations);

        assertEquals(locations.size(), result.shapePoints().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionForEmptyLocations() {
        factory.create(Collections.emptyList());
    }

    @Test(expected = TransitRuntimeException.class)
    public void shouldThrowExceptionForIncorrectLocation() {
        factory.create(Arrays.asList(new Location(-33.865143, 151.209900), new Location(-34, 25)));
    }
}
