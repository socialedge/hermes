package eu.socialedge.hermes.backend.application.api.v2.mapping;

import eu.socialedge.hermes.backend.application.api.dto.LineDTO;
import eu.socialedge.hermes.backend.application.api.v2.mapping.util.EntityBuilder;
import eu.socialedge.hermes.backend.transit.domain.VehicleType;
import eu.socialedge.hermes.backend.transit.domain.provider.Agency;
import eu.socialedge.hermes.backend.transit.domain.service.Line;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
public class LineMapper implements SelectiveMapper<Line, LineDTO> {

    private final RouteMapper routeMapper;

    @Autowired
    public LineMapper(RouteMapper routeMapper) {
        this.routeMapper = routeMapper;
    }

    @Override
    public LineDTO toDTO(Line line) {
        if (line == null)
            return null;

        val dto = new LineDTO();

        dto.setId(line.getId());
        dto.setName(line.getName());
        dto.setDescription(line.getDescription());
        dto.setVehicleType(line.getVehicleType().name());
        dto.setAgencyId(line.getAgency().getId());
        dto.setOutboundRoute(routeMapper.toDTO(line.getOutboundRoute()));

        if (line.getInboundRoute() != null)
            dto.setInboundRoute(routeMapper.toDTO(line.getInboundRoute()));

        if (line.getUrl() != null)
            dto.setUrl(line.getUrl().toString());

        return dto;
    }

    @Override
    public Line toDomain(LineDTO dto) {
        if (dto == null)
            return null;

        try {
            val id = dto.getId();
            val name = dto.getName();
            val desc = dto.getDescription();
            val vt = VehicleType.fromNameOrOther(dto.getVehicleType());
            val agency = agencyFromId(dto.getAgencyId());
            val outRoute = routeMapper.toDomain(dto.getOutboundRoute());
            val inRoute = routeMapper.toDomain(dto.getInboundRoute());
            val url = isBlank(dto.getUrl()) ? (URL) null : new URL(dto.getUrl());

            return new Line(id, name, desc, vt, outRoute, inRoute, agency, url);
        } catch (MalformedURLException e) {
            throw new MappingException("Failed to map dto to line entity", e);
        }
    }

    @Override
    public void update(Line line, LineDTO dto) {
        if (!isBlank(dto.getName()))
            line.setName(dto.getName());

        if (!isBlank(dto.getDescription()))
            line.setDescription(dto.getDescription());

        if (!isBlank(dto.getVehicleType()))
            line.setVehicleType(VehicleType.fromNameOrOther(dto.getVehicleType()));

        if (!isBlank(dto.getAgencyId())) {
            val agency = agencyFromId(dto.getAgencyId());
            if (agency != null) line.setAgency(agency);
        }

        if (dto.getOutboundRoute() != null) {
            val outRoute = routeMapper.toDomain(dto.getOutboundRoute());
            line.setOutboundRoute(outRoute);
        }

        if (dto.getOutboundRoute() != null) {
            val inRoute = routeMapper.toDomain(dto.getInboundRoute());
            line.setInboundRoute(inRoute);
        }

        if (!isBlank(dto.getUrl())) {
            try {
                line.setUrl(new URL(dto.getUrl()));
            } catch (MalformedURLException e) {
                throw new MappingException("Failed to update line entity", e);
            }
        }
    }

    private Agency agencyFromId(String id) {
        try {
            return EntityBuilder.of(Agency.class).idValue(id).build();
        } catch (ReflectiveOperationException e) {
            throw new MappingException("Failed to create proxy Agency entity", e);
        }
    }
}
