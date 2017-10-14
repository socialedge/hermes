package eu.socialedge.hermes.backend.application.api.v2.mapping;

import eu.socialedge.hermes.backend.application.api.dto.AvailabilityDTO;
import eu.socialedge.hermes.backend.application.api.dto.ScheduleDTO;
import eu.socialedge.hermes.backend.application.api.dto.TripDTO;
import eu.socialedge.hermes.backend.application.api.v2.mapping.util.EntityBuilder;
import eu.socialedge.hermes.backend.schedule.domain.Availability;
import eu.socialedge.hermes.backend.schedule.domain.Schedule;
import eu.socialedge.hermes.backend.schedule.domain.Trip;
import eu.socialedge.hermes.backend.transit.domain.service.Line;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScheduleMapper implements Mapper<Schedule, ScheduleDTO> {

    private final Mapper<Availability, AvailabilityDTO> availabilityMapper;
    private final Mapper<Trip, TripDTO> tripMapper;

    @Autowired
    public ScheduleMapper(Mapper<Availability, AvailabilityDTO> availabilityMapper,
                          Mapper<Trip, TripDTO> tripMapper) {
        this.availabilityMapper = availabilityMapper;
        this.tripMapper = tripMapper;
    }

    @Override
    public ScheduleDTO toDTO(Schedule schedule) {
        if (schedule == null)
            return null;

        // inbound and outbound trips are write only
        return new ScheduleDTO()
            .id(schedule.getId())
            .description(schedule.getDescription())
            .lineId(schedule.getLine().getId())
            .availability(availabilityMapper.toDTO(schedule.getAvailability()));
    }

    @Override
    public Schedule toDomain(ScheduleDTO dto) {
        if (dto == null)
            return null;

        try {
            return new Schedule.Builder()
                .description(dto.getDescription())
                .line(EntityBuilder.proxy(Line.class, dto.getLineId()))
                .availability(availabilityMapper.toDomain(dto.getAvailability()))
                .outboundTrips(tripMapper.toDomain(dto.getOutboundTrips()))
                .inboundTrips(tripMapper.toDomain(dto.getInboundTrips()))
                .build();
        } catch (ReflectiveOperationException e) {
            throw new MappingException("Failed to create proxy Station entity", e);
        }
    }
}
