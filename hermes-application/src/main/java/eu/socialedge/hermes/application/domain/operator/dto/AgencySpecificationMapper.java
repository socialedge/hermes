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
package eu.socialedge.hermes.application.domain.operator.dto;

import eu.socialedge.hermes.application.domain.SpecificationMapper;
import eu.socialedge.hermes.application.domain.SpecificationMapperException;
import eu.socialedge.hermes.domain.contact.Email;
import eu.socialedge.hermes.domain.contact.Phone;
import eu.socialedge.hermes.domain.geo.Location;
import eu.socialedge.hermes.domain.operator.Agency;
import eu.socialedge.hermes.domain.operator.AgencyId;

import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZoneOffset;

import static eu.socialedge.hermes.util.Values.isNotNull;


@Component
public class AgencySpecificationMapper implements SpecificationMapper<AgencySpecification, Agency> {

    public AgencySpecification toDto(Agency agency) {
        AgencySpecification data = new AgencySpecification();

        data.agencyId = agency.id().toString();
        data.name = agency.name();
        data.website = agency.website().toString();

        Location agencyLocation = agency.location();
        if (isNotNull(agencyLocation)) {
            data.location.latitude = agencyLocation.latitude();
            data.location.longitude = agencyLocation.longitude();
        }

        data.email = isNotNull(agency.email()) ? agency.email().address() : null;
        data.phone = isNotNull(agency.phone()) ? agency.phone().number() : null;

        data.timeZoneOffset = agency.timeZone().toString();

        return data;
    }

    public Agency fromDto(AgencySpecification data) {
        AgencyId agencyId = AgencyId.of(data.agencyId);
        URL website = url(data.website);
        ZoneOffset zoneOffset = ZoneOffset.of(data.timeZoneOffset);
        Location location = Location.of(data.location.latitude, data.location.longitude);
        Phone phone = isNotNull(data.phone) ? Phone.of(data.phone) : null;
        Email email = isNotNull(data.email) ? Email.of(data.email) : null;

        return new Agency(agencyId, data.name, website, zoneOffset, location, phone,  email);
    }

    private static URL url(String rawUrl) {
        try {
            return new URL(rawUrl);
        } catch (MalformedURLException e) {
            throw new SpecificationMapperException("Failed to parse URL = " + rawUrl, e);
        }
    }
}
