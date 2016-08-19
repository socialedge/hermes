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
package eu.socialedge.hermes.domain.operator;

import eu.socialedge.hermes.domain.SpecificationMapperException;
import eu.socialedge.hermes.domain.operator.dto.AgencySpecification;
import eu.socialedge.hermes.domain.operator.dto.AgencySpecificationMapper;
import eu.socialedge.hermes.domain.contact.Email;
import eu.socialedge.hermes.domain.contact.Phone;
import eu.socialedge.hermes.domain.geo.Location;

import org.junit.Before;
import org.junit.Test;

import java.net.URL;
import java.time.ZoneOffset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class AgencySpecificationMapperTest {

    private Agency agency;
    private AgencySpecification spec;

    private AgencySpecificationMapper agencyDataMapper = new AgencySpecificationMapper();

    @Before
    public void setUp() throws Exception {
        agency = new Agency(AgencyId.of("agencyId"), "name",
                new URL("http://google.com"), ZoneOffset.UTC, Location.of(10, 10),
                Phone.of("+1 1111111111111"), Email.of("email@mail.com"));

        spec = new AgencySpecification();
        spec.id = "agencyId";
        spec.name = "name";
        spec.website = "http://google.com";
        spec.timeZoneOffset = "-18:00";
        spec.location.longitude = 10f;
        spec.location.latitude = 10f;
        spec.phone = "+1 11111111111";
        spec.email = "mail@mail.ru";
    }

    @Test
    public void testToDataShouldMapAllValuesIfAllOfThemAreNotNull() throws Exception {
        AgencySpecification spec = agencyDataMapper.toDto(agency);

        assertEquals(agency.id().toString(), spec.id);
        assertEquals(agency.name(), spec.name);
        assertEquals(agency.website().toString(), spec.website);
        assertEquals(agency.location().latitude(), spec.location.latitude, 0.0);
        assertEquals(agency.location().longitude(), spec.location.longitude, 0.0);
        assertEquals(agency.timeZone().toString(), spec.timeZoneOffset);
        assertEquals(agency.phone().number(), spec.phone);
        assertEquals(agency.email().address(), spec.email);
    }

    @Test
    public void testToDataShouldLeaveNullsForNullValues() throws Exception {
        agency.email(null);
        agency.phone(null);

        AgencySpecification spec = agencyDataMapper.toDto(agency);

        assertEquals(agency.id().toString(), spec.id);
        assertEquals(agency.name(), spec.name);
        assertEquals(agency.website().toString(), spec.website);
        assertEquals(agency.location().latitude(), spec.location.latitude, 0.0);
        assertEquals(agency.location().longitude(), spec.location.longitude, 0.0);
        assertEquals(agency.timeZone().toString(), spec.timeZoneOffset);
        assertNull(spec.phone);
        assertNull(spec.email);
    }

    @Test
    public void testFromDataShouldMapAllValuesIfAllOfThemAreNotNull() {

        Agency agency = agencyDataMapper.fromDto(spec);

        assertEquals(spec.id, agency.id().toString());
        assertEquals(spec.name, agency.name(), spec.name);
        assertEquals(spec.website, agency.website().toString());
        assertEquals(spec.location.latitude, agency.location().latitude(), 0.0);
        assertEquals(spec.location.longitude, agency.location().longitude(), 0.0);
        assertEquals(spec.timeZoneOffset, agency.timeZone().toString());
        assertEquals(spec.phone, agency.phone().number());
        assertEquals(spec.email, agency.email().address());
    }

    @Test(expected = SpecificationMapperException.class)
    public void testFromDataShouldThrowExceptionIfWebsiteIsInvalid() {
        spec.website = "invalid";
        agencyDataMapper.fromDto(spec);
    }
}
