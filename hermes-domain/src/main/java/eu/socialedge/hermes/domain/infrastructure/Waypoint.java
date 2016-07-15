package eu.socialedge.hermes.domain.infrastructure;

import eu.socialedge.hermes.domain.ext.ValueObject;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

@ValueObject
@Embeddable
public class Waypoint implements Serializable, Comparable<Waypoint> {
    @NotNull
    @ManyToOne
    @JoinColumn(name = "station_id")
    private final Station station;

    @Min(1)
    @Column(name = "position")
    private final int position;

    public Waypoint(Station station, int position) {
        this.station = station;
        this.position = position;
    }

    public static Waypoint of(Station station, int position) {
        return new Waypoint(station, position);
    }

    public Station getStation() {
        return station;
    }

    public int getPosition() {
        return position;
    }

    @Override
    public int compareTo(Waypoint o) {
        if (this.position < o.position)
            return -1;
        else if (this.position > o.position)
            return 1;

        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Waypoint)) return false;
        Waypoint waypoint = (Waypoint) o;
        return getPosition() == waypoint.getPosition() &&
                Objects.equals(getStation(), waypoint.getStation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStation(), getPosition());
    }

    @Override
    public String toString() {
        return "Waypoint{" +
                ", station=" + station +
                ", position=" + position +
                '}';
    }
}
