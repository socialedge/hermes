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
package eu.socialedge.hermes.application.domain.infrastructure;

import eu.socialedge.hermes.application.domain.infrastructure.InfrastructureService;
import eu.socialedge.hermes.application.domain.infrastructure.StationData;
import eu.socialedge.hermes.application.domain.infrastructure.StationDataMapper;
import eu.socialedge.hermes.domain.geo.Location;
import eu.socialedge.hermes.domain.infrastructure.Station;
import eu.socialedge.hermes.domain.infrastructure.StationId;
import eu.socialedge.hermes.domain.infrastructure.StationRepository;
import eu.socialedge.hermes.domain.transport.VehicleType;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import javax.ws.rs.NotFoundException;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class InfrastructureServiceTest {

    @InjectMocks
    private InfrastructureService infrastructureService;

    @Mock
    private StationRepository stationRepository;

    @Test
    public void testFetchAllStationsReturnCollection() throws Exception {
        List<Station> stationList = Arrays.asList(randomStation(), randomStation(), randomStation());
        when(stationRepository.list()).thenReturn(stationList);

        Collection<StationData> fetchResult = infrastructureService.fetchAllStations();

        assertEquals(stationList, fetchResult.stream().map(StationDataMapper::fromData).collect(Collectors.toList()));
    }

    @Test
    public void testFetchAllStationsEmptyResult() throws Exception {
        when(stationRepository.list()).thenReturn(Collections.emptyList());

        Collection<StationData> fetchResult = infrastructureService.fetchAllStations();

        assertTrue(fetchResult.isEmpty());
        verify(stationRepository).list();
        verifyNoMoreInteractions(stationRepository);
    }

    @Test
    public void testFetchStationReturnStation() throws Exception {
        Station station = randomStation();
        when(stationRepository.get(station.id())).thenReturn(Optional.of(station));

        StationData stationData = infrastructureService.fetchStation(station.id());

        assertEquals(station, StationDataMapper.fromData(stationData));
    }

    @Test(expected = NotFoundException.class)
    public void testFetchStationNotFound() throws Exception {
        final StationId stationId = StationId.of("stationId");
        when(stationRepository.get(stationId)).thenReturn(Optional.empty());

        infrastructureService.fetchStation(stationId);
    }

    @Test
    public void testCreateStationWithAllFields() {
        StationData data = stationSpecification();

        Mockito.doAnswer(invocation -> {
            Station station = (Station) invocation.getArguments()[0];

            assertStationEqualsToSpec(data, station);

            return null;
        }).when(stationRepository).add(any(Station.class));

        infrastructureService.createStation(data);

        verify(stationRepository).add(any(Station.class));
        verifyNoMoreInteractions(stationRepository);
    }

    @Test
    public void testUpdateStationAllFields() throws Exception {
        Station stationToUpdate = randomStation();
        StationData data = stationSpecification();
        data.stationId = stationToUpdate.id().toString();
        when(stationRepository.get(stationToUpdate.id())).thenReturn(Optional.of(stationToUpdate));
        doAnswer(invocation -> {
            Station station = (Station) invocation.getArguments()[0];

            assertStationEqualsToSpec(data, station);

            return null;
        }).when(stationRepository).update(stationToUpdate);

        infrastructureService.updateStation(stationToUpdate.id(), data);

        verify(stationRepository).get(stationToUpdate.id());
        verify(stationRepository).update(stationToUpdate);
        verifyNoMoreInteractions(stationRepository);
    }

    @Test
    public void testUpdateStationAllFieldsBlankOrNull() throws Exception {
        Station stationToUpdate = randomStation();
        StationData data = new StationData();
        data.stationId = stationToUpdate.id().toString();
        data.name = "";
        data.vehicleTypes = Collections.emptySet();
        when(stationRepository.get(stationToUpdate.id())).thenReturn(Optional.of(stationToUpdate));
        doAnswer(invocation -> {
            Station station = (Station) invocation.getArguments()[0];

            assertEquals(stationToUpdate.id(), station.id());
            assertEquals(stationToUpdate.name(), station.name());
            assertEquals(stationToUpdate.location(), station.location());
            assertEquals(stationToUpdate.vehicleTypes(), station.vehicleTypes());

            return null;
        }).when(stationRepository).update(stationToUpdate);

        infrastructureService.updateStation(stationToUpdate.id(), data);

        verify(stationRepository).get(stationToUpdate.id());
        verify(stationRepository).update(stationToUpdate);
        verifyNoMoreInteractions(stationRepository);
    }

    @Test(expected = NotFoundException.class)
    public void testUpdateStationNotFound() {
        final StationId stationId = StationId.of("stationId");
        when(stationRepository.get(stationId)).thenReturn(Optional.empty());

        infrastructureService.updateStation(stationId, stationSpecification());

        verify(stationRepository).get(stationId);
        verifyNoMoreInteractions(stationRepository);
    }

    @Test
    public void testDeleteStation() {
        final StationId stationId = StationId.of("stationId");
        when(stationRepository.remove(stationId)).thenReturn(true);

        infrastructureService.deleteStation(stationId);

        verify(stationRepository).remove(stationId);
        verifyNoMoreInteractions(stationRepository);
    }

    private void assertStationEqualsToSpec(StationData data, Station station) {
        assertEquals(data.stationId, station.id().toString());
        assertEquals(data.name, station.name());
        assertEquals(data.locationLatitude, station.location().latitude(), 0.0);
        assertEquals(data.locationLongitude, station.location().longitude(), 0.0);
        assertEquals(data.vehicleTypes, station.vehicleTypes().stream().map(VehicleType::name).collect(Collectors.toSet()));
    }

    private Station randomStation() throws Exception {
        int id = ThreadLocalRandom.current().nextInt(100, 1000);
        Set<VehicleType> vehicleTypes = new HashSet<VehicleType>() {{
            add(VehicleType.FUNICULAR);
            add(VehicleType.LOCAL_BUS);
        }};
        return new Station(StationId.of("station" + id), "name" + id, new Location(23, 32), vehicleTypes);
    }

    private StationData stationSpecification() {
        StationData data = new StationData();
        data.stationId = "stationId";
        data.locationLatitude = 10f;
        data.locationLongitude = 10f;
        data.name = "name";
        data.vehicleTypes = new HashSet<String>() {{
            add("BUS");
            add("CABLE_CAR");
            add("COACH");
        }};

        return data;
    }
}
