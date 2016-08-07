package eu.socialedge.hermes.infrastructure.persistence.jpa.repository.domain;

import eu.socialedge.hermes.domain.geo.Location;
import eu.socialedge.hermes.domain.infrastructure.Station;
import eu.socialedge.hermes.domain.infrastructure.StationId;
import eu.socialedge.hermes.domain.infrastructure.StationRepository;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringV2TestConfig.class, loader = AnnotationConfigContextLoader.class)
public class SpringStationRepositoryTest {

    @Inject
    private StationRepository stationRepository;

    @Inject
    private DataSource dataSource;

    @After
    public void cleanStationRepository() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("SET REFERENTIAL_INTEGRITY FALSE");
            }
            try (Statement statement = connection.createStatement()) {
                statement.execute("TRUNCATE TABLE stations");
            }
            try (Statement statement = connection.createStatement()) {
                statement.execute("SET REFERENTIAL_INTEGRITY TRUE");
            }
        }
    }

    @Test
    public void shouldCreateAndReturnValidStation() throws Exception {
        assertEquals(0, stationRepository.size());

        Station station = randomStation();

        stationRepository.save(station);
        assertEquals(1, stationRepository.size());

        Optional<Station> storedStation1520Opt = stationRepository.get(station.id());
        assertTrue(storedStation1520Opt.isPresent());

        assertEquals(station, storedStation1520Opt.get());
    }

    @Test
    public void shouldContainCreatedStation() throws Exception {
        Station station = randomStation();

        stationRepository.save(station);
        assertTrue(stationRepository.contains(station.id()));
    }

    @Test
    public void shouldClearRepository() throws Exception {
        assertEquals(0, stationRepository.size());

        Station station = randomStation();
        stationRepository.save(station);
        assertEquals(1, stationRepository.size());

        stationRepository.clear();
        assertEquals(0, stationRepository.size());
    }

    @Test
    public void shouldRemoveCreatedStation() throws Exception {
        Station station = randomStation();
        stationRepository.save(station);
        assertEquals(1, stationRepository.size());

        Station station2 = randomStation();
        stationRepository.save(station2);
        assertEquals(2, stationRepository.size());

        stationRepository.remove(station);
        assertEquals(1, stationRepository.size());
    }

    @Test
    public void shouldRemoveCreatedStationById() throws Exception {
        Station station = randomStation();
        stationRepository.save(station);
        assertEquals(1, stationRepository.size());

        stationRepository.remove(station.id());
        assertEquals(0, stationRepository.size());
    }

    @Test
    public void shouldHaveProperSizeAfterDeletion() throws Exception {
        List<Station> stations = Arrays.asList(randomStation(), randomStation(), randomStation(), randomStation());

        stations.forEach(stationRepository::save);
        assertEquals(stations.size(), stationRepository.size());

        stationRepository.remove(stations.get(ThreadLocalRandom.current().nextInt(0, stations.size() - 1)));
        assertEquals(stations.size() - 1, stationRepository.size());
    }

    @Test
    public void shouldRemoveCreatedStationByIds() throws Exception {
        List<Station> stations = Arrays.asList(randomStation(), randomStation(), randomStation(), randomStation());

        stations.forEach(stationRepository::save);
        assertEquals(stations.size(), stationRepository.size());

        List<StationId> stationIdsToRemove = stations.stream()
                .skip(ThreadLocalRandom.current().nextInt(0, stations.size() - 1))
                .map(Station::id)
                .collect(Collectors.toList());

        stationRepository.remove(stationIdsToRemove);
        assertEquals(stationIdsToRemove.size(), stations.size() - stationRepository.size());
        assertTrue(stationIdsToRemove.stream().noneMatch(id -> stationRepository.contains(id)));
    }

    private Station randomStation() throws MalformedURLException {
        int id = ThreadLocalRandom.current().nextInt(100, 1000);
        return new Station(StationId.of("station" + id), "name" + id, new Location(10, 10), VehicleType.BUS);
    }
}