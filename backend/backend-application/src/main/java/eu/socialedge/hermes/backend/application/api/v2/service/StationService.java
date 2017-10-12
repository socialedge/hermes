package eu.socialedge.hermes.backend.application.api.v2.service;

import eu.socialedge.hermes.backend.application.api.dto.StationDTO;
import eu.socialedge.hermes.backend.application.api.v2.mapping.StationMapper;
import eu.socialedge.hermes.backend.transit.domain.infra.Station;
import eu.socialedge.hermes.backend.transit.domain.infra.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StationService extends PagingAndSortingService<Station, String, StationDTO> {

    @Autowired
    public StationService(StationRepository repository, StationMapper mapper) {
        super(repository, mapper);
    }
}
