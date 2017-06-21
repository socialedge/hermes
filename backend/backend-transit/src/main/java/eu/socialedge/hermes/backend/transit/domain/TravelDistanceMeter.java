package eu.socialedge.hermes.backend.transit.domain;

import lombok.val;
import org.apache.commons.lang3.tuple.Pair;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public interface TravelDistanceMeter {

    default Quantity<Length> calculate(Location origin, Location destination) {
        val distances = calculate(Collections.singletonList(origin), Collections.singletonList(destination));
        return distances.values().iterator().next();
    }

    Map<Pair<Location, Location>, Quantity<Length>> calculate(List<Location> origins, List<Location> destinations);

}
