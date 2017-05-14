package eu.socialedge.hermes.backend.application.api.projection;

import java.net.URL;

public interface RichLineProjection {

    String getCode();

    String getName();

    String getDescription();

    AgencyProjection getAgency();

    RichRouteProjection getInboundRoute();

    RichRouteProjection getOutboundRoute();

    URL getUrl();
}
