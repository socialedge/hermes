package eu.socialedge.hermes.infrastructure.persistence.v2.jpa.repository.domain;

import eu.socialedge.hermes.domain.v2.operator.Agency;
import eu.socialedge.hermes.domain.v2.operator.AgencyId;
import eu.socialedge.hermes.domain.v2.geo.Location;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringV2TestConfig.class, loader = AnnotationConfigContextLoader.class)
public class SpringAgencyRepositoryTest {
    @Autowired SpringAgencyRepository springAgencyRepository;

    @Test
    public void shouldCreateAndReturnValidAgency() throws MalformedURLException {
        assertEquals(0, springAgencyRepository.size());

        AgencyId agencyId = AgencyId.of("ag1520");
        Agency ag1520 = new Agency(agencyId, "name", new URL("http://google.com"),
                ZoneOffset.UTC, Location.of(-20, 20));

        springAgencyRepository.store(ag1520);
        assertEquals(1, springAgencyRepository.size());

        Optional<Agency> storedAg1520Opt = springAgencyRepository.get(agencyId);
        assertTrue(storedAg1520Opt.isPresent());

        assertEquals(ag1520, storedAg1520Opt.get());
    }
}