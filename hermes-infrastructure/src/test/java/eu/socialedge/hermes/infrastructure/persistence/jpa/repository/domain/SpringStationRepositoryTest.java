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
import eu.socialedge.hermes.domain.infrastructure.Station;
import eu.socialedge.hermes.domain.infrastructure.StationId;
import eu.socialedge.hermes.domain.infrastructure.StationRepository;
import eu.socialedge.hermes.domain.transport.VehicleType;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import javax.inject.Inject;

import static eu.socialedge.hermes.infrastructure.persistence.jpa.repository.domain.RandomIdGenerator.randomStationId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringV2TestConfig.class, loader = AnnotationConfigContextLoader.class)
@Transactional
public class SpringStationRepositoryTest {

    @Inject
    private StationRepository stationRepository;

    @Test @Rollback
    public void shouldCreateAndReturnValidStation() throws Exception {
        assertEquals(0, stationRepository.size());

        Station station = randomStation();

        stationRepository.add(station);
        assertEquals(1, stationRepository.size());

        Optional<Station> storedStation1520Opt = stationRepository.get(station.id());
        assertTrue(storedStation1520Opt.isPresent());

        assertEquals(station, storedStation1520Opt.get());
    }

    @Test @Rollback
    public void shouldContainCreatedStation() throws Exception {
        Station station = randomStation();

        stationRepository.add(station);
        assertTrue(stationRepository.contains(station.id()));
    }

    @Test @Rollback
    public void shouldClearRepository() throws Exception {
        assertEquals(0, stationRepository.size());

        Station station = randomStation();
        stationRepository.add(station);
        assertEquals(1, stationRepository.size());

        stationRepository.clear();
        assertEquals(0, stationRepository.size());
    }

    @Test @Rollback
    public void shouldRemoveCreatedStation() throws Exception {
        Station station = randomStation();
        stationRepository.add(station);
        assertEquals(1, stationRepository.size());

        Station station2 = randomStation();
        stationRepository.add(station2);
        assertEquals(2, stationRepository.size());

        stationRepository.remove(station);
        assertEquals(1, stationRepository.size());
    }

    @Test @Rollback
    public void shouldRemoveCreatedStationById() throws Exception {
        Station station = randomStation();
        stationRepository.add(station);
        assertEquals(1, stationRepository.size());

        stationRepository.remove(station.id());
        assertEquals(0, stationRepository.size());
    }

    @Test @Rollback
    public void shouldHaveProperSizeAfterDeletion() throws Exception {
        List<Station> stations = Arrays.asList(randomStation(), randomStation(), randomStation(), randomStation());

        stations.forEach(stationRepository::add);
        assertEquals(stations.size(), stationRepository.size());

        stationRepository.remove(stations.get(ThreadLocalRandom.current().nextInt(0, stations.size() - 1)));
        assertEquals(stations.size() - 1, stationRepository.size());
    }

    @Test @Rollback
    public void shouldRemoveCreatedStationByIds() throws Exception {
        List<Station> stations = Arrays.asList(randomStation(), randomStation(), randomStation(), randomStation());

        stations.forEach(stationRepository::add);
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
        return new Station(randomStationId(), "name1", new Location(10, 10), VehicleType.BUS);
    }
}