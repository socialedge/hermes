/*
 * Hermes - The Municipal Transport Timetable System
 * Copyright (c) 2017 SocialEdge
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
package eu.socialedge.hermes.backend.schedule.domain;

import com.google.gson.*;
import eu.socialedge.hermes.backend.transit.domain.Route;
import eu.socialedge.hermes.backend.transit.domain.Station;
import eu.socialedge.hermes.backend.transit.domain.Stop;
import eu.socialedge.hermes.backend.transit.domain.Trip;
import lombok.val;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Parameterized.class)
public class BasicScheduleGeneratorTest {

    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(Quantity.class, new QuantityTypeAdapter()).create();

    @Parameterized.Parameter
    public String fileName;

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection<String> data() {
        val file = new File(BasicScheduleGeneratorTest.class.getClass().getResource("/input").getFile());
        return Arrays.asList(file.list());
    }

    @Test
    public void shouldReturnCorrectSchedule() {
        val generator = GSON.fromJson(readFileAsString("/input/" + fileName), BasicScheduleGenerator.class);
        val result = generator.generate();

        val expected = GSON.fromJson(readFileAsString("/expectation/" + fileName), Schedule.class);

        assertNotNull(result);
        assertSchedulesEqual(expected, result);
    }

    private static void assertSchedulesEqual(Schedule expected, Schedule result) {
        assertEquals(expected.availability(), result.availability());
        assertEquals(expected.description(), result.description());

        assertEquals(expected.trips().size(), result.trips().size());

        assertEquals(expected.trips().stream().map(Trip::vehicleId).collect(toList()),
            result.trips().stream().map(Trip::vehicleId).collect(toList()));

        assertEquals(expected.trips().stream().map(Trip::headsign).collect(toList()),
            result.trips().stream().map(Trip::headsign).collect(toList()));

        assertEquals(expected.trips().stream().map(Trip::route).map(Route::code).collect(toList()),
            result.trips().stream().map(Trip::route).map(Route::code).collect(toList()));

        assertEquals(expected.trips().stream().map(Trip::stops).collect(toList()),
            result.trips().stream().map(Trip::stops).collect(toList()));

        assertEquals(expected.trips().stream()
                .map(trip -> trip.stops().stream().map(Stop::station).map(Station::name).collect(toList()))
                .collect(toList()),
            result.trips().stream()
                .map(trip -> trip.stops().stream().map(Stop::station).map(Station::name).collect(toList()))
                .collect(toList()));
    }

    private static String readFileAsString(String path) {
        try {
            return IOUtils.toString(BasicScheduleGeneratorTest.class.getResourceAsStream(path));
        } catch (IOException e) {
            throw new RuntimeException("Exception while reading file: " + path, e);
        }
    }
}

class QuantityTypeAdapter implements JsonSerializer<Quantity>, JsonDeserializer<Quantity> {

    @Override
    public Quantity deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Quantities.getQuantity(json.getAsJsonPrimitive().getAsString());
    }

    @Override
    public JsonElement serialize(Quantity src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }
}
