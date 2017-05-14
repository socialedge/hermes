package eu.socialedge.hermes.backend.application.api.projection;

import com.neovisionaries.i18n.LanguageCode;

import java.util.TimeZone;

public interface AgencyProjection {

    String getName();

    LanguageCode getLanguage();

    String getPhone();

    TimeZone getTimeZone();

    String getUrl();
}
