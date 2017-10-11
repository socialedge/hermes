package eu.socialedge.hermes.backend.application.api.v2.serivce;

import eu.socialedge.hermes.backend.application.api.dto.LineDTO;
import eu.socialedge.hermes.backend.application.api.v2.mapping.LineMapper;
import eu.socialedge.hermes.backend.transit.domain.service.Line;
import eu.socialedge.hermes.backend.transit.domain.service.LineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LineService extends PagingAndSortingService<Line, String, LineDTO> {

    @Autowired
    public LineService(LineRepository repository, LineMapper mapper) {
        super(repository, mapper);
    }
}
