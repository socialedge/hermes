package eu.socialedge.hermes.infrastructure.persistence.jpa.repository.domain;

import eu.socialedge.hermes.domain.geo.Location;
import eu.socialedge.hermes.domain.operator.Agency;
import eu.socialedge.hermes.domain.operator.AgencyId;
import eu.socialedge.hermes.domain.operator.AgencyRepository;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.sql.DataSource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringV2TestConfig.class, loader = AnnotationConfigContextLoader.class)
@Transactional
public class SpringAgencyRepositoryTest {
    @Inject
    private AgencyRepository agencyRepository;

    @Test @Rollback
    public void shouldCreateAndReturnValidAgency() throws MalformedURLException {
        assertEquals(0, agencyRepository.size());

        Agency agency = randomAgency();

        agencyRepository.save(agency);
        assertEquals(1, agencyRepository.size());

        Optional<Agency> storedAg1520Opt = agencyRepository.get(agency.id());
        assertTrue(storedAg1520Opt.isPresent());

        assertEquals(agency, storedAg1520Opt.get());
    }

    @Test @Rollback
    public void shouldContainCreatedAgency() throws MalformedURLException {
        Agency agency = randomAgency();

        agencyRepository.save(agency);
        assertTrue(agencyRepository.contains(agency.id()));
    }

    @Test @Rollback
    public void shouldClearRepository() throws MalformedURLException {
        assertEquals(0, agencyRepository.size());

        Agency agency = randomAgency();
        agencyRepository.save(agency);
        assertEquals(1, agencyRepository.size());

        agencyRepository.clear();
        assertEquals(0, agencyRepository.size());
    }

    @Test @Rollback
    public void shouldRemoveCreatedAgency() throws MalformedURLException {
        Agency agency = randomAgency();
        agencyRepository.save(agency);
        assertEquals(1, agencyRepository.size());

        Agency agency2 = randomAgency();
        agencyRepository.save(agency2);
        assertEquals(2, agencyRepository.size());

        agencyRepository.remove(agency);
        assertEquals(1, agencyRepository.size());
    }

    @Test @Rollback
    public void shouldRemoveCreatedAgencyById() throws MalformedURLException {
        Agency agency = randomAgency();
        agencyRepository.save(agency);
        assertEquals(1, agencyRepository.size());

        agencyRepository.remove(agency.id());
        assertEquals(0, agencyRepository.size());
    }



    @Test @Rollback
    public void shouldHaveProperSizeAfterDeletion() throws MalformedURLException {
        ArrayList<Agency> agencies = new ArrayList<Agency>() {{
            add(randomAgency());
            add(randomAgency());
            add(randomAgency());
            add(randomAgency());
        }};
        agencies.forEach(agencyRepository::save);
        assertEquals(agencies.size(), agencyRepository.size());

        agencyRepository.remove(agencies.get(ThreadLocalRandom.current().nextInt(0, agencies.size() - 1)));
        assertEquals(agencies.size() - 1, agencyRepository.size());
    }

    @Test @Rollback
    public void shouldRemoveCreatedAgencyByIds() throws MalformedURLException {
        ArrayList<Agency> agencies = new ArrayList<Agency>() {{
            add(randomAgency());
            add(randomAgency());
            add(randomAgency());
            add(randomAgency());
        }};
        agencies.forEach(agencyRepository::save);
        assertEquals(agencies.size(), agencyRepository.size());

        List<AgencyId> agencyIdsToRemove = agencies.stream()
                .skip(ThreadLocalRandom.current().nextInt(0, agencies.size() - 1))
                .map(Agency::id)
                .collect(Collectors.toList());

        agencyRepository.remove(agencyIdsToRemove);
        assertEquals(agencyIdsToRemove.size(), agencies.size() - agencyRepository.size());
        assertTrue(agencyIdsToRemove.stream().noneMatch(id -> agencyRepository.contains(id)));
    }

    private Agency randomAgency() throws MalformedURLException {
        int aId = ThreadLocalRandom.current().nextInt(100, 1000);

        AgencyId agencyId = AgencyId.of("ag" + aId);
        return new Agency(agencyId, "name", new URL("http://google.com"),
                ZoneOffset.UTC, Location.of(-20, 20));
    }
}