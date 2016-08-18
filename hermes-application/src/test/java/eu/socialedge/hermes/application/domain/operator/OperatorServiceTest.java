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
package eu.socialedge.hermes.application.domain.operator;

import eu.socialedge.hermes.application.domain.operator.dto.AgencySpecification;
import eu.socialedge.hermes.application.domain.operator.dto.AgencySpecificationMapper;
import eu.socialedge.hermes.domain.geo.Location;
import eu.socialedge.hermes.domain.operator.Agency;
import eu.socialedge.hermes.domain.operator.AgencyId;
import eu.socialedge.hermes.domain.operator.AgencyRepository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.URL;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import javax.ws.rs.NotFoundException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OperatorServiceTest {

    @InjectMocks
    private OperatorService operatorService;

    @Mock
    private AgencyRepository agencyRepository;

    @Spy
    private AgencySpecificationMapper agencyDataMapper;

    @Test
    public void testFetchAllAgenciesReturnCollection() throws Exception {
        List<Agency> agencyList = Arrays.asList(randomAgency(), randomAgency(), randomAgency());
        when(agencyRepository.list()).thenReturn(agencyList);

        Collection<Agency> fetchResult = operatorService.fetchAllAgencies();

        assertEquals(agencyList, fetchResult);
    }

    @Test
    public void testFetchAllAgenciesEmptyResult() throws Exception {
        when(agencyRepository.list()).thenReturn(Collections.emptyList());

        Collection<Agency> fetchResult = operatorService.fetchAllAgencies();

        assertTrue(fetchResult.isEmpty());
        verify(agencyRepository).list();
        verifyNoMoreInteractions(agencyRepository);
    }

    @Test
    public void testFetchAgencySuccess() throws Exception {
        Agency agency = randomAgency();
        when(agencyRepository.get(agency.id())).thenReturn(Optional.of(agency));

        Agency dbAgency = operatorService.fetchAgency(agency.id());

        assertEquals(agency, dbAgency);
        verify(agencyRepository).get(agency.id());
        verifyNoMoreInteractions(agencyRepository);
    }

    @Test(expected = NotFoundException.class)
    public void testFetchAgencyNotFound() throws Exception {
        final AgencyId agencyId = AgencyId.of("agencyId");
        when(agencyRepository.get(agencyId)).thenReturn(Optional.empty());

        operatorService.fetchAgency(agencyId);
    }

    @Test
    public void testCreateAgencyWithAllFields() {
        AgencySpecification spec = agencySpecification();

        Mockito.doAnswer(invocation -> {
            Agency agency = (Agency) invocation.getArguments()[0];

            assertAgencyEqualsToSpec(spec, agency);

            return null;
        }).when(agencyRepository).add(any(Agency.class));

        operatorService.createAgency(spec);

        verify(agencyRepository).add(any(Agency.class));
        verifyNoMoreInteractions(agencyRepository);
    }

    @Test
    public void testCreateAgencyPhoneAndEmailNull() {
        AgencySpecification data = agencySpecification();
        data.phone = null;
        data.email = null;

        Mockito.doAnswer(invocation -> {
            Agency agency = (Agency) invocation.getArguments()[0];

            assertAgencyEqualsToSpec(data, agency);
            assertNull(agency.email());
            assertNull(agency.phone());

            return null;
        }).when(agencyRepository).add(any(Agency.class));

        operatorService.createAgency(data);

        verify(agencyRepository).add(any(Agency.class));
        verifyNoMoreInteractions(agencyRepository);
    }

    @Test
    public void testUpdateAgencyAllFields() throws Exception {
        Agency agencyToUpdate = randomAgency();
        AgencySpecification data = agencySpecification();
        data.id = agencyToUpdate.id().toString();
        when(agencyRepository.get(agencyToUpdate.id())).thenReturn(Optional.of(agencyToUpdate));
        doAnswer(invocation -> {
            Agency agency = (Agency) invocation.getArguments()[0];

            assertAgencyEqualsToSpec(data, agency);
            assertEquals(data.phone, agency.phone().number());
            assertEquals(data.email, agency.email().address());

            return null;
        }).when(agencyRepository).update(agencyToUpdate);

        operatorService.updateAgency(agencyToUpdate.id(), data);

        verify(agencyRepository).get(agencyToUpdate.id());
        verify(agencyRepository).update(agencyToUpdate);
        verifyNoMoreInteractions(agencyRepository);
    }

    @Test
    public void testUpdateAgencyAllFieldsBlankOrNull() throws Exception {
        Agency agencyToUpdate = randomAgency();
        AgencySpecification data = new AgencySpecification();
        data.id = agencyToUpdate.id().toString();
        data.name = "";
        data.website = "";
        data.phone = "";
        data.email = "";
        when(agencyRepository.get(agencyToUpdate.id())).thenReturn(Optional.of(agencyToUpdate));
        doAnswer(invocation -> {
            Agency agency = (Agency) invocation.getArguments()[0];

            assertEquals(agencyToUpdate.id(), agency.id());
            assertEquals(agencyToUpdate.name(), agency.name());
            assertEquals(agencyToUpdate.website(), agency.website());
            assertEquals(agencyToUpdate.location(), agency.location());
            assertEquals(agencyToUpdate.timeZone(), agency.timeZone());

            return null;
        }).when(agencyRepository).update(agencyToUpdate);

        operatorService.updateAgency(agencyToUpdate.id(), data);

        verify(agencyRepository).get(agencyToUpdate.id());
        verify(agencyRepository).update(agencyToUpdate);
        verifyNoMoreInteractions(agencyRepository);
    }

    @Test(expected = NotFoundException.class)
    public void testUpdateAgencyNotFound() {
        final AgencyId agencyId = AgencyId.of("agencyId");
        when(agencyRepository.get(agencyId)).thenReturn(Optional.empty());

        operatorService.updateAgency(agencyId, agencySpecification());

        verify(agencyRepository).get(agencyId);
        verifyNoMoreInteractions(agencyRepository);
    }

    @Test
    public void testDeleteAgency() {
        final AgencyId agencyId = AgencyId.of("agencyId");
        when(agencyRepository.remove(agencyId)).thenReturn(true);

        operatorService.deleteAgency(agencyId);

        verify(agencyRepository).remove(agencyId);
        verifyNoMoreInteractions(agencyRepository);
    }

    private void assertAgencyEqualsToSpec(AgencySpecification data, Agency agency) {
        assertEquals(data.id, agency.id().toString());
        assertEquals(data.name, agency.name());
        assertEquals(data.website, agency.website().toString());
        assertEquals(data.location.latitude, agency.location().latitude(), 0.0);
        assertEquals(data.location.longitude, agency.location().longitude(), 0.0);
        assertEquals(data.timeZoneOffset, agency.timeZone().toString());
    }

    private Agency randomAgency() throws Exception {
        int aId = ThreadLocalRandom.current().nextInt(100, 1000);

        AgencyId agencyId = AgencyId.of("agency" + aId);
        return new Agency(agencyId, "name", new URL("http://google.com"),
                ZoneOffset.UTC, Location.of(-20, 20));
    }

    private AgencySpecification agencySpecification() {
        AgencySpecification data = new AgencySpecification();
        data.id = "agencyId";
        data.name = "name";
        data.website = "http://google.com";
        data.phone = "+123 123456";
        data.email = "email@mail.ru";
        data.location.latitude = 30f;
        data.location.longitude = 30f;
        data.timeZoneOffset = "+10:00";
        return data;
    }
}
