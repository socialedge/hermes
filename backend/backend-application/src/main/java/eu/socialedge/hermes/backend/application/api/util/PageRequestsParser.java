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

import lombok.experimental.var;
import lombok.val;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static java.util.Objects.nonNull;

/**
 * {@code PageRequestsParser} provides convenient util methods for creating
 * Spring's {@link PageRequest} for Data Repositories paging and sorting.
 *
 * @see SortsParser
 * @since Hermes 3.0
 */
public final class PageRequestsParser {

    private static final int DEFAULT_PAGE_SIZE = 25;

    private PageRequestsParser() {
        throw new AssertionError("No instance for you");
    }

    public static Optional<Pageable> from(Integer size, Integer page, String sorting) {
        if (page == null)
            return Optional.empty();

        val pageNumber = page < 0 ? 0 : page;
        val pageSize = nonNull(size) && size >=0 ? size : DEFAULT_PAGE_SIZE;

        var pageable = SortsParser.from(sorting)
            .map(sort -> new PageRequest(pageNumber, pageSize, sort))
            .orElse(new PageRequest(pageNumber, pageSize));

        return Optional.of(pageable);
    }
}
