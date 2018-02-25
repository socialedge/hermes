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

package eu.socialedge.hermes.backend.application.api.util;

import lombok.val;
import org.junit.Test;

import static org.junit.Assert.*;

public class FiltersParserTest {

    @Test
    public void shouldParseOneFilterFromCsvFilterStringCorrectly() {
        val param = "param";
        val value = "value";
        val paramValueCsv = param + "," + value;

        val filterOpt = FiltersParser.from(paramValueCsv);
        assertTrue(filterOpt.isPresent());

        val filter = filterOpt.get();
        assertEquals(param, filter.field());
        assertEquals(value, filter.value());
    }

    @Test
    public void shouldParseMultipleFiltersFromCsvDelimitedFilterStringCorrectly() {
        val param1 = "param";
        val value1 = "regexp";

        val param2 = "param2";
        val value2 = "value2";

        val paramsValuesCsv =
            param1 + "," + value1 + ";" +
            param2 + "," + value2;

        val filters = FiltersParser.fromMultiple(paramsValuesCsv);
        assertFalse(filters.isEmpty());

        assertEquals(param1, filters.get(0).field());
        assertEquals(value1, filters.get(0).value());

        assertEquals(param2, filters.get(1).field());
        assertEquals(value2, filters.get(1).value());
    }
}
