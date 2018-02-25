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

package eu.socialedge.hermes.backend.application.api.util;

import eu.socialedge.hermes.backend.shared.domain.Filter;
import eu.socialedge.hermes.backend.shared.domain.FilteringPagingAndSortingRepository;
import eu.socialedge.hermes.backend.shared.domain.Filters;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

/**
 * {@code FiltersParser} provides convenient util methods for parsing
 * {@code ?filter} query param values for being passed to Spring
 * Data Repositories of {@link FilteringPagingAndSortingRepository} type.
 * <p>
 * For example: {@code ?filter=name,John;sname,Snow}.
 *
 * @since Hermes 3.0
 */
public class FiltersParser {

    private static final String FILTERS_DELIMITER = ";";
    private static final String FILTER_PROP_DELIMITER = ",";
    private static final boolean FILTER_CASE_INSENSITIVE_BY_DEFAULT = true;

    private FiltersParser() {
        throw new AssertionError("No instance for you");
    }

    private static Optional<Filter> parse(String filteringCsv, boolean ignoreCase) {
        if (!isParsable(filteringCsv))
            return Optional.empty();

        val filteringPropRegex = filteringCsv.split(FILTER_PROP_DELIMITER, 2);
        val filteringProp = filteringPropRegex[0];
        val filteringRegexStr = Pattern.quote(filteringPropRegex[1]);

        val filteringRegex = ignoreCase ?
            Pattern.compile(filteringRegexStr, Pattern.CASE_INSENSITIVE)
            : Pattern.compile(filteringRegexStr);

        return Optional.of(Filter.from(filteringProp, filteringRegex));
    }

    /**
     * Parses singe CSV filter value (e.g. {@code property,regexp}) into
     * {@link Filter}
     *
     * @param filteringCsv CSV filter query param value
     * @param ignoreCase whether filter should be case insensitive or not
     * @return optional {@link Filter}
     */
    public static Optional<Filter> from(String filteringCsv, boolean ignoreCase) {
        return parse(filteringCsv, ignoreCase);
    }

    public static Optional<Filter> from(String filteringCsv) {
        return from(filteringCsv, FILTER_CASE_INSENSITIVE_BY_DEFAULT);
    }

    /**
     * Parses multiple CSV filter value (e.g. {@code prop,regexp;prop,regexp}) into
     * {@link Filter}
     *
     * @param filteringCsv CSV filter query param values
     * @param ignoreCase whether filter should be case insensitive or not
     * @return List of {@link Filter}s
     */
    public static Filters fromMultiple(String filteringCsv, boolean ignoreCase) {
        if (!hasMultipleFilters(filteringCsv)) {
            return from(filteringCsv, ignoreCase)
                .map(Filters::new)
                .orElseGet(Filters::emptyFilters);
        }

        return stream(filteringCsv.split(FILTERS_DELIMITER))
            .map(String::trim)
            .map(csv -> from(csv, ignoreCase).orElse(null))
            .filter(Objects::nonNull)
            .collect(Collectors.collectingAndThen(toList(), Filters::new));
    }

    public static Filters fromMultiple(String filteringCsv) {
        return fromMultiple(filteringCsv, FILTER_CASE_INSENSITIVE_BY_DEFAULT);
    }

    private static boolean isParsable(String filtering) {
        return StringUtils.countMatches(filtering, FILTER_PROP_DELIMITER) >= 1;
    }

    private static boolean hasMultipleFilters(String filtering) {
        return StringUtils.countMatches(filtering, FILTERS_DELIMITER) >= 1;
    }
}
