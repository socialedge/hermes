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
package eu.socialedge.hermes.domain.v2.operator;

import eu.socialedge.hermes.domain.ext.AggregateRoot;
import eu.socialedge.hermes.domain.v2.shared.geo.Location;

import java.net.URL;
import java.time.ZoneOffset;
import java.util.Objects;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * Represents a public transport company that operates bus/tram/train/trolley
 * services.
 *
 * <p>Agency is uniquely identified by {@link AgencyId} - Legal Entity Identifier.
 * It also has {@link Agency#name}, personal {@link Agency#website}, {@link Agency#timeZoneOffset}
 * offset, {{@link Agency#phone} number, {@link Agency#email} and headquarters
 * geographic {@link Location}</p>
 *
 * @see <a href="https://goo.gl/oyo05p">Google Transit APIs
 * > Static Transit > agency.txt File</a>
 */
@AggregateRoot
public class Agency {

    private final AgencyId agencyId;

    private String name;

    private URL website;

    private ZoneOffset timeZoneOffset;

    private Location location;

    private Phone phone;

    private Email email;

    public Agency(AgencyId agencyId, String name, URL website,
                  ZoneOffset timeZoneOffset, Location location) {
        this.agencyId = notNull(agencyId);
        this.name = notEmpty(name);
        this.website = notNull(website);
        this.timeZoneOffset = notNull(timeZoneOffset);
        this.location = notNull(location);
    }

    public Agency(AgencyId agencyId, String name, URL website, ZoneOffset timeZoneOffset,
                  Location location, Phone phone, Email email) {
        this(agencyId, name, website, timeZoneOffset, location);
        this.phone = phone;
        this.email = email;
    }

    public AgencyId agencyId() {
        return agencyId;
    }

    public ZoneOffset timeZoneOffset() {
        return timeZoneOffset;
    }

    public void timeZoneOffset(ZoneOffset timeZoneOffset) {
        this.timeZoneOffset = notNull(timeZoneOffset);
    }

    public String name() {
        return name;
    }

    public void name(String name) {
        this.name = notBlank(name);
    }

    public URL website() {
        return website;
    }

    public void website(URL website) {
        this.website = notNull(website);
    }

    public Location location() {
        return location;
    }

    public void location(Location location) {
        this.location = notNull(location);
    }

    public Phone phone() {
        return phone;
    }

    public void phone(Phone phone) {
        this.phone = phone;
    }

    public Email email() {
        return email;
    }

    public void email(Email email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Agency)) return false;
        Agency agency = (Agency) o;
        return Objects.equals(agencyId, agency.agencyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(agencyId);
    }

    @Override
    public String toString() {
        return "Agency{" +
                "agencyId=" + agencyId +
                ", name='" + name + '\'' +
                ", website=" + website +
                ", timeZoneOffset=" + timeZoneOffset +
                ", location=" + location +
                ", phone=" + phone +
                ", email=" + email +
                '}';
    }
}
