package eu.socialedge.hermes.backend.application.api.v2.mapping;

import eu.socialedge.hermes.backend.application.api.dto.SegmentDTO;
import eu.socialedge.hermes.backend.transit.domain.infra.StationRepository;
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
    private final StationRepository stationRepository;

    @Autowired
    public SegmentMapper(LocationMapper locMapper, StationRepository stationRepository) {
        this.locMapper = locMapper;
        this.stationRepository = stationRepository;
    }

    @Override
    public SegmentDTO toDTO(Segment segment) {
        if (segment == null)
            return null;

        val dto = new SegmentDTO();

        dto.setStartStationId(segment.getBegin().getId());
        dto.setStartStationName(segment.getBegin().getName());
        dto.setEndStationId(segment.getEnd().getId());
        dto.setEndStationName(segment.getEnd().getName());
        dto.setLength(segment.getLength().getValue().doubleValue());

        val locDtos = segment.getWaypoints().stream().map(locMapper::toDTO).collect(toList());
        dto.setWaypoints(locDtos);

        return dto;
    }

    @Override
    public Segment toDomain(SegmentDTO dto) {
        if (dto == null)
            return null;

        val begin = stationRepository.findOne(dto.getStartStationId());
        val end = stationRepository.findOne(dto.getEndStationId());
        val length = dto.getLength();
        val waypoints = locMapper.toDomain(dto.getWaypoints());

        if (length == null)
            return Segment.of(begin, end, waypoints);

        return Segment.of(begin, end, Quantities.getQuantity(length, METRE), waypoints);
    }
}
