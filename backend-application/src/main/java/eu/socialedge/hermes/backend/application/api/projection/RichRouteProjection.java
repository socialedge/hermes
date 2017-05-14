package eu.socialedge.hermes.backend.application.api.projection;

import eu.socialedge.hermes.backend.transit.domain.VehicleType;

import java.util.List;

public interface RichRouteProjection {

    String getCode();

    VehicleType getVehicleType();

    List<StationProjection> getStations();
}
