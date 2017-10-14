package eu.socialedge.hermes.backend.application.api.util;

import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

/**
 * {@code Sorts} provides convenient util methods for parsing
 * {@code ?sort} query param values in Spring Data Rest like
 * format for being passed to Spring Data Repositories of
 * {@link PagingAndSortingRepository} type.
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
     * @return optional @link Sort}
     */
    public static Optional<Sort> parse(String sortCsv) {
        if (!isParsable(sortCsv))
            return Optional.empty();

        return Optional.of(new Sort(parseSortOrder(sortCsv)));
    }

    /**
     * Parses CSV sort values (e.g. {@code property,asc}) into {@link Sort}
     * respecting default sorting direction (DESC)
     *
     * @param sortCsv CSV sort query param values
     * @return optional {@link Sort}
     */
    public static Optional<Sort> parse(String[] sortCsv) {
        if (stream(sortCsv).noneMatch(Sorts::isParsable))
            return Optional.empty();

        return stream(sortCsv)
            .filter(Sorts::isParsable)
            .map(Sorts::parseSortOrder)
            .collect(collectingAndThen(toList(),
                sortOrders -> Optional.of(new Sort(sortOrders))));
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

    private static boolean isParsable(String sorting) {
        return StringUtils.countMatches(sorting, SEARCH_PROP_DELIMITER) == 1;
    }
}
