package eu.socialedge.hermes.backend.transit.domain;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.Validate.notNull;

@Document
@ToString @EqualsAndHashCode
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
public class Segment {

    @DBRef @Getter
    private final @NotNull Station begin;

    @DBRef @Getter
    private final @NotNull Station end;

    @Getter
    private final Quantity<Length> length;

    private final @NotNull List<Location> waypoints = new ArrayList<>();

    public Segment(Station begin, Station end, Quantity<Length> length, List<Location> waypoints) {
        this.begin = notNull(begin);
        this.end = notNull(end);

        if (length != null && length.getValue().longValue() < 0)
            throw new IllegalArgumentException("Length cant be < 0");

        this.length = length;

        if (waypoints != null)
            this.waypoints.addAll(waypoints);
    }

    public static Segment of(Station begin, Station end, Quantity<Length> length, Location... waypoints) {
        return new Segment(begin, end, length, asList(waypoints));
    }

    public Segment(Station begin, Station end, Quantity<Length> length) {
        this(begin, end, length, null);
    }

    public static Segment of(Station begin, Station end, Quantity<Length> length) {
        return new Segment(begin, end, length);
    }

    public Segment(Station begin, Station end) {
        this(begin, end, null, null);
    }

    public static Segment of(Station begin, Station end) {
        return new Segment(begin, end);
    }

    public List<Location> getWaypoints() {
        return Collections.unmodifiableList(waypoints);
    }
}
