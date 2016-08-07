package eu.socialedge.hermes.infrastructure.persistence.jpa.repository.domain;

import eu.socialedge.hermes.domain.geo.Location;
import eu.socialedge.hermes.domain.infrastructure.Station;
import eu.socialedge.hermes.domain.infrastructure.StationId;
import eu.socialedge.hermes.domain.infrastructure.StationRepository;
import eu.socialedge.hermes.domain.timetable.Schedule;
import eu.socialedge.hermes.domain.timetable.ScheduleAvailability;
import eu.socialedge.hermes.domain.timetable.ScheduleId;
import eu.socialedge.hermes.domain.timetable.ScheduleRepository;
import eu.socialedge.hermes.domain.transit.Route;
import eu.socialedge.hermes.domain.transit.RouteId;
import eu.socialedge.hermes.domain.transit.RouteRepository;
import eu.socialedge.hermes.domain.transport.VehicleType;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringV2TestConfig.class, loader = AnnotationConfigContextLoader.class)
public class SpringScheduleRepositoryTest {

    @Inject
    private ScheduleRepository scheduleRepository;

    @Inject
    private RouteRepository routeRepository;

    @Inject
    private StationRepository stationRepository;

    @Inject
    private DataSource dataSource;

    @After
    public void cleanScheduleRepository() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("SET REFERENTIAL_INTEGRITY FALSE");
            }
            try (Statement statement = connection.createStatement()) {
                statement.execute("TRUNCATE TABLE stations");
            }
            try (Statement statement = connection.createStatement()) {
                statement.execute("TRUNCATE TABLE routes");
            }
            try (Statement statement = connection.createStatement()) {
                statement.execute("TRUNCATE TABLE schedules");
            }
            try (Statement statement = connection.createStatement()) {
                statement.execute("SET REFERENTIAL_INTEGRITY TRUE");
            }
        }
    }

    @Test
    public void shouldCreateAndReturnValidSchedule() throws Exception {
        assertEquals(0, scheduleRepository.size());

        Schedule schedule = randomSchedule();

        scheduleRepository.save(schedule);
        assertEquals(1, scheduleRepository.size());

        Optional<Schedule> storedSchedule1520Opt = scheduleRepository.get(schedule.id());
        assertTrue(storedSchedule1520Opt.isPresent());

        assertEquals(schedule, storedSchedule1520Opt.get());
    }

    @Test
    public void shouldContainCreatedSchedule() throws Exception {
        Schedule schedule = randomSchedule();

        scheduleRepository.save(schedule);
        assertTrue(scheduleRepository.contains(schedule.id()));
    }

    @Test
    public void shouldClearRepository() throws Exception {
        assertEquals(0, scheduleRepository.size());

        Schedule schedule = randomSchedule();
        scheduleRepository.save(schedule);
        assertEquals(1, scheduleRepository.size());

        scheduleRepository.clear();
        assertEquals(0, scheduleRepository.size());
    }

    @Test
    public void shouldRemoveCreatedSchedule() throws Exception {
        Schedule schedule = randomSchedule();
        scheduleRepository.save(schedule);
        assertEquals(1, scheduleRepository.size());

        Schedule schedule2 = randomSchedule();
        scheduleRepository.save(schedule2);
        assertEquals(2, scheduleRepository.size());

        scheduleRepository.remove(schedule);
        assertEquals(1, scheduleRepository.size());
    }

    @Test
    public void shouldRemoveCreatedScheduleById() throws Exception {
        Schedule schedule = randomSchedule();
        scheduleRepository.save(schedule);
        assertEquals(1, scheduleRepository.size());

        scheduleRepository.remove(schedule.id());
        assertEquals(0, scheduleRepository.size());
    }

    @Test
    public void shouldHaveProperSizeAfterDeletion() throws Exception {
        List<Schedule> schedules = Arrays.asList(randomSchedule(), randomSchedule(), randomSchedule(), randomSchedule());

        schedules.forEach(scheduleRepository::save);
        assertEquals(schedules.size(), scheduleRepository.size());

        scheduleRepository.remove(schedules.get(ThreadLocalRandom.current().nextInt(0, schedules.size() - 1)));
        assertEquals(schedules.size() - 1, scheduleRepository.size());
    }

    @Test
    public void shouldRemoveCreatedScheduleByIds() throws Exception {
        List<Schedule> schedules = Arrays.asList(randomSchedule(), randomSchedule(), randomSchedule(), randomSchedule());

        schedules.forEach(scheduleRepository::save);
        assertEquals(schedules.size(), scheduleRepository.size());

        List<ScheduleId> scheduleIdsToRemove = schedules.stream()
                .skip(ThreadLocalRandom.current().nextInt(0, schedules.size() - 1))
                .map(Schedule::id)
                .collect(Collectors.toList());

        scheduleRepository.remove(scheduleIdsToRemove);
        assertEquals(scheduleIdsToRemove.size(), schedules.size() - scheduleRepository.size());
        assertTrue(scheduleIdsToRemove.stream().noneMatch(id -> scheduleRepository.contains(id)));
    }

    private Schedule randomSchedule() throws MalformedURLException {
        int id = ThreadLocalRandom.current().nextInt(100, 1000);

        Station station = new Station(StationId.of("station" + id + 1), "name" + id + 1, new Location(11, 11), VehicleType.BUS);
        stationRepository.save(station);

        Route route = new Route(RouteId.of("route" + id), Arrays.asList(station.id()));
        routeRepository.save(route);

        return new Schedule(ScheduleId.of("schedule" + id), route.id(),
                ScheduleAvailability.weekendDays(LocalDate.now().minusDays(1), LocalDate.now()));
    }
}