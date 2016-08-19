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
package eu.socialedge.hermes.domain.operator.dto;

import eu.socialedge.hermes.domain.SpecificationMapper;
import eu.socialedge.hermes.domain.SpecificationMapperException;
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
        AgencySpecification spec = new AgencySpecification();

        spec.id = agency.id().toString();
        spec.name = agency.name();
        spec.website = agency.website().toString();

        Location agencyLocation = agency.location();
        if (isNotNull(agencyLocation)) {
            spec.location.latitude = agencyLocation.latitude();
            spec.location.longitude = agencyLocation.longitude();
        }

        spec.email = isNotNull(agency.email()) ? agency.email().address() : null;
        spec.phone = isNotNull(agency.phone()) ? agency.phone().number() : null;

        spec.timeZoneOffset = agency.timeZone().toString();

        return spec;
    }

    public Agency fromDto(AgencySpecification spec) {
        AgencyId agencyId = AgencyId.of(spec.id);
        URL website = url(spec.website);
        ZoneOffset zoneOffset = ZoneOffset.of(spec.timeZoneOffset);
        Location location = Location.of(spec.location.latitude, spec.location.longitude);
        Phone phone = isNotNull(spec.phone) ? Phone.of(spec.phone) : null;
        Email email = isNotNull(spec.email) ? Email.of(spec.email) : null;

        return new Agency(agencyId, spec.name, website, zoneOffset, location, phone,  email);
    }

    private static URL url(String rawUrl) {
        try {
            return new URL(rawUrl);
        } catch (MalformedURLException e) {
            throw new SpecificationMapperException("Failed to parse URL = " + rawUrl, e);
        }
    }
}
