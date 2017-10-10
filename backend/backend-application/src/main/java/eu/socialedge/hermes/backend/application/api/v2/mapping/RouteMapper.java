package eu.socialedge.hermes.backend.application.api.v2.mapping;

import eu.socialedge.hermes.backend.application.api.dto.RouteDTO;
import eu.socialedge.hermes.backend.transit.domain.service.Route;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Component
public class RouteMapper implements Mapper<Route, RouteDTO> {

    private final SegmentMapper segmentMapper;

    @Autowired
    public RouteMapper(SegmentMapper segmentMapper) {
        this.segmentMapper = segmentMapper;
    }

    @Override
    public RouteDTO toDTO(Route route) {
        if (route == null)
            return null;

        val dto = new RouteDTO();

        for (val seg : route) {
            dto.add(segmentMapper.toDTO(seg));
        }

        return dto;
    }

    @Override
    public Route toDomain(RouteDTO dto) {
        if (dto == null)
            return null;

        return dto.stream()
            .map(segmentMapper::toDomain)
            .collect(Collectors.collectingAndThen(toList(), Route::new));
    }
}
