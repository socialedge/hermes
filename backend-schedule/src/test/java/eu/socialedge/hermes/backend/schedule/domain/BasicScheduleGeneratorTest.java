package eu.socialedge.hermes.backend.schedule.domain;

import com.google.gson.*;
import eu.socialedge.hermes.backend.transit.domain.Route;
import eu.socialedge.hermes.backend.transit.domain.Trip;
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

    private BasicScheduleGenerator generator;

    private final Gson gson = new GsonBuilder().registerTypeAdapter(Quantity.class, new QuantityTypeAdapter()).create();

    @Test
    public void easyScheduleForTwoVehiclesAndExtraTimeAtEnds() {
        generator = gson.fromJson(readFileAsString("/input/two-vehicles-with-fine-layover.json"), BasicScheduleGenerator.class);
        Schedule result = generator.generate();

        Schedule expected = gson.fromJson(readFileAsString("/expectation/two-vehicles-with-fine-layover.json"), Schedule.class);

        assertNotNull(result);
        assertSchedulesEqual(expected, result);
    }

    @Test
    public void realDataFromSumyTransitRoute() {
        generator = gson.fromJson(readFileAsString("/input/sumy-route.json"), BasicScheduleGenerator.class);
        Schedule result = generator.generate();

        Schedule expected = gson.fromJson(readFileAsString("/expectation/sumy-route.json"), Schedule.class);

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
