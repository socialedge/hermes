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

package eu.socialedge.hermes.backend.shared.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.val;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;

/**
 * {@code Filter} option for queries that allows filter results
 * by regexp applied to a certain filed
 */
@Getter @Accessors(fluent = true)
@ToString @EqualsAndHashCode
public class Filter {

    private final String field;

    private final Pattern regexp;

    private Filter(String field, Pattern regexp) {
        this.field = requireNonNull(field);
        this.regexp = requireNonNull(regexp);
    }

    public static Filter from(String field, Pattern regexp) {
        return new Filter(field, regexp);
    }

    public static Filter from(String field, String regexp) {
        val patternRegex = Pattern.compile(regexp, Pattern.UNICODE_CASE);
        return from(field, patternRegex);
    }

    public Criteria asCriteria() {
        return Criteria.where(field).regex(regexp);
    }
}
