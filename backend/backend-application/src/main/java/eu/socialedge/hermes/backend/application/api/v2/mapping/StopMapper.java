package eu.socialedge.hermes.backend.application.api.v2.mapping;

import eu.socialedge.hermes.backend.application.api.dto.StopDTO;
import eu.socialedge.hermes.backend.application.api.v2.mapping.util.EntityBuilder;
import eu.socialedge.hermes.backend.schedule.domain.Stop;
import eu.socialedge.hermes.backend.transit.domain.infra.Station;
import lombok.val;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
public class StopMapper implements Mapper<Stop, StopDTO> {

    @Override
    public StopDTO toDTO(Stop stop) {
        if (stop == null)
            return null;

        return new StopDTO()
            .name(stop.getStation().getName())
            .stationId(stop.getStation().getId())
            .arrival(stop.getArrival().toString())
            .departure(stop.getDeparture().toString());
    }

    @Override
    public Stop toDomain(StopDTO dto) {
        if (dto == null)
            return null;

        try {
            val arrival = LocalTime.parse(dto.getArrival());
            val departure = LocalTime.parse(dto.getDeparture());
            val stationProxy = EntityBuilder.proxy(Station.class, dto.getStationId());

            return Stop.of(arrival, departure, stationProxy);
        } catch (ReflectiveOperationException e) {
            throw new MappingException("Failed to create proxy Station entity", e);
        }
    }
}
