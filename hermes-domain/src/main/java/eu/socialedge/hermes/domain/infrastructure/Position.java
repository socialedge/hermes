package eu.socialedge.hermes.domain.infrastructure;

import eu.socialedge.hermes.domain.ext.ValueObject;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Objects;

@ValueObject
@Embeddable
public class Position {
    @Min(-90)
    @Max(90)
    @Column(name = "latitude")
    private final Float latitude;

    @Min(-180)
    @Max(180)
    @Column(name = "longitude")
    private final Float longitude;

    public Position(float latitude, float longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static Position of(float latitude, float longitude) {
        return new Position(latitude, longitude);
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return Objects.equals(latitude, position.latitude) &&
                Objects.equals(longitude, position.longitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }

    @Override
    public String toString() {
        return "Position{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
