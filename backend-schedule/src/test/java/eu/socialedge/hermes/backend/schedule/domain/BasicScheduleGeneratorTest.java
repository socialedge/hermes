package eu.socialedge.hermes.backend.schedule.domain;

import eu.socialedge.hermes.backend.transit.domain.*;
import org.junit.Before;
import org.junit.Test;
import tec.uom.se.quantity.Quantities;
import tec.uom.se.unit.Units;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;

public class BasicScheduleGeneratorTest {

    private BasicScheduleGenerator generator;

    @Before
    public void setUp() {
        Location l1 = new Location(10, 22);
        Location l2 = new Location(40, 53);
        Location l3 = new Location(31, 66);
        Location l4 = new Location(55, 69);
        Location l5 = new Location(63, 82);
        Location l6 = new Location(80, 65);
        Location l7 = new Location(58, 9);

        Station s1 = new Station("s1", Collections.singleton(VehicleType.BUS), l1);
        Station s2 = new Station("s2", Collections.singleton(VehicleType.BUS), l2);
        Station s3 = new Station("s3", Collections.singleton(VehicleType.BUS), l3);
        Station s4 = new Station("s4", Collections.singleton(VehicleType.BUS), l4);
        Station s5 = new Station("s5", Collections.singleton(VehicleType.BUS), l5);
        Station s6 = new Station("s6", Collections.singleton(VehicleType.BUS), l6);
        Station s7 = new Station("s7", Collections.singleton(VehicleType.BUS), l7);

        ShapePoint sp1 = new ShapePoint(l1, Quantities.getQuantity(0, Units.METRE));
        ShapePoint sp2 = new ShapePoint(l2, Quantities.getQuantity(1400, Units.METRE));
        ShapePoint sp3 = new ShapePoint(l3, Quantities.getQuantity(3548, Units.METRE));
        ShapePoint sp4 = new ShapePoint(l4, Quantities.getQuantity(6541, Units.METRE));
        ShapePoint sp5 = new ShapePoint(l5, Quantities.getQuantity(9854, Units.METRE));
        ShapePoint sp6 = new ShapePoint(l6, Quantities.getQuantity(13789, Units.METRE));
        ShapePoint sp7 = new ShapePoint(l7, Quantities.getQuantity(15873, Units.METRE));

        Route route = new Route("route", VehicleType.BUS, Arrays.asList(s1, s2, s3, s4, s5, s6, s7), new Shape(Arrays.asList(sp1, sp2, sp3, sp4, sp5, sp6, sp7)));

        generator = BasicScheduleGenerator.builder()
            .description("description")
            .availability(Availability.weekendDays(LocalDate.now(), LocalDate.now().plusDays(20)))
            .averageSpeed(Quantities.getQuantity(15, Units.METRE_PER_SECOND))
            .dwellTime(Duration.ofSeconds(25))
            .startTimeInbound(LocalTime.of(8, 0))
            .startTimeOutbound(LocalTime.of(8, 0))
            .endTimeInbound(LocalTime.of(19, 0))
            .endTimeOutbound(LocalTime.of(19, 0))
            .headway(Duration.ofMinutes(45))
            .minLayover(Duration.ofMinutes(4))
            .route(route)
            .build();
        
        
        /*
        start time  end time   vehicle id     direction
        8:00         8:21         1            inbound
        8:00         8:21         2            outbound
        8:45         9:06         1            outbound
        8:45         9:06         2            inbound
        9:30         9:51         1            inbound
        9:30         9:51         2            outbound
        10:15        10:36        1            outbound
        10:15        10:36        2            inbound
        11:00        11:21        1            inbound
        11:00        11:21        2            outbound
        11:45        12:06        1            outbound
        11:45        12:06        2            inbound
        12:30        12:51        1            inbound
        12:30        12:51        2            outbound
        13:15        13:36        1            outbound
        13:15        13:36        2            inbound
        14:00        14:21        1            inbound
        14:00        14:21        2            outbound
         */
    }

    @Test
    public void test() {
        Schedule schedule = generator.generate();
        int i = 0;
    }
}
