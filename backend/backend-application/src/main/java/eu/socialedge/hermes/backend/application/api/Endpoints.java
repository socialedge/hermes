package eu.socialedge.hermes.backend.application.api;

import lombok.val;

import java.util.Optional;
import java.util.regex.Pattern;

import static java.lang.String.format;

/**
 * {@code Endpoints} contains API endpoints base urls along with
 * util methods to match urls and parse resource ids
 *
 * @since Hermes 3.0
 */
public enum Endpoints {
    AGENCY("/agencies"),
    STATION("/stations"),
    LINE("/lines"),
    SCEDULE("/schedules"),
    COLLISION("/collisions");

    public static final String AGENCIES = "/agencies";
    public static final String STATIONS = "/stations";
    public static final String LINES = "/lines";
    public static final String SCEDULES = "/schedules";
    public static final String COLLISTIONS = "/collisions";

    private static final String URL_MATCHES_PATTERN_FORMAT = "(?i).*?%s($|[\\/\\?]).*$";
    private static final String ID_CAPTURE_PATTERN_FORMAT = "(?i).*?%s\\/(.{1,}?)($|[\\/\\?]).*$";

    private final String base;
    private final Pattern urlMatchesPattern;
    private final Pattern idCapturePattern;

    Endpoints(String base) {
        this.base = base;

        this.urlMatchesPattern = Pattern.compile(format(URL_MATCHES_PATTERN_FORMAT, Pattern.quote(base)));
        this.idCapturePattern = Pattern.compile(format(ID_CAPTURE_PATTERN_FORMAT, Pattern.quote(base)));
    }

    /**
     * Checks whether given url points the same resource as this enum
     *
     * @param url url to test
     * @return true, if given url points the same resource as this enum
     */
    public boolean matches(String url) {
        return urlMatchesPattern.matcher(url).matches();
    }

    /**
     * Parses an id of a resource from a given url if it points the
     * same resource as this enum.
     *
     * @param url url to parse id from
     * @return id wrapped into Optional (empty if no id found or resource name
     * doest match)
     */
    public Optional<String> parseId(String url) {
        val idMatcher = idCapturePattern.matcher(url);

        if (idMatcher.matches())
            return Optional.of(idMatcher.group(1));

        return Optional.empty();
    }

    /**
     * Gets the resource base url (e.g. '/agencies')
     *
     * @return resource base url
     */
    public String base() {
        return base;
    }

    /**
     * Gets the resource base url. Alias of {@link #base()}.
     *
     * @return resource base url
     */
    @Override
    public String toString() {
        return base();
    }
}
