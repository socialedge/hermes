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
package eu.socialedge.hermes.infrastructure.persistence.spring;

import eu.socialedge.hermes.domain.infrastructure.Route;
import eu.socialedge.hermes.domain.infrastructure.RouteRepository;
import eu.socialedge.hermes.domain.timetable.Schedule;
import eu.socialedge.hermes.domain.timetable.ScheduleRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringTestConfig.class, loader = AnnotationConfigContextLoader.class)
@Transactional
public class SpringJpaScheduleRepositoryTest {
    @Autowired private ScheduleRepository scheduleRepository;
    @Inject private RouteRepository routeRepository;

    private Route route1;
    private Route route2;
    private Schedule schedule1;
    private Schedule schedule2;

    @Before
    public void setUp() {
        route1 = new Route("route1");
        routeRepository.store(route1);
        route2 = new Route("route2");
        routeRepository.store(route2);

        schedule1 = new Schedule("schedule1", route1);
        scheduleRepository.store(schedule1);
        schedule2 = new Schedule("schedule2", route2);
        scheduleRepository.store(schedule2);
    }

    @Test
    public void testFindByRouteCodeIdOneSchedule() {
        Collection<Schedule> schedules = scheduleRepository.findByRouteCodeId(route1.getCodeId());

        assertEquals(1, schedules.size());
        assertTrue(schedules.contains(schedule1));
    }

    @After
    public void tearDown() {
        scheduleRepository.remove(schedule1);
        scheduleRepository.remove(schedule2);
        routeRepository.remove(route1);
        routeRepository.remove(route2);
    }
}
