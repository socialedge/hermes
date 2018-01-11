package eu.socialedge.hermes.backend.schedule.domain.v2;

import eu.socialedge.hermes.backend.schedule.domain.Stop;
import eu.socialedge.hermes.backend.transit.domain.infra.Station;

import java.time.LocalTime;

public interface StopFactory {

    Stop create(LocalTime arrival, Station station);
}
