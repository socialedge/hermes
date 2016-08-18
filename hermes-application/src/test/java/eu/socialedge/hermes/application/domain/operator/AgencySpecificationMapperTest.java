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

import eu.socialedge.hermes.application.domain.SpecificationMapperException;
import eu.socialedge.hermes.application.domain.operator.dto.AgencySpecification;
import eu.socialedge.hermes.application.domain.operator.dto.AgencySpecificationMapper;
import eu.socialedge.hermes.domain.contact.Email;
import eu.socialedge.hermes.domain.contact.Phone;
import eu.socialedge.hermes.domain.geo.Location;
import eu.socialedge.hermes.domain.operator.Agency;
import eu.socialedge.hermes.domain.operator.AgencyId;

import org.junit.Before;
import org.junit.Test;

import java.net.URL;
import java.time.ZoneOffset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class AgencySpecificationMapperTest {

    private Agency agency;
    private AgencySpecification data;

    private AgencySpecificationMapper agencyDataMapper = new AgencySpecificationMapper();

    @Before
    public void setUp() throws Exception {
        agency = new Agency(AgencyId.of("agencyId"), "name",
                new URL("http://google.com"), ZoneOffset.UTC, Location.of(10, 10),
                Phone.of("+1 1111111111111"), Email.of("email@mail.com"));

        data = new AgencySpecification();
        data.id = "agencyId";
        data.name = "name";
        data.website = "http://google.com";
        data.timeZoneOffset = "-18:00";
        data.location.longitude = 10f;
        data.location.latitude = 10f;
        data.phone = "+1 11111111111";
        data.email = "mail@mail.ru";
    }

    @Test
    public void testToDataShouldMapAllValuesIfAllOfThemAreNotNull() throws Exception {
        AgencySpecification data = agencyDataMapper.toDto(agency);

        assertEquals(agency.id().toString(), data.id);
        assertEquals(agency.name(), data.name);
        assertEquals(agency.website().toString(), data.website);
        assertEquals(agency.location().latitude(), data.location.latitude, 0.0);
        assertEquals(agency.location().longitude(), data.location.longitude, 0.0);
        assertEquals(agency.timeZone().toString(), data.timeZoneOffset);
        assertEquals(agency.phone().number(), data.phone);
        assertEquals(agency.email().address(), data.email);
    }

    @Test
    public void testToDataShouldLeaveNullsForNullValues() throws Exception {
        agency.email(null);
        agency.phone(null);

        AgencySpecification data = agencyDataMapper.toDto(agency);

        assertEquals(agency.id().toString(), data.id);
        assertEquals(agency.name(), data.name);
        assertEquals(agency.website().toString(), data.website);
        assertEquals(agency.location().latitude(), data.location.latitude, 0.0);
        assertEquals(agency.location().longitude(), data.location.longitude, 0.0);
        assertEquals(agency.timeZone().toString(), data.timeZoneOffset);
        assertNull(data.phone);
        assertNull(data.email);
    }

    @Test
    public void testFromDataShouldMapAllValuesIfAllOfThemAreNotNull() {

        Agency agency = agencyDataMapper.fromDto(data);

        assertEquals(data.id, agency.id().toString());
        assertEquals(data.name, agency.name(), data.name);
        assertEquals(data.website, agency.website().toString());
        assertEquals(data.location.latitude, agency.location().latitude(), 0.0);
        assertEquals(data.location.longitude, agency.location().longitude(), 0.0);
        assertEquals(data.timeZoneOffset, agency.timeZone().toString());
        assertEquals(data.phone, agency.phone().number());
        assertEquals(data.email, agency.email().address());
    }

    @Test(expected = SpecificationMapperException.class)
    public void testFromDataShouldThrowExceptionIfWebsiteIsInvalid() {
        data.website = "invalid";
        agencyDataMapper.fromDto(data);
    }
}
