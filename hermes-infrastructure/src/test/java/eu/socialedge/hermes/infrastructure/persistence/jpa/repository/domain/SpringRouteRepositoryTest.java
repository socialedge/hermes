package eu.socialedge.hermes.infrastructure.persistence.jpa.repository.domain;

import eu.socialedge.hermes.domain.geo.Location;
import eu.socialedge.hermes.domain.infrastructure.Station;
import eu.socialedge.hermes.domain.infrastructure.StationId;
import eu.socialedge.hermes.domain.infrastructure.StationRepository;
import eu.socialedge.hermes.domain.transit.Route;
import eu.socialedge.hermes.domain.transit.RouteId;
import eu.socialedge.hermes.domain.transit.RouteRepository;
import eu.socialedge.hermes.domain.transport.VehicleType;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

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
@Transactional
public class SpringRouteRepositoryTest {

    @Inject
    private RouteRepository routeRepository;

    @Inject
    private StationRepository stationRepository;

    @Test @Rollback
    public void shouldCreateAndReturnValidRoute() throws Exception {
        assertEquals(0, routeRepository.size());

        Route route = randomRoute();

        routeRepository.save(route);
        assertEquals(1, routeRepository.size());

        Optional<Route> storedRoute1520Opt = routeRepository.get(route.id());
        assertTrue(storedRoute1520Opt.isPresent());

        assertEquals(route, storedRoute1520Opt.get());
    }

    @Test @Rollback
    public void shouldContainCreatedRoute() throws Exception {
        Route route = randomRoute();

        routeRepository.save(route);
        assertTrue(routeRepository.contains(route.id()));
    }

    @Test @Rollback
    public void shouldClearRepository() throws Exception {
        assertEquals(0, routeRepository.size());

        Route route = randomRoute();
        routeRepository.save(route);
        assertEquals(1, routeRepository.size());

        routeRepository.clear();
        assertEquals(0, routeRepository.size());
    }

    @Test @Rollback
    public void shouldRemoveCreatedRoute() throws Exception {
        Route route = randomRoute();
        routeRepository.save(route);
        assertEquals(1, routeRepository.size());

        Route route2 = randomRoute();
        routeRepository.save(route2);
        assertEquals(2, routeRepository.size());

        routeRepository.remove(route);
        assertEquals(1, routeRepository.size());
    }

    @Test @Rollback
    public void shouldRemoveCreatedRouteById() throws Exception {
        Route route = randomRoute();
        routeRepository.save(route);
        assertEquals(1, routeRepository.size());

        routeRepository.remove(route.id());
        assertEquals(0, routeRepository.size());
    }

    @Test @Rollback
    public void shouldHaveProperSizeAfterDeletion() throws Exception {
        List<Route> routes = Arrays.asList(randomRoute(), randomRoute(), randomRoute(), randomRoute());

        routes.forEach(routeRepository::save);
        assertEquals(routes.size(), routeRepository.size());

        routeRepository.remove(routes.get(ThreadLocalRandom.current().nextInt(0, routes.size() - 1)));
        assertEquals(routes.size() - 1, routeRepository.size());
    }

    @Test @Rollback
    public void shouldRemoveCreatedRouteByIds() throws Exception {
        List<Route> routes = Arrays.asList(randomRoute(), randomRoute(), randomRoute(), randomRoute());

        routes.forEach(routeRepository::save);
        assertEquals(routes.size(), routeRepository.size());

        List<RouteId> routeIdsToRemove = routes.stream()
                .skip(ThreadLocalRandom.current().nextInt(0, routes.size() - 1))
                .map(Route::id)
                .collect(Collectors.toList());

        routeRepository.remove(routeIdsToRemove);
        assertEquals(routeIdsToRemove.size(), routes.size() - routeRepository.size());
        assertTrue(routeIdsToRemove.stream().noneMatch(id -> routeRepository.contains(id)));
    }

    private Route randomRoute() throws MalformedURLException {
        int id = ThreadLocalRandom.current().nextInt(100, 1000);

        Station station1 = new Station(StationId.of("station" + id), "name" + id, new Location(10, 10), VehicleType.BUS);
        Station station2 = new Station(StationId.of("station" + id + 1), "name" + id + 1, new Location(11, 11), VehicleType.BUS);

        stationRepository.save(station1);
        stationRepository.save(station2);

        return new Route(RouteId.of("route" + id), Arrays.asList(station1.id(), station2.id()));
    }
}