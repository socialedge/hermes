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
import eu.socialedge.hermes.domain.operator.AgencyRepository;

import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZoneOffset;
import java.util.Collection;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import static eu.socialedge.hermes.util.Strings.isNotBlank;
import static java.util.Objects.isNull;

@Component
public class OperatorService {

    private final AgencyRepository agencyRepository;

    @Inject
    public OperatorService(AgencyRepository agencyRepository) {
        this.agencyRepository = agencyRepository;
    }

    public Collection<Agency> fetchAllAgencies() {
        return agencyRepository.list();
    }

    public Agency fetchAgency(AgencyId agencyId) {
        return agencyRepository.get(agencyId).orElseThrow(()
                    -> new NotFoundException("Agency not found. Id = " + agencyId));
    }

    public void createAgency(AgencySpecification spec) {
        AgencyId agencyId = AgencyId.of(spec.agencyId);
        String name = spec.name;
        URL website = url(spec.website);
        ZoneOffset zoneOffset = ZoneOffset.of(spec.timeZoneOffset);
        Location location = Location.of(spec.locationLatitude, spec.locationLongitude);
        Phone phone = !isNull(spec.phone) ? Phone.of(spec.phone) : null;
        Email email = !isNull(spec.email) ? Email.of(spec.email) : null;

        Agency agency = new Agency(agencyId, name, website, zoneOffset, location, phone, email);

        agencyRepository.add(agency);
    }

    public void updateAgency(AgencyId agencyId, AgencySpecification spec) {
        Agency persistedAgency = fetchAgency(agencyId);

        if (!isNull(spec.timeZoneOffset))
            persistedAgency.timeZone(ZoneOffset.of(spec.timeZoneOffset));

        if (isNotBlank(spec.name))
            persistedAgency.name(spec.name);

        if (isNotBlank(spec.website))
            persistedAgency.website(url(spec.website));

        if (!isNull(spec.locationLatitude) && !isNull(spec.locationLongitude))
            persistedAgency.location(Location.of(spec.locationLatitude, spec.locationLongitude));

        if (isNotBlank(spec.phone))
            persistedAgency.phone(Phone.of(spec.phone));

        if (isNotBlank(spec.email))
            persistedAgency.email(Email.of(spec.email));

        agencyRepository.update(persistedAgency);
    }

    public void deleteAgency(AgencyId agencyId) {
        boolean wasRemoved = agencyRepository.remove(agencyId);
        if (!wasRemoved)
            throw new NotFoundException("Failed to find agency to delete. Id = " + agencyId);
    }

    private static URL url(String rawUrl) {
        try {
            return new URL(rawUrl);
        } catch (MalformedURLException e) {
            throw new BadRequestException("Failed to parse URL = " + rawUrl, e);
        }
    }
}
