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

import eu.socialedge.hermes.application.domain.operator.dto.AgencySpecification;
import eu.socialedge.hermes.application.domain.operator.dto.AgencySpecificationMapper;
import eu.socialedge.hermes.domain.operator.Agency;
import eu.socialedge.hermes.domain.operator.AgencyId;
import eu.socialedge.hermes.domain.operator.AgencyRepository;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import static eu.socialedge.hermes.util.Strings.isNotBlank;
import static eu.socialedge.hermes.util.Values.isNotNull;

@Component
public class OperatorService {

    private final AgencyRepository agencyRepository;
    private final AgencySpecificationMapper agencySpecificationMapper;

    @Inject
    public OperatorService(AgencyRepository agencyRepository, AgencySpecificationMapper agencySpecificationMapper) {
        this.agencyRepository = agencyRepository;
        this.agencySpecificationMapper = agencySpecificationMapper;
    }

    public Collection<AgencySpecification> fetchAllAgencies() {
        return agencyRepository.list().stream().map(agencySpecificationMapper::toDto).collect(Collectors.toList());
    }

    public AgencySpecification fetchAgency(AgencyId agencyId) {
        Agency agency = agencyRepository.get(agencyId).orElseThrow(()
                -> new NotFoundException("Agency not found. Id = " + agencyId));

        return agencySpecificationMapper.toDto(agency);
    }

    public void createAgency(AgencySpecification data) {
        agencyRepository.add(agencySpecificationMapper.fromDto(data));
    }

    public void updateAgency(AgencyId agencyId, AgencySpecification data) {
        AgencySpecification persistedAgencySpecification = fetchAgency(agencyId);

        if (isNotNull(data.timeZoneOffset)) {
            persistedAgencySpecification.timeZoneOffset = data.timeZoneOffset;
        }

        if (isNotBlank(data.name)) {
            persistedAgencySpecification.name = data.name;
        }

        if (isNotBlank(data.website)) {
            persistedAgencySpecification.website = data.website;
        }

        if (isNotNull(data.location.latitude)) {
            persistedAgencySpecification.location.latitude = data.location.latitude;
        }

        if (isNotNull(data.location.longitude)) {
            persistedAgencySpecification.location.longitude = data.location.longitude;
        }

        if (isNotBlank(data.phone)) {
            persistedAgencySpecification.phone = data.phone;
        }

        if (isNotBlank(data.email)) {
            persistedAgencySpecification.email = data.email;
        }

        agencyRepository.update(agencySpecificationMapper.fromDto(persistedAgencySpecification));
    }

    public void deleteAgency(AgencyId agencyId) {
        boolean wasRemoved = agencyRepository.remove(agencyId);
        if (!wasRemoved)
            throw new NotFoundException("Failed to find agency to delete. Id = " + agencyId);
    }

}
