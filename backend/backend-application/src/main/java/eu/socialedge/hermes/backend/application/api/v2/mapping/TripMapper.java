package eu.socialedge.hermes.backend.application.api.v2.mapping;

import eu.socialedge.hermes.backend.application.api.dto.StopDTO;
import eu.socialedge.hermes.backend.application.api.dto.TripDTO;
import eu.socialedge.hermes.backend.schedule.domain.Stop;
import eu.socialedge.hermes.backend.schedule.domain.Trip;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TripMapper implements Mapper<Trip, TripDTO> {

    private final Mapper<Stop, StopDTO> stopMapper;

    @Autowired
    public TripMapper(Mapper<Stop, StopDTO> stopMapper) {
        this.stopMapper = stopMapper;
    }

    @Override
    public TripDTO toDTO(Trip trip) {
        if (trip == null)
            return null;

        return new TripDTO()
            .vehicleId(trip.getVehicleId())
            .headsign(trip.getHeadsign())
            .stops(stopMapper.toDTO(trip.getStops()));
    }

    @Override
    public Trip toDomain(TripDTO dto) {
        if (dto == null)
            return null;

        val vehId = dto.getVehicleId();
        val headsign = dto.getHeadsign();
        val stops = stopMapper.toDomain(dto.getStops());

        return Trip.of(vehId, headsign, stops);
    }
}
