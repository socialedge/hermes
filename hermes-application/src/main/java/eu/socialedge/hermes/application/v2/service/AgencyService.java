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
package eu.socialedge.hermes.application.v2.service;

import eu.socialedge.hermes.application.v2.resource.spec.AgencySpecification;
import eu.socialedge.hermes.domain.v2.geo.Location;
import eu.socialedge.hermes.domain.v2.operator.Agency;
import eu.socialedge.hermes.domain.v2.operator.AgencyId;
import eu.socialedge.hermes.domain.v2.operator.AgencyRepository;
import eu.socialedge.hermes.domain.v2.operator.Email;
import eu.socialedge.hermes.domain.v2.operator.Phone;

import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Optional;

import javax.inject.Inject;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
public class AgencyService {
    private final AgencyRepository agencyRepository;

    @Inject
    public AgencyService(AgencyRepository agencyRepository) {
        this.agencyRepository = agencyRepository;
    }

    public Collection<Agency> fetchAllAgencies() {
        return agencyRepository.list();
    }

    public Optional<Agency> fetchAgency(AgencyId agencyId) {
        return agencyRepository.get(agencyId);
    }

    public void createAgency(AgencySpecification spec) {
        AgencyId agencyId = AgencyId.of(spec.agencyId);
        String name = spec.name;
        URL website = url(spec.website);
        ZoneOffset zoneOffset = ZoneOffset.ofHours(spec.timeZoneOffset);
        Location location = Location.of(spec.locationLatitude, spec.locationLongitude);
        Phone phone = !isNull(spec.phone) ? Phone.of(spec.phone) : null;
        Email email = !isNull(spec.email) ? Email.of(spec.email) : null;

        Agency agency = new Agency(agencyId, name, website, zoneOffset, location, phone, email);

        agencyRepository.store(agency);
    }

    public void updateAgency(AgencySpecification spec) {
        AgencyId agencyId = AgencyId.of(spec.agencyId);

        Optional<Agency> persistedAgencyOpt = agencyRepository.get(agencyId);
        if (!persistedAgencyOpt.isPresent())
            throw new ServiceException("Failed to find Agency to update. Id = " + agencyId);

        Agency persistedAgency = persistedAgencyOpt.get();

        if (!isNull(spec.timeZoneOffset))
            persistedAgency.timeZoneOffset(ZoneOffset.ofHours(spec.timeZoneOffset));

        if (isNotBlank(spec.name))
            persistedAgency.name(spec.name);

        if (!isNotBlank(spec.website))
            persistedAgency.website(url(spec.website));

        if (!isNull(spec.locationLatitude) && !isNull(spec.locationLongitude))
            persistedAgency.location(Location.of(spec.locationLatitude, spec.locationLongitude));

        if (!isNotBlank(spec.phone))
            persistedAgency.phone(Phone.of(spec.phone));

        if (!isNotBlank(spec.email))
            persistedAgency.email(Email.of(spec.email));

        agencyRepository.store(persistedAgency);
    }

    public boolean deleteAgency(AgencyId agencyId) {
        return agencyRepository.remove(agencyId);
    }

    private static URL url(String rawUrl) {
        try {
            return new URL(rawUrl);
        } catch (MalformedURLException e) {
            throw new ServiceException("Failed to parse URL = " + rawUrl, e);
        }
    }
}
