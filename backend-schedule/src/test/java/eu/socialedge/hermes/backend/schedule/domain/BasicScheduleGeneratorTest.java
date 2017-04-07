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
import eu.socialedge.hermes.backend.schedule.domain.api.ScheduleGenerator;
import eu.socialedge.hermes.backend.transit.domain.Route;
import eu.socialedge.hermes.backend.transit.domain.Station;
import eu.socialedge.hermes.backend.transit.domain.Stop;
import eu.socialedge.hermes.backend.transit.domain.Trip;
import lombok.val;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import java.io.IOException;
import java.lang.reflect.Type;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BasicScheduleGeneratorTest {

    private ScheduleGenerator generator;

    private final Gson gson = new GsonBuilder().registerTypeAdapter(Quantity.class, new QuantityTypeAdapter()).create();

    @Test
    public void easyScheduleForTwoVehiclesAndExtraTimeAtEnds() {
        generator = gson.fromJson(readFileAsString("/input/two-vehicles-with-fine-layover.json"), BasicScheduleGenerator.class);
        val result = generator.generate();

        val expected = gson.fromJson(readFileAsString("/expectation/two-vehicles-with-fine-layover.json"), Schedule.class);

        assertNotNull(result);
        assertSchedulesEqual(expected, result);
    }

    @Test
    public void realDataFromSumyTransitRoute() {
        generator = gson.fromJson(readFileAsString("/input/sumy-route.json"), BasicScheduleGenerator.class);
        val result = generator.generate();

        val expected = gson.fromJson(readFileAsString("/expectation/sumy-route.json"), Schedule.class);

        assertNotNull(result);
        assertSchedulesEqual(expected, result);
    }

    @Test
    public void intenseScheduleWithManyVehicles() {
        generator = gson.fromJson(readFileAsString("/input/intense-with-many-vehicles.json"), BasicScheduleGenerator.class);
        val result = generator.generate();

        val expected = gson.fromJson(readFileAsString("/expectation/intense-with-many-vehicles.json"), Schedule.class);

        assertNotNull(result);
        assertSchedulesEqual(expected, result);
    }

    @Test
    public void scheduleWhereVehiclesShouldSkipATripBecauseOfLayover() {
        generator = gson.fromJson(readFileAsString("/input/small-layover.json"), BasicScheduleGenerator.class);
        val result = generator.generate();

        val expected = gson.fromJson(readFileAsString("/expectation/small-layover.json"), Schedule.class);

        assertNotNull(result);
        assertSchedulesEqual(expected, result);
    }

    @Test
    public void differentTravelTimeInboundAndOutbound() {
        generator = gson.fromJson(readFileAsString("/input/different-travel-times-inbound-outbound.json"), BasicScheduleGenerator.class);
        val result = generator.generate();

        val expected = gson.fromJson(readFileAsString("/expectation/different-travel-times-inbound-outbound.json"), Schedule.class);

        assertNotNull(result);
        assertSchedulesEqual(expected, result);
    }

    //TODO this case introduces problem when some vehicle might have only one trip for whole day
    @Test
    public void differentStartAndEndTimesForInboundAndOutbound() {
        generator = gson.fromJson(readFileAsString("/input/different-start-time-inbound-outbound.json"), BasicScheduleGenerator.class);
        val result = generator.generate();

        val expected = gson.fromJson(readFileAsString("/expectation/different-start-time-inbound-outbound.json"), Schedule.class);

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
