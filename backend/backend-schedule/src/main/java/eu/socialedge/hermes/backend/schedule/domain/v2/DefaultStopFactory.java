package eu.socialedge.hermes.backend.schedule.domain.v2;

import eu.socialedge.hermes.backend.schedule.domain.Stop;
import eu.socialedge.hermes.backend.transit.domain.infra.Station;
import lombok.val;

import java.time.LocalTime;

public class DefaultStopFactory implements StopFactory {

    @Override
    public Stop create(LocalTime arrival, Station station) {
        val departure = arrival.plus(station.getDwell());
        return Stop.of(arrival, departure, station);
    }
}
