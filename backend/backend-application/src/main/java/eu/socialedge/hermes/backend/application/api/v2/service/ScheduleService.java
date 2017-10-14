package eu.socialedge.hermes.backend.application.api.v2.service;

import eu.socialedge.hermes.backend.application.api.dto.ScheduleDTO;
import eu.socialedge.hermes.backend.application.api.v2.mapping.ScheduleMapper;
import eu.socialedge.hermes.backend.schedule.domain.Schedule;
import eu.socialedge.hermes.backend.schedule.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScheduleService extends PagingAndSortingService<Schedule, String, ScheduleDTO> {

    @Autowired
    public ScheduleService(ScheduleRepository repository, ScheduleMapper mapper) {
        super(repository, mapper);
    }
}
