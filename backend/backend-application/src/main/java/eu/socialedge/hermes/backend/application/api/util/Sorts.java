package eu.socialedge.hermes.backend.application.api.util;

import lombok.val;
import org.springframework.data.domain.Sort;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

/**
 * {@code Sorts} provides convenient util methods for parsing
 * {@code ?sort} query param values in Spring Data Rest like
 * format: {@code ?sort=property,ASC/DESC}.
 * <p>
 * For example: {@code ?sort=name,asc}. Default sorting direction
 * is DESC so that {@code ?sort=name} will be sorted by name DESC
 *
 * @since Hermes 3.0
 */
public final class Sorts {

    private static final String SEARCH_PROP_DELIMITER = ",";

    private Sorts() {
        throw new AssertionError("No instance for you");
    }

    /**
     * Parses singe CSV sort value (e.g. {@code property,asc}) into
     * {@link Sort} respecting default sorting direction (DESC)
     *
     * @param sortCsv CSV sort query param value
     * @return {@link Sort}
     */
    public static Sort parse(String sortCsv) {
        return new Sort(parseSortOrder(sortCsv));
    }

    /**
     * Parses CSV sort values (e.g. {@code property,asc}) into {@link Sort}
     * respecting default sorting direction (DESC)
     *
     * @param sortCsv CSV sort query param values
     * @return {@link Sort}
     */
    public static Sort parse(String[] sortCsv) {
        return stream(sortCsv).map(Sorts::parseSortOrder).collect(collectingAndThen(toList(), Sort::new));
    }

    private static Sort.Order parseSortOrder(String sortCsv) {
        val sortPropDir = sortCsv.split(SEARCH_PROP_DELIMITER);

        if (sortPropDir.length == 0)
            throw new IllegalArgumentException("String doest match prop" + SEARCH_PROP_DELIMITER + "direction pattern");

        val sortProp = sortPropDir[0];
        val sortDirStr  = sortPropDir.length > 1 ? sortPropDir[1] : (String) null;

        val sortDir = Sort.Direction.fromStringOrNull(sortDirStr);

        return new Sort.Order(sortDir, sortProp);
    }
}
