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
package eu.socialedge.hermes.infrastructure.persistence.v2.jpa.entity;

import eu.socialedge.hermes.domain.v2.infrastructure.Station;
import eu.socialedge.hermes.domain.v2.infrastructure.TransportType;
import eu.socialedge.hermes.domain.v2.operator.Agency;
import eu.socialedge.hermes.domain.v2.operator.Email;
import eu.socialedge.hermes.domain.v2.operator.Location;
import eu.socialedge.hermes.domain.v2.operator.Phone;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZoneOffset;
import java.util.Set;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class EntityMapper {
    
    public static JpaAgency mapAgencyToEntity(Agency agency) {
        return new JpaAgency() {{
            agencyId(agency.agencyId().toString());

            name(agency.name());
            website(agency.website().toString());

            timeZone(agency.timeZoneOffset().getTotalSeconds());
            location(mapLocationToEntity(agency.location()));

            if (!isNull(agency.phone()))
                phone(agency.phone().toString());
            if (!isNull(agency.email()))
                email(agency.email().toString());
        }};
    }

    public static Agency mapEntityToAgency(JpaAgency jpaAgency) throws MalformedURLException {
        String agencyId = jpaAgency.agencyId();

        String name = jpaAgency.name();
        URL website = new URL(jpaAgency.website());

        ZoneOffset zoneOffset = ZoneOffset.ofTotalSeconds(jpaAgency.timeZone());
        Location location = mapEntityToLocation(jpaAgency.location());

        Agency agency = new Agency(agencyId, name, website, zoneOffset, location);

        if (!isBlank(jpaAgency.email()))
            agency.email(new Email(jpaAgency.email()));
        if (!isBlank(jpaAgency.phone()))
            agency.phone(new Phone(jpaAgency.phone()));

        return agency;
    }

    public static JpaStation mapStationToEntity(Station station) {
        return new JpaStation() {{
            stationId(station.stationId().toString());
            name(station.name());

            location(mapLocationToEntity(station.location()));
            transportTypes(station.transportTypes());
        }};
    }

    public static Station mapEntityToStation(JpaStation jpaStation) {
        String stationId = jpaStation.stationId();
        String name = jpaStation.name();
        Location location = mapEntityToLocation(jpaStation.location());
        Set<TransportType> transportTypes = jpaStation.transportTypes();

        return new Station(stationId, name, location, transportTypes);
    }

    private static JpaLocation mapLocationToEntity(Location location) {
        return new JpaLocation() {{
            latitude(location.latitude());
            longitude(location.longitude());
        }};
    }

    private static Location mapEntityToLocation(JpaLocation jpaLocation) {
        return Location.of(jpaLocation.latitude(), jpaLocation.longitude());
    }
}
