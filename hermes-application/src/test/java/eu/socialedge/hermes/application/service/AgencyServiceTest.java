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
package eu.socialedge.hermes.application.service;

import eu.socialedge.hermes.application.resource.spec.AgencySpecification;
import eu.socialedge.hermes.domain.geo.Location;
import eu.socialedge.hermes.domain.operator.Agency;
import eu.socialedge.hermes.domain.operator.AgencyId;
import eu.socialedge.hermes.domain.operator.AgencyRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.URL;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AgencyServiceTest {

    @InjectMocks
    private AgencyService agencyService;

    @Mock
    private AgencyRepository agencyRepository;

    @Test
    public void testFetchAllAgenciesReturnCollection() throws Exception {
        List<Agency> agencyList = Arrays.asList(randomAgency(), randomAgency(), randomAgency());
        when(agencyRepository.list()).thenReturn(agencyList);

        Collection<Agency> fetchResult = agencyService.fetchAllAgencies();

        assertEquals(agencyList, fetchResult);
    }

    @Test
    public void testFetchAllAgenciesEmptyResult() throws Exception {
        when(agencyRepository.list()).thenReturn(Collections.emptyList());

        Collection<Agency> fetchResult = agencyService.fetchAllAgencies();

        assertTrue(fetchResult.isEmpty());
        verify(agencyRepository).list();
        verifyNoMoreInteractions(agencyRepository);
    }

    @Test
    public void testFetchAgencyNotFound() throws Exception {
        final AgencyId agencyId = AgencyId.of("agencyId");
        when(agencyRepository.get(agencyId)).thenReturn(Optional.empty());

        Optional<Agency> fetchResultOpt = agencyService.fetchAgency(agencyId);

        assertFalse(fetchResultOpt.isPresent());
        verify(agencyRepository).get(agencyId);
        verifyNoMoreInteractions(agencyRepository);
    }

    @Test
    public void testCreateAgencyWithAllFields() {
        AgencySpecification spec = agencySpecification();

        Mockito.doAnswer(invocation -> {
            Agency agency = (Agency) invocation.getArguments()[0];

            assertAgencyEqualsToSpec(spec, agency);

            return null;
        }).when(agencyRepository).save(any(Agency.class));

        agencyService.createAgency(spec);

        verify(agencyRepository).save(any(Agency.class));
        verifyNoMoreInteractions(agencyRepository);
    }

    @Test
    public void testCreateAgencyPhoneAndEmailNull() {
        AgencySpecification spec = agencySpecification();
        spec.phone = null;
        spec.email = null;

        Mockito.doAnswer(invocation -> {
            Agency agency = (Agency) invocation.getArguments()[0];

            assertAgencyEqualsToSpec(spec, agency);
            assertNull(agency.email());
            assertNull(agency.phone());

            return null;
        }).when(agencyRepository).save(any(Agency.class));

        agencyService.createAgency(spec);

        verify(agencyRepository).save(any(Agency.class));
        verifyNoMoreInteractions(agencyRepository);
    }

    @Test
    public void testUpdateAgencyAllFields() throws Exception {
        Agency agencyToUpdate = randomAgency();
        AgencySpecification spec = agencySpecification();
        spec.agencyId = agencyToUpdate.id().toString();
        when(agencyRepository.get(agencyToUpdate.id())).thenReturn(Optional.of(agencyToUpdate));
        doAnswer(invocation -> {
            Agency agency = (Agency) invocation.getArguments()[0];

            assertAgencyEqualsToSpec(spec, agency);
            assertEquals(spec.phone, agency.phone().toString());
            assertEquals(spec.email, agency.email().toString());

            return null;
        }).when(agencyRepository).save(agencyToUpdate);

        agencyService.updateAgency(agencyToUpdate.id(), spec);

        verify(agencyRepository).get(agencyToUpdate.id());
        verify(agencyRepository).save(agencyToUpdate);
        verifyNoMoreInteractions(agencyRepository);
    }

    @Test
    public void testUpdateAgencyAllFieldsBlankOrNull() throws Exception {
        Agency agencyToUpdate = randomAgency();
        AgencySpecification spec = new AgencySpecification();
        spec.agencyId = agencyToUpdate.id().toString();
        spec.name = "";
        spec.website = "";
        spec.phone = "";
        spec.email = "";
        when(agencyRepository.get(agencyToUpdate.id())).thenReturn(Optional.of(agencyToUpdate));
        doAnswer(invocation -> {
            Agency agency = (Agency) invocation.getArguments()[0];

            assertEquals(agencyToUpdate.id(), agency.id());
            assertEquals(agencyToUpdate.name(), agency.name());
            assertEquals(agencyToUpdate.website(), agency.website());
            assertEquals(agencyToUpdate.location(), agency.location());
            assertEquals(agencyToUpdate.timeZoneOffset(), agency.timeZoneOffset());

            return null;
        }).when(agencyRepository).save(agencyToUpdate);

        agencyService.updateAgency(agencyToUpdate.id(), spec);

        verify(agencyRepository).get(agencyToUpdate.id());
        verify(agencyRepository).save(agencyToUpdate);
        verifyNoMoreInteractions(agencyRepository);
    }

    @Test(expected = ServiceException.class)
    public void testUpdateAgencyNotFound() {
        final AgencyId agencyId = AgencyId.of("agencyId");
        when(agencyRepository.get(agencyId)).thenReturn(Optional.empty());

        agencyService.updateAgency(agencyId, agencySpecification());

        verify(agencyRepository).get(agencyId);
        verifyNoMoreInteractions(agencyRepository);
    }

    @Test
    public void testDeleteAgency() {
        final AgencyId agencyId = AgencyId.of("agencyId");
        when(agencyRepository.remove(agencyId)).thenReturn(true);

        boolean deleteResult = agencyService.deleteAgency(agencyId);

        assertTrue(deleteResult);
        verify(agencyRepository).remove(agencyId);
        verifyNoMoreInteractions(agencyRepository);
    }

    private void assertAgencyEqualsToSpec(AgencySpecification spec, Agency agency) {
        assertEquals(spec.agencyId, agency.id().toString());
        assertEquals(spec.name, agency.name());
        assertEquals(spec.website, agency.website().toString());
        assertEquals(spec.locationLatitude, agency.location().latitude(), 0.0);
        assertEquals(spec.locationLongitude, agency.location().longitude(), 0.0);
        assertEquals(spec.timeZoneOffset, agency.timeZoneOffset().toString());
    }

    private Agency randomAgency() throws Exception {
        int aId = ThreadLocalRandom.current().nextInt(100, 1000);

        AgencyId agencyId = AgencyId.of("agency" + aId);
        return new Agency(agencyId, "name", new URL("http://google.com"),
                ZoneOffset.UTC, Location.of(-20, 20));
    }

    private AgencySpecification agencySpecification() {
        AgencySpecification spec = new AgencySpecification();
        spec.agencyId = "agencyId";
        spec.name = "name";
        spec.website = "http://google.com";
        spec.phone = "+123 123456";
        spec.email = "email@mail.ru";
        spec.locationLatitude = 30f;
        spec.locationLongitude = 30f;
        spec.timeZoneOffset = "+10:00";
        return spec;
    }
}
