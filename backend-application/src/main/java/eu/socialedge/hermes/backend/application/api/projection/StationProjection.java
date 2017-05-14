package eu.socialedge.hermes.backend.application.api.projection;

import eu.socialedge.hermes.backend.transit.domain.Location;
import eu.socialedge.hermes.backend.transit.domain.VehicleType;

import java.util.Set;

public interface StationProjection {

    String getCode();

    String getName();

    String getDescription();

    Set<VehicleType> getVehicleTypes();

    Location getLocation();

    boolean isHailStop();

}
