package eu.socialedge.hermes.backend.transit.domain;

import lombok.val;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.Validate.notNull;
import static tec.uom.se.unit.Units.METRE;

public class DistanceAwareSegmentFactory {

    private final TravelDistanceMeter distanceMeter;

    public DistanceAwareSegmentFactory(TravelDistanceMeter distanceMeter) {
        this.distanceMeter = notNull(distanceMeter);
    }

    public Segment factory(Station begin, Station end) {
        return factory(begin, end, null);
    }

    public Segment factory(Station begin, Station end, List<Location> waypoints) {
        val segmentWaypoints = new ArrayList<Location>() {{
            add(begin.getLocation());

            if (waypoints != null)
                addAll(waypoints);

            add(end.getLocation());
        }};

        val segmentWaypointsOrigins = segmentWaypoints.subList(0, segmentWaypoints.size() - 1);
        val segmentWaypointsDestins = segmentWaypoints.subList(1, segmentWaypoints.size());
        val segmentDistances = distanceMeter.calculate(segmentWaypointsOrigins, segmentWaypointsDestins);

        val segmentSize = segmentDistances.values().stream()
            .reduce(Quantities.getQuantity(0, METRE), Quantity::add);

        return new Segment(begin, end, segmentSize);
    }
}
