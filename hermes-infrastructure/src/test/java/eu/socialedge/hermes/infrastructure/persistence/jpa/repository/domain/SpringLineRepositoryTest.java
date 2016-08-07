package eu.socialedge.hermes.infrastructure.persistence.jpa.repository.domain;

import eu.socialedge.hermes.domain.geo.Location;
import eu.socialedge.hermes.domain.operator.Agency;
import eu.socialedge.hermes.domain.operator.AgencyId;
import eu.socialedge.hermes.domain.operator.AgencyRepository;
import eu.socialedge.hermes.domain.transit.Line;
import eu.socialedge.hermes.domain.transit.LineId;
import eu.socialedge.hermes.domain.transit.LineRepository;
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
import java.net.URL;
import java.sql.Connection;
import java.sql.Statement;
import java.time.ZoneOffset;
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
public class SpringLineRepositoryTest {

    @Inject
    private LineRepository lineRepository;

    @Inject
    private AgencyRepository agencyRepository;

    @Test @Rollback
    public void shouldCreateAndReturnValidLine() throws Exception {
        assertEquals(0, lineRepository.size());

        Line line = randomLine();

        lineRepository.save(line);
        assertEquals(1, lineRepository.size());

        Optional<Line> storedLine1520Opt = lineRepository.get(line.id());
        assertTrue(storedLine1520Opt.isPresent());

        assertEquals(line, storedLine1520Opt.get());
    }

    @Test @Rollback
    public void shouldContainCreatedLine() throws Exception {
        Line line = randomLine();

        lineRepository.save(line);
        assertTrue(lineRepository.contains(line.id()));
    }

    @Test @Rollback
    public void shouldClearRepository() throws Exception {
        assertEquals(0, lineRepository.size());

        Line line = randomLine();
        lineRepository.save(line);
        assertEquals(1, lineRepository.size());

        lineRepository.clear();
        assertEquals(0, lineRepository.size());
    }

    @Test @Rollback
    public void shouldRemoveCreatedLine() throws Exception {
        Line line = randomLine();
        lineRepository.save(line);
        assertEquals(1, lineRepository.size());

        Line line2 = randomLine();
        lineRepository.save(line2);
        assertEquals(2, lineRepository.size());

        lineRepository.remove(line);
        assertEquals(1, lineRepository.size());
    }

    @Test @Rollback
    public void shouldRemoveCreatedLineById() throws Exception {
        Line line = randomLine();
        lineRepository.save(line);
        assertEquals(1, lineRepository.size());

        lineRepository.remove(line.id());
        assertEquals(0, lineRepository.size());
    }

    @Test @Rollback
    public void shouldHaveProperSizeAfterDeletion() throws Exception {
        List<Line> lines = Arrays.asList(randomLine(), randomLine(), randomLine(), randomLine());

        lines.forEach(lineRepository::save);
        assertEquals(lines.size(), lineRepository.size());

        lineRepository.remove(lines.get(ThreadLocalRandom.current().nextInt(0, lines.size() - 1)));
        assertEquals(lines.size() - 1, lineRepository.size());
    }

    @Test @Rollback
    public void shouldRemoveCreatedLineByIds() throws Exception {
        List<Line> lines = Arrays.asList(randomLine(), randomLine(), randomLine(), randomLine());

        lines.forEach(lineRepository::save);
        assertEquals(lines.size(), lineRepository.size());

        List<LineId> lineIdsToRemove = lines.stream()
                .skip(ThreadLocalRandom.current().nextInt(0, lines.size() - 1))
                .map(Line::id)
                .collect(Collectors.toList());

        lineRepository.remove(lineIdsToRemove);
        assertEquals(lineIdsToRemove.size(), lines.size() - lineRepository.size());
        assertTrue(lineIdsToRemove.stream().noneMatch(id -> lineRepository.contains(id)));
    }

    private Line randomLine() throws MalformedURLException {
        int id = ThreadLocalRandom.current().nextInt(100, 1000);

        Agency agency = new Agency(AgencyId.of("agency" + id), "name", new URL("http://google.com"),
                ZoneOffset.UTC, Location.of(-20, 20));
        agencyRepository.save(agency);

        return new Line(LineId.of("line" + id), "name", agency.id(), VehicleType.BUS);
    }
}