package eu.socialedge.hermes.backend.application.api.v2.mapping;

import eu.socialedge.hermes.backend.application.api.dto.LocationDTO;
import eu.socialedge.hermes.backend.transit.domain.geo.Location;
import lombok.val;
import org.springframework.stereotype.Component;

@Component
public class LocationMapper implements Mapper<Location, LocationDTO> {

    @Override
    public LocationDTO toDTO(Location loc) {
        if (loc == null)
            return null;

        val dto = new LocationDTO();

        dto.setLatitude(loc.getLatitude());
        dto.setLongitude(loc.getLongitude());

        return dto;
    }

    @Override
    public Location toDomain(LocationDTO dto) {
        return Location.of(dto.getLatitude(), dto.getLongitude());
    }
}
