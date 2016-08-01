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
package eu.socialedge.hermes.infrastructure.persistence.v2.jpa.repository.mapping;

import eu.socialedge.hermes.domain.v2.operator.Agency;
import eu.socialedge.hermes.domain.v2.operator.Email;
import eu.socialedge.hermes.domain.v2.shared.geo.Location;
import eu.socialedge.hermes.domain.v2.operator.Phone;
import eu.socialedge.hermes.infrastructure.persistence.v2.jpa.entity.JpaAgency;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZoneOffset;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
public class AgencyEntityMapper implements EntityMapper<Agency, JpaAgency> {

    private final LocationEntityMapper locationEntityMapper;

    @Inject
    public AgencyEntityMapper(LocationEntityMapper locationEntityMapper) {
        this.locationEntityMapper = locationEntityMapper;
    }

    @Override
    public JpaAgency mapToEntity(Agency agency) {
        JpaAgency entity = new JpaAgency();

        entity.agencyId(agency.agencyId().toString());

        entity.name(agency.name());
        entity.website(agency.website().toString());

        entity.timeZone(agency.timeZoneOffset().getTotalSeconds());
        entity.location(locationEntityMapper.mapToEntity(agency.location()));

        if (!isNull(agency.phone()))
            entity.phone(agency.phone().toString());
        if (!isNull(agency.email()))
            entity.email(agency.email().toString());

        return entity;
    }

    @Override
    public Agency mapToDomain(JpaAgency jpaAgency) {
        String agencyId = jpaAgency.agencyId();

        String name = jpaAgency.name();
        URL website;
        try {
            website = new URL(jpaAgency.website());
        } catch (MalformedURLException e) {
            throw new EntityMappingException("Failed to map JPA entity to domain Agency object", e);
        }

        ZoneOffset zoneOffset = ZoneOffset.ofTotalSeconds(jpaAgency.timeZone());
        Location location = locationEntityMapper.mapToDomain(jpaAgency.location());

        Agency agency = new Agency(agencyId, name, website, zoneOffset, location);

        if (!isBlank(jpaAgency.email()))
            agency.email(new Email(jpaAgency.email()));
        if (!isBlank(jpaAgency.phone()))
            agency.phone(new Phone(jpaAgency.phone()));

        return agency;
    }
}
