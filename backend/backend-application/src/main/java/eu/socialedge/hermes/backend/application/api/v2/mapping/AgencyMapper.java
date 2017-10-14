/*
 * Hermes - The Municipal Transport Timetable System
 * Copyright (c) 2016-2017 SocialEdge
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

package eu.socialedge.hermes.backend.application.api.v2.mapping;

import eu.socialedge.hermes.backend.application.api.dto.AgencyDTO;
import eu.socialedge.hermes.backend.transit.domain.provider.Agency;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;

@Component
public class AgencyMapper implements Mapper<Agency, AgencyDTO> {

    @Override
    public AgencyDTO toDTO(Agency agency) {
        if (agency == null)
            return null;

        return new AgencyDTO()
            .id(agency.getId())
            .name(agency.getName())
            .language(agency.getLanguage().name())
            .phone(agency.getPhone())
            .timeZone(agency.getTimeZone().getId())
            .url(agency.getUrl() != null ? agency.getUrl().toString() : null);
    }

    @Override
    public Agency toDomain(AgencyDTO dto) {
        if (dto == null)
            return null;

        try {
            return new Agency.Builder()
                .id(dto.getId())
                .name(dto.getName())
                .language(dto.getLanguage())
                .phone(dto.getPhone())
                .timeZone(dto.getTimeZone())
                .url(dto.getUrl())
                .build();
        } catch (MalformedURLException e) {
            throw new MappingException("Failed to map dto to agency entity", e);
        }
    }
}
