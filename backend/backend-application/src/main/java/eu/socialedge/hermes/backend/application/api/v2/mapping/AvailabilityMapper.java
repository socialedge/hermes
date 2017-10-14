package eu.socialedge.hermes.backend.application.api.v2.mapping;

import eu.socialedge.hermes.backend.application.api.dto.AvailabilityDTO;
import eu.socialedge.hermes.backend.schedule.domain.Availability;
import lombok.val;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.util.ArrayList;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Component
public class AvailabilityMapper implements Mapper<Availability, AvailabilityDTO> {

    @Override
    public AvailabilityDTO toDTO(Availability availability) {
        if (availability == null)
            return null;

        val dto = new AvailabilityDTO();

        dto.setDayOfWeek(availability.getAvailabilityDays().stream().map(Enum::name).collect(toList()));
        dto.setStartDate(availability.getStartDate());
        dto.setEndDate(availability.getEndDate());
        dto.setExceptionDays(new ArrayList<>(availability.getExceptionDays()));

        return dto;
    }

    @Override
    public Availability toDomain(AvailabilityDTO dto) {
        if (dto == null)
            return null;

        val daysOfWeek = dto.getDayOfWeek().stream()
            .map(String::toUpperCase)
            .map(DayOfWeek::valueOf)
            .collect(toSet());

        return new Availability.Builder()
            .from(dto.getStartDate())
            .to(dto.getEndDate())
            .daysOfWeek(daysOfWeek)
            .exceptionDays(dto.getExceptionDays())
            .build();
    }
}
