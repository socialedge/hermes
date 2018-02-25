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

import static java.util.regex.Pattern.quote;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FiltersParserTest {

    @Test
    public void shouldParseOneFilterFromCsvFilterStringCorrectly() {
        val param = "param";
        val regexp = "regexp";
        val paramRegexpCsv = param + "," + regexp;

        val filterOpt = FiltersParser.from(paramRegexpCsv);
        assertTrue(filterOpt.isPresent());

        val filter = filterOpt.get();
        assertEquals(param, filter.field());
        assertEquals(quote(regexp), filter.regexp().toString());
    }

    @Test
    public void shouldParseMultipleFiltersFromCsvDelimitedFilterStringCorrectly() {
        val param1 = "param";
        val regexp1 = "regexp";

        val param2 = "param2";
        val regexp2 = "regexp2";

        val paramsRegexpsCsv =
            param1 + "," + regexp1 + ";" +
            param2 + "," + regexp2;

        val filters = FiltersParser.fromMultiple(paramsRegexpsCsv);
        assertFalse(filters.isEmpty());

        assertEquals(param1, filters.get(0).field());
        assertEquals(quote(regexp1), filters.get(0).regexp().pattern());

        assertEquals(param2, filters.get(1).field());
        assertEquals(quote(regexp2), filters.get(1).regexp().toString());
    }
}
