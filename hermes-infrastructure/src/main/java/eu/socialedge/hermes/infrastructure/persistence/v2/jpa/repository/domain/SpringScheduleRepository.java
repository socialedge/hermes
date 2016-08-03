/**
 * Hermes - The Municipal Transport Timetable System
 * Copyright (c) 2016 SocialEdge
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package eu.socialedge.hermes.infrastructure.persistence.v2.jpa.repository.domain;

import eu.socialedge.hermes.domain.v2.timetable.Schedule;
import eu.socialedge.hermes.domain.v2.timetable.ScheduleId;
import eu.socialedge.hermes.domain.v2.timetable.ScheduleRepository;
import eu.socialedge.hermes.infrastructure.persistence.v2.jpa.entity.JpaSchedule;
import eu.socialedge.hermes.infrastructure.persistence.v2.jpa.mapping.ScheduleEntityMapper;
import eu.socialedge.hermes.infrastructure.persistence.v2.jpa.repository.entity.SpringJpaScheduleRepository;

import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class SpringScheduleRepository extends SpringRepository<Schedule, ScheduleId,
                                                            JpaSchedule, String>
                                                        implements ScheduleRepository {

    private final ScheduleEntityMapper scheduleEntityMapper;

    @Inject
    protected SpringScheduleRepository(SpringJpaScheduleRepository jpaScheduleRepository,
                                       ScheduleEntityMapper scheduleEntityMapper) {
        super(jpaScheduleRepository);
        this.scheduleEntityMapper = scheduleEntityMapper;
    }

    @Override
    protected ScheduleId extractDomainId(Schedule schedule) {
        return schedule.scheduleId();
    }

    @Override
    protected String mapToJpaEntityId(ScheduleId scheduleId) {
        return scheduleId.toString();
    }

    @Override
    protected Schedule mapToDomainObject(JpaSchedule jpaSchedule) {
        return scheduleEntityMapper.mapToDomain(jpaSchedule);
    }

    @Override
    protected JpaSchedule mapToJpaEntity(Schedule schedule) {
        return scheduleEntityMapper.mapToEntity(schedule);
    }
}
