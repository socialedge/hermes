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
package eu.socialedge.hermes.domain.timetable;

import eu.socialedge.hermes.application.exception.NotFoundException;
import eu.socialedge.hermes.application.service.StationService;
import eu.socialedge.hermes.domain.infrastructure.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class StationServiceTest {

    @InjectMocks
    private StationService stationService;

    @Mock
    private StationRepository stationRepository;

    private final String stationCode1 = "station1";
    private final Station station1 = new Station(stationCode1, "stationName1",
            TransportType.BUS, new Position(1, 1));

    @Test
    public void testFetchStationSuccess() {
        when(stationRepository.get(stationCode1)).thenReturn(Optional.of(station1));

        Station station = stationService.fetchStation(stationCode1);

        assertEquals(station1, station);
        verify(stationRepository).get(stationCode1);
        verifyNoMoreInteractions(stationRepository);
    }

    @Test(expected = NullPointerException.class)
    public void testFetchStationCodeNull() {
        stationService.fetchStation(null);

        verifyZeroInteractions(stationRepository);
    }

    @Test(expected = NotFoundException.class)
    public void testFetchStationNotFound() {
        when(stationRepository.get(stationCode1)).thenReturn(Optional.empty());

        stationService.fetchStation(stationCode1);

        verify(stationRepository).get(stationCode1);
        verifyNoMoreInteractions(stationRepository);
    }

    @Test
    public void testFetchAllStationsSuccess() {
        Station station2 = new Station("station2", "stationName2", TransportType.TRAM, new Position(2, 2));
        List<Station> stations = Arrays.asList(station1, station2);
        when(stationRepository.list()).thenReturn(stations);

        assertEquals(stations, stationService.fetchAllStations());

        verify(stationRepository).list();
        verifyNoMoreInteractions(stationRepository);
    }

    @Test
    public void testFetchAllStationsEmpty() {
        when(stationRepository.list()).thenReturn(Collections.emptyList());

        assertTrue(stationService.fetchAllStations().isEmpty());

        verify(stationRepository).list();
        verifyNoMoreInteractions(stationRepository);
    }

    @Test
    public void testCreateStationSuccess() {
        when(stationRepository.store(station1)).then(invocation -> {
            Station station = (Station) invocation.getArguments()[0];

            assertEquals(station1, station);
            assertEquals(station1.getName(), station.getName());
            assertEquals(station1.getPosition(), station.getPosition());
            assertEquals(station1.getTransportType(), station.getTransportType());

            return station;
        });

        stationService.createStation(station1.getCodeId(), station1.getName(), station1.getTransportType(),
                station1.getPosition());

        verify(stationRepository).store(station1);
        verifyNoMoreInteractions(stationRepository);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateStationCodeEmpty() {
        stationService.createStation("", station1.getName(), station1.getTransportType(), station1.getPosition());

        verifyZeroInteractions(stationRepository);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateStationNameEmpty() {
        stationService.createStation(station1.getCodeId(), "", station1.getTransportType(), station1.getPosition());

        verifyZeroInteractions(stationRepository);
    }

    @Test(expected = NullPointerException.class)
    public void testCreateStationTransportTypeNull() {
        stationService.createStation(station1.getCodeId(), station1.getName(), null, station1.getPosition());

        verifyZeroInteractions(stationRepository);
    }

    @Test(expected = NullPointerException.class)
    public void testCreateStationPositionNull() {
        stationService.createStation(station1.getCodeId(), station1.getName(), station1.getTransportType(), null);

        verifyZeroInteractions(stationRepository);
    }

    @Test
    public void testUpdateStationSuccess() {
        String newName = "newName";
        when(stationRepository.get(stationCode1)).thenReturn(Optional.of(station1));
        when(stationRepository.store(station1)).then(invocation -> {
            Station station = (Station) invocation.getArguments()[0];

            assertEquals(newName, station.getName());

            return station;
        });

        stationService.updateStation(stationCode1, newName);

        verify(stationRepository).get(stationCode1);
        verify(stationRepository).store(station1);
        verifyNoMoreInteractions(stationRepository);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateStationEmptyName() {
        when(stationRepository.get(stationCode1)).thenReturn(Optional.of(station1));

        stationService.updateStation(stationCode1, "");

        verify(stationRepository).get(stationCode1);
        verifyNoMoreInteractions(stationRepository);
    }

    @Test
    public void testRemoveStationSuccess() {
        when(stationRepository.get(stationCode1)).thenReturn(Optional.of(station1));

        stationService.removeStation(stationCode1);

        verify(stationRepository).get(stationCode1);
        verify(stationRepository).remove(station1);
        verifyNoMoreInteractions(stationRepository);
    }

    @Test(expected = NotFoundException.class)
    public void testRemoveStationNotFound() {
        when(stationRepository.get(stationCode1)).thenReturn(Optional.empty());

        stationService.removeStation(stationCode1);

        verify(stationRepository).get(stationCode1);
        verifyNoMoreInteractions(stationRepository);
    }
}
