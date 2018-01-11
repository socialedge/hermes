package eu.socialedge.hermes.backend.schedule.domain.v2;

import eu.socialedge.hermes.backend.schedule.domain.Availability;
import eu.socialedge.hermes.backend.schedule.domain.Schedule;
import eu.socialedge.hermes.backend.transit.domain.service.Line;

import java.time.Duration;
import java.time.LocalTime;

public class StaticScheduleBuilder implements ScheduleBuilder {

    private TripFactory tripFactory;

    private String description;

    private Availability availability;

    private Line line;

    private LocalTime startTimeInbound;
    private LocalTime endTimeInbound;

    private LocalTime startTimeOutbound;
    private LocalTime endTimeOutbound;

    private Duration headway;
    private Duration minLayover;

    public StaticScheduleBuilder description(String description) {
        this.description = description;
        return this;
    }

    public StaticScheduleBuilder availability(Availability availability) {
        this.availability = availability;
        return this;
    }

    public StaticScheduleBuilder line(Line line) {
        this.line = line;
        return this;
    }

    public StaticScheduleBuilder startTimeInbound(LocalTime startTimeInbound) {
        this.startTimeInbound = startTimeInbound;
        return this;
    }

    public StaticScheduleBuilder endTimeInbound(LocalTime endTimeInbound) {
        this.endTimeInbound = endTimeInbound;
        return this;
    }

    public StaticScheduleBuilder startTimeOutbound(LocalTime startTimeOutbound) {
        this.startTimeOutbound = startTimeOutbound;
        return this;
    }

    public StaticScheduleBuilder endTimeOutbound(LocalTime endTimeOutbound) {
        this.endTimeOutbound = endTimeOutbound;
        return this;
    }

    public StaticScheduleBuilder headway(Duration headway) {
        this.headway = headway;
        return this;
    }

    public StaticScheduleBuilder minLayover(Duration minLayover) {
        this.minLayover = minLayover;
        return this;
    }

    public StaticScheduleBuilder tripFactory(TripFactory tripFactory) {
        this.tripFactory = tripFactory;
        return this;
    }

    @Override
    public Schedule build() {
        // TODO: generate Schedule here using TripFactory#create
        return null;
    }
}
