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
package eu.socialedge.hermes.domain.infrastructure;

import eu.socialedge.hermes.domain.ext.AggregateRoot;
import org.apache.commons.lang3.Validate;

import javax.persistence.*;
import java.io.Serializable;
import java.net.URL;
import java.util.Objects;

@Entity
@AggregateRoot
@Table(name = "operators")
public class Operator implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "operator_id")
    private int operatorId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "website")
    private URL website;

    @Embedded
    private Position position;

    protected Operator() {}

    public Operator(String name) {
        this.name = Validate.notBlank(name);
    }

    public int getOperatorId() {
        return operatorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Validate.notBlank(name);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = Validate.notBlank(description);
    }

    public URL getWebsite() {
        return website;
    }

    public void setWebsite(URL website) {
        this.website = Validate.notNull(website);
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = Validate.notNull(position);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Operator)) return false;
        Operator operator = (Operator) o;
        return Objects.equals(getName(), operator.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public String toString() {
        return "Operator{" +
                "operatorId=" + operatorId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", website=" + website +
                ", position=" + position +
                '}';
    }
}
