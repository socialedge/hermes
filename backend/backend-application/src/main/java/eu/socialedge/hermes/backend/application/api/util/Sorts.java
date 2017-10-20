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

import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

/**
 * {@code Sorts} provides convenient util methods for parsing
 * {@code ?sort} query param values in Spring Data Rest like
 * format for being passed to Spring Data Repositories of
 * {@link PagingAndSortingRepository} type.
 * <p>
 * For example: {@code ?sort=name,asc}. Default sorting direction
 * is DESC so that {@code ?sort=name} will be sorted by name DESC
 *
 * @since Hermes 3.0
 */
public final class Sorts {

    private static final String SEARCH_PROP_DELIMITER = ",";

    private Sorts() {
        throw new AssertionError("No instance for you");
    }

    /**
     * Parses singe CSV sort value (e.g. {@code property,asc}) into
     * {@link Sort} respecting default sorting direction (DESC)
     *
     * @param sortCsv CSV sort query param value
     * @return optional {@link Sort}
     */
    public static Optional<Sort> from(String sortCsv) {
        if (!isParsable(sortCsv))
            return Optional.empty();

        return Optional.of(new Sort(parseSortOrder(sortCsv)));
    }

    /**
     * Parses CSV sort values (e.g. {@code property,asc}) into {@link Sort}
     * respecting default sorting direction (DESC)
     *
     * @param sortCsv CSV sort query param values
     * @return optional {@link Sort}
     */
    public static Optional<Sort> from(String[] sortCsv) {
        if (stream(sortCsv).noneMatch(Sorts::isParsable))
            return Optional.empty();

        return stream(sortCsv)
            .filter(Sorts::isParsable)
            .map(Sorts::parseSortOrder)
            .collect(collectingAndThen(toList(),
                sortOrders -> Optional.of(new Sort(sortOrders))));
    }

    private static Sort.Order parseSortOrder(String sortCsv) {
        val sortPropDir = sortCsv.split(SEARCH_PROP_DELIMITER);

        if (sortPropDir.length == 0)
            throw new IllegalArgumentException("String doest match prop" + SEARCH_PROP_DELIMITER + "direction pattern");

        val sortProp = sortPropDir[0];
        val sortDirStr  = sortPropDir.length > 1 ? sortPropDir[1] : (String) null;

        val sortDir = Sort.Direction.fromStringOrNull(sortDirStr);

        return new Sort.Order(sortDir, sortProp);
    }

    private static boolean isParsable(String sorting) {
        return StringUtils.countMatches(sorting, SEARCH_PROP_DELIMITER) == 1;
    }
}
