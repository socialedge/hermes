package eu.socialedge.hermes.backend.application.api.v2.serivce;

import eu.socialedge.hermes.backend.application.api.dto.AgencyDTO;
import eu.socialedge.hermes.backend.application.api.v2.mapping.AgencyEntityMapper;
import eu.socialedge.hermes.backend.transit.domain.provider.Agency;
import eu.socialedge.hermes.backend.transit.domain.provider.AgencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AgencyService extends PagingAndSortingService<Agency, String, AgencyDTO> {

    @Autowired
    public AgencyService(AgencyRepository repository,
                         AgencyEntityMapper entityMapper) {
        super(repository, entityMapper);
    }
}
