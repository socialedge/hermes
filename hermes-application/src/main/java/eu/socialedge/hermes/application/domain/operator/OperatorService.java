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

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import static eu.socialedge.hermes.util.Strings.isNotBlank;
import static java.util.Objects.nonNull;

@Component
public class OperatorService {

    private final AgencyRepository agencyRepository;
    private final AgencyMapper agencyMapper;

    @Inject
    public OperatorService(AgencyRepository agencyRepository, AgencyMapper agencyMapper) {
        this.agencyRepository = agencyRepository;
        this.agencyMapper = agencyMapper;
    }

    public Collection<AgencyData> fetchAllAgencies() {
        return agencyRepository.list().stream().map(agencyMapper::toDto).collect(Collectors.toList());
    }

    public AgencyData fetchAgency(AgencyId agencyId) {
        Agency agency = agencyRepository.get(agencyId).orElseThrow(()
                -> new NotFoundException("Agency not found. Id = " + agencyId));

        return agencyMapper.toDto(agency);
    }

    public void createAgency(AgencyData data) {
        agencyRepository.add(agencyMapper.fromDto(data));
    }

    public void updateAgency(AgencyId agencyId, AgencyData data) {
        AgencyData persistedAgencyData = fetchAgency(agencyId);

        if (nonNull(data.timeZoneOffset)) {
            persistedAgencyData.timeZoneOffset = data.timeZoneOffset;
        }

        if (isNotBlank(data.name)) {
            persistedAgencyData.name = data.name;
        }

        if (isNotBlank(data.website)) {
            persistedAgencyData.website = data.website;
        }

        if (nonNull(data.locationLatitude)) {
            persistedAgencyData.locationLatitude = data.locationLatitude;
        }

        if (nonNull(data.locationLongitude)) {
            persistedAgencyData.locationLongitude = data.locationLongitude;
        }

        if (isNotBlank(data.phone)) {
            persistedAgencyData.phone = data.phone;
        }

        if (isNotBlank(data.email)) {
            persistedAgencyData.email = data.email;
        }

        agencyRepository.update(agencyMapper.fromDto(persistedAgencyData));
    }

    public void deleteAgency(AgencyId agencyId) {
        boolean wasRemoved = agencyRepository.remove(agencyId);
        if (!wasRemoved)
            throw new NotFoundException("Failed to find agency to delete. Id = " + agencyId);
    }

}
