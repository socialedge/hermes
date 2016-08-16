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

import eu.socialedge.hermes.domain.contact.Email;
import eu.socialedge.hermes.domain.contact.Phone;
import eu.socialedge.hermes.domain.geo.Location;
import eu.socialedge.hermes.domain.operator.Agency;
import eu.socialedge.hermes.domain.operator.AgencyId;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.BadRequestException;
import java.net.URL;
import java.time.ZoneOffset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class AgencyDataMapperTest {

    private Agency agency;
    private AgencyData data;

    @Before
    public void setUp() throws Exception {
        agency = new Agency(AgencyId.of("agencyId"), "name",
                new URL("http://google.com"), ZoneOffset.UTC, Location.of(10, 10),
                Phone.of("+1 1111111111111"), Email.of("email@mail.com"));

        data = new AgencyData();
        data.agencyId = "agencyId";
        data.name = "name";
        data.website = "http://google.com";
        data.timeZoneOffset = "-18:00";
        data.locationLongitude = 10f;
        data.locationLatitude = 10f;
        data.phone = "+1 11111111111";
        data.email = "mail@mail.ru";
    }

    @Test
    public void testToDataShouldMapAllValuesIfAllOfThemAreNotNull() throws Exception {
        AgencyData data = AgencyDataMapper.toData(agency);

        assertEquals(agency.id().toString(), data.agencyId);
        assertEquals(agency.name(), data.name);
        assertEquals(agency.website().toString(), data.website);
        assertEquals(agency.location().latitude(), data.locationLatitude, 0.0);
        assertEquals(agency.location().longitude(), data.locationLongitude, 0.0);
        assertEquals(agency.timeZone().toString(), data.timeZoneOffset);
        assertEquals(agency.phone().number(), data.phone);
        assertEquals(agency.email().address(), data.email);
    }

    @Test
    public void testToDataShouldLeaveNullsForNullValues() throws Exception {
        agency.email(null);
        agency.phone(null);

        AgencyData data = AgencyDataMapper.toData(agency);

        assertEquals(agency.id().toString(), data.agencyId);
        assertEquals(agency.name(), data.name);
        assertEquals(agency.website().toString(), data.website);
        assertEquals(agency.location().latitude(), data.locationLatitude, 0.0);
        assertEquals(agency.location().longitude(), data.locationLongitude, 0.0);
        assertEquals(agency.timeZone().toString(), data.timeZoneOffset);
        assertNull(data.phone);
        assertNull(data.email);
    }

    @Test
    public void testFromDataShouldMapAllValuesIfAllOfThemAreNotNull() {

        Agency agency = AgencyDataMapper.fromData(data);

        assertEquals(data.agencyId, agency.id().toString());
        assertEquals(data.name, agency.name(), data.name);
        assertEquals(data.website, agency.website().toString());
        assertEquals(data.locationLatitude, agency.location().latitude(), 0.0);
        assertEquals(data.locationLongitude, agency.location().longitude(), 0.0);
        assertEquals(data.timeZoneOffset, agency.timeZone().toString());
        assertEquals(data.phone, agency.phone().number());
        assertEquals(data.email, agency.email().address());
    }

    @Test(expected = BadRequestException.class)
    public void testFromDataShouldThrowExceptionIfWebsiteIsInvalid() {
        data.website = "invalid";
        AgencyDataMapper.fromData(data);
    }
}
