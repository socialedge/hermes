package eu.socialedge.hermes.backend.transit.domain;

import lombok.val;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

public class RouteTest {

    private List<Segment> segments = new ArrayList<>();

    @Before
    public void setUp() {
        val station1 = new Station("stop1", new HashSet<>(singletonList(VehicleType.BUS)),
            new Location(1.1, 1.1));
        val station2 = new Station("stop2", new HashSet<>(singletonList(VehicleType.BUS)),
            new Location(1.2, 1.2));
        val station3 = new Station("stop3", new HashSet<>(singletonList(VehicleType.BUS)),
            new Location(1.3, 1.3));
        val station4 = new Station("stop3", new HashSet<>(singletonList(VehicleType.BUS)),
            new Location(1.4, 1.4));

        segments.add(new Segment(station1, station2));
        segments.add(new Segment(station2, station3));
        segments.add(new Segment(station3, station4));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfSegmentsAreNotIntereconnected() {
        segments.add(new Segment(segments.get(0).getBegin(), segments.get(0).getEnd()));
        new Route(segments);
    }

    @Test
    public void shouldCreateRouteWithAllSegmentsInCorrectOrder() {
        val route = new Route(segments);
        val routeSegments = route.stream().collect(toList());

        assertEquals(segments, routeSegments);
    }

    @Test
    public void shouldReturnLastStationAsHead() {
        val route = new Route(segments);

        assertEquals(segments.get(segments.size() - 1).getEnd(), route.getHead());
    }

    @Test
    public void shouldReturnFirstStationAsTail() {
        val route = new Route(segments);

        assertEquals(segments.get(0).getBegin(), route.getTail());
    }
}
