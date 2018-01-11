package eu.socialedge.hermes.backend.schedule.domain.v2;

import eu.socialedge.hermes.backend.schedule.domain.Trip;
import eu.socialedge.hermes.backend.transit.domain.service.Route;

import java.time.LocalTime;

public interface TripFactory {

    Trip create(LocalTime startTime, Integer vehicleId, String headsign, Route route);

    default Trip create(LocalTime startTime, Integer vehicleId, Route route) {
        return create(startTime, vehicleId, null, route);
    }
}
