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

import eu.socialedge.hermes.application.domain.DtoMapper;
import eu.socialedge.hermes.domain.contact.Email;
import eu.socialedge.hermes.domain.contact.Phone;
import eu.socialedge.hermes.domain.geo.Location;
import eu.socialedge.hermes.domain.operator.Agency;
import eu.socialedge.hermes.domain.operator.AgencyId;
import org.springframework.stereotype.Component;

import javax.ws.rs.BadRequestException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZoneOffset;

import static java.util.Objects.nonNull;

@Component
public class AgencyMapper implements DtoMapper<AgencyData, Agency> {

    public AgencyData toDto(Agency agency) {
        AgencyData data = new AgencyData();

        data.agencyId = agency.id().toString();
        data.name = agency.name();
        data.website = agency.website().toString();

        Location agencyLocation = agency.location();
        if (nonNull(agencyLocation)) {
            data.locationLatitude = agencyLocation.latitude();
            data.locationLongitude = agencyLocation.longitude();
        }

        data.email = nonNull(agency.email()) ? agency.email().address() : null;
        data.phone = nonNull(agency.phone()) ? agency.phone().number() : null;

        data.timeZoneOffset = agency.timeZone().toString();

        return data;
    }

    public Agency fromDto(AgencyData data) {
        AgencyId agencyId = AgencyId.of(data.agencyId);
        URL website = url(data.website);
        ZoneOffset zoneOffset = ZoneOffset.of(data.timeZoneOffset);
        Location location = Location.of(data.locationLatitude, data.locationLongitude);
        Phone phone = nonNull(data.phone) ? Phone.of(data.phone) : null;
        Email email = nonNull(data.email) ? Email.of(data.email) : null;

        return new Agency(agencyId, data.name, website, zoneOffset, location, phone,  email);
    }

    private static URL url(String rawUrl) {
        try {
            return new URL(rawUrl);
        } catch (MalformedURLException e) {
            throw new BadRequestException("Failed to parse URL = " + rawUrl, e);
        }
    }
}
