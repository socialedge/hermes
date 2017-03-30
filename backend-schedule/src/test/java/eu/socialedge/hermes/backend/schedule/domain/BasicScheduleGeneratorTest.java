package eu.socialedge.hermes.backend.schedule.domain;

import eu.socialedge.hermes.backend.transit.domain.*;
import lombok.val;
import org.junit.Before;
import tec.uom.se.quantity.Quantities;
import tec.uom.se.unit.Units;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashSet;

public class BasicScheduleGeneratorTest {

    private BasicScheduleGenerator generator;

    @Before
    public void setUp() {
            val station = new Station("name", new HashSet<VehicleType>() {{ add(VehicleType.BUS);}}, new Location(2.3, 3.2));
            val route = new Route("code",
                VehicleType.BUS,
                Collections.singletonList(station),
                new Shape(Collections.singletonList(new ShapePoint(new Location(2.3, 3.2), Quantities.getQuantity(140, Units.METRE)))));
            generator = BasicScheduleGenerator.builder()
                .description("description")
                .availability(Availability.weekendDays(LocalDate.now(), LocalDate.now().plusDays(1)))
                .averageSpeed(Quantities.getQuantity(10, Units.METRE_PER_SECOND))
                .dwellTime(Duration.ofMinutes(1))
                .endTimeInbound(LocalTime.MIDNIGHT)
                .endTimeOutbound(LocalTime.MIDNIGHT)
                .startTimeInbound(LocalTime.NOON)
                .startTimeOutbound(LocalTime.NOON)
                .headway(Duration.ofHours(1))
                .fleetSize(10)
                .minLayover(Duration.ofMinutes(4))
                .route(route)
                .build();
    }
}
