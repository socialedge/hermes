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
package eu.socialedge.hermes.infrastructure.persistence.jpa.repository.domain;

import eu.socialedge.hermes.domain.geo.Location;
import eu.socialedge.hermes.domain.operator.Agency;
import eu.socialedge.hermes.domain.operator.AgencyRepository;
import eu.socialedge.hermes.domain.transit.Line;
import eu.socialedge.hermes.domain.transit.LineId;
import eu.socialedge.hermes.domain.transit.LineRepository;
import eu.socialedge.hermes.domain.transport.VehicleType;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import javax.inject.Inject;

import static eu.socialedge.hermes.infrastructure.persistence.jpa.repository.domain.RandomIdGenerator.randomAgencyId;
import static eu.socialedge.hermes.infrastructure.persistence.jpa.repository.domain.RandomIdGenerator.randomLineId;
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
        Agency agency = new Agency(randomAgencyId(), "name", new URL("http://google.com"),
                ZoneOffset.UTC, Location.of(-20, 20));
        agencyRepository.save(agency);

        return new Line(randomLineId(), "name", agency.id(), VehicleType.BUS);
    }
}