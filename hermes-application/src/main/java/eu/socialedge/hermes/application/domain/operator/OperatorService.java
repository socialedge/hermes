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

import eu.socialedge.hermes.domain.operator.Agency;
import eu.socialedge.hermes.domain.operator.AgencyId;
import eu.socialedge.hermes.domain.operator.AgencyRepository;
import eu.socialedge.hermes.domain.operator.dto.AgencySpecification;
import eu.socialedge.hermes.domain.operator.dto.AgencySpecificationMapper;
import eu.socialedge.hermes.domain.contact.Email;
import eu.socialedge.hermes.domain.contact.Phone;
import eu.socialedge.hermes.domain.geo.Location;

import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZoneOffset;
import java.util.Collection;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import static eu.socialedge.hermes.util.Strings.isNotBlank;
import static eu.socialedge.hermes.util.Values.isNotNull;

@Component
public class OperatorService {

    private final AgencyRepository agencyRepository;
    private final AgencySpecificationMapper agencySpecMapper;

    @Inject
    public OperatorService(AgencyRepository agencyRepository,
                           AgencySpecificationMapper agencySpecMapper) {
        this.agencyRepository = agencyRepository;
        this.agencySpecMapper = agencySpecMapper;
    }

    public Collection<Agency> fetchAllAgencies() {
        return agencyRepository.list();
    }

    public Agency fetchAgency(AgencyId agencyId) {
        return agencyRepository.get(agencyId).orElseThrow(()
                -> new NotFoundException("Agency not found. Id = " + agencyId));
    }

    public void createAgency(AgencySpecification data) {
        agencyRepository.add(agencySpecMapper.fromDto(data));
    }

    public void updateAgency(AgencyId agencyId, AgencySpecification spec) {
        Agency persistedAgency = fetchAgency(agencyId);

        if (isNotNull(spec.timeZoneOffset))
            persistedAgency.timeZone(ZoneOffset.of(spec.timeZoneOffset));

        if (isNotBlank(spec.name))
            persistedAgency.name(spec.name);

        if (isNotBlank(spec.website))
            persistedAgency.website(url(spec.website));

        if (isNotNull(spec.location.latitude) && isNotNull(spec.location.longitude))
            persistedAgency.location(Location.of(spec.location.latitude, spec.location.longitude));

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
