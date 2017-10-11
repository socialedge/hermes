package eu.socialedge.hermes.backend.application.api.v2.mapping;

import eu.socialedge.hermes.backend.application.api.dto.SegmentDTO;
import eu.socialedge.hermes.backend.application.api.dto.SegmentVertexDTO;
import eu.socialedge.hermes.backend.application.api.v2.mapping.util.EntityBuilder;
import eu.socialedge.hermes.backend.transit.domain.infra.Station;
import eu.socialedge.hermes.backend.transit.domain.service.Segment;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tec.uom.se.quantity.Quantities;

import static java.util.stream.Collectors.toList;
import static tec.uom.se.unit.Units.METRE;

@Component
public class SegmentMapper implements Mapper<Segment, SegmentDTO> {

    private final LocationMapper locMapper;

    @Autowired
    public SegmentMapper(LocationMapper locMapper) {
        this.locMapper = locMapper;
    }

    @Override
    public SegmentDTO toDTO(Segment segment) {
        if (segment == null)
            return null;

        val dto = new SegmentDTO();

        val startDto = new SegmentVertexDTO();
        startDto.setStationId(segment.getBegin().getId());
        startDto.setName(segment.getBegin().getName());
        startDto.setLocation(locMapper.toDTO(segment.getBegin().getLocation()));
        dto.setBegin(startDto);

        val endDto = new SegmentVertexDTO();
        endDto.setStationId(segment.getEnd().getId());
        endDto.setName(segment.getEnd().getName());
        endDto.setLocation(locMapper.toDTO(segment.getEnd().getLocation()));
        dto.setEnd(endDto);

        dto.setLength(segment.getLength().getValue().doubleValue());

        val locDtos = segment.getWaypoints().stream().map(locMapper::toDTO).collect(toList());
        dto.setWaypoints(locDtos);

        return dto;
    }

    @Override
    public Segment toDomain(SegmentDTO dto) {
        if (dto == null)
            return null;

        val begin = stationFromId(dto.getBegin().getStationId());
        val end = stationFromId(dto.getEnd().getStationId());
        val length = dto.getLength();
        val waypoints = locMapper.toDomain(dto.getWaypoints());

        if (length == null)
            return Segment.of(begin, end, waypoints);

        return Segment.of(begin, end, Quantities.getQuantity(length, METRE), waypoints);
    }

    private Station stationFromId(String id) {
        try {
            return EntityBuilder.of(Station.class).idValue(id).build();
        } catch (ReflectiveOperationException e) {
            throw new MappingException("Failed to create proxy station entity", e);
        }
    }
}
