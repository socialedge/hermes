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

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "agencies")
public class JpaAgency {

    @Id
    @Column(name = "agency_id")
    private String agencyId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "website", nullable = false)
    private String website;

    @Column(name = "time_zone", nullable = false)
    private String timeZone;

    @Embedded
    private JpaLocation location;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    public JpaAgency() {}

    public String agencyId() {
        return agencyId;
    }

    public void agencyId(String agencyId) {
        this.agencyId = agencyId;
    }

    public String name() {
        return name;
    }

    public void name(String name) {
        this.name = name;
    }

    public String website() {
        return website;
    }

    public void website(String website) {
        this.website = website;
    }

    public String timeZone() {
        return timeZone;
    }

    public void timeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public JpaLocation location() {
        return location;
    }

    public void location(JpaLocation location) {
        this.location = location;
    }

    public String phone() {
        return phone;
    }

    public void phone(String phone) {
        this.phone = phone;
    }

    public String email() {
        return email;
    }

    public void email(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JpaAgency)) return false;
        JpaAgency jpaAgency = (JpaAgency) o;
        return Objects.equals(agencyId, jpaAgency.agencyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(agencyId);
    }
}
