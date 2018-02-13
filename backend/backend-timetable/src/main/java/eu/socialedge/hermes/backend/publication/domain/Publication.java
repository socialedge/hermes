/*
 * Hermes - The Municipal Transport Timetable System
 * Copyright (c) 2016-2018 SocialEdge
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
package eu.socialedge.hermes.backend.publication.domain;

import eu.socialedge.hermes.backend.schedule.domain.Schedule;
import eu.socialedge.hermes.backend.transit.domain.infra.Station;
import eu.socialedge.hermes.backend.transit.domain.service.Line;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

@Document
@ToString
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
public class Publication {

    @Id
    private ObjectId id;

    @Getter
    private String name;

    @Getter
    private LocalDate date;

    @Getter
    private byte[] file;

    @DBRef @Getter
    private List<Schedule> schedules = new ArrayList<>();

    @DBRef @Getter
    private Line line;

    @DBRef @Getter
    private Station station;

    public Publication(String id, String name, LocalDate date, byte[] file, List<Schedule> schedules, Line line, Station station) {
        this.id = isNotBlank(id) ? new ObjectId(id) : ObjectId.get();
        this.name = notBlank(name);
        this.date = notNull(date);
        this.file = notNull(file);
        this.schedules = notEmpty(schedules);
        if (line == null && station == null) {
            throw new IllegalArgumentException("At least one of [line, station] must be specified for publication");
        }
        this.line = line;
        this.station = station;
    }

    public Publication(String name, LocalDate date, byte[] file, List<Schedule> schedules, Line line, Station station) {
        this(null, name, date, file, schedules, line, station);
    }

    public Publication(String name, byte[] file, List<Schedule> schedules, Line line, Station station) {
        this(name, LocalDate.now(), file, schedules, line, station);
    }

    public Publication(String name, byte[] file, List<Schedule> schedules, Station station) {
        this(name, file, schedules, null, station);
    }

    public Publication(String name, byte[] file, List<Schedule> schedules, Line line) {
        this(name, file, schedules, line, null);
    }

    public String getId() {
        return id.toHexString();
    }
}
