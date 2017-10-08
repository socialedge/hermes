package eu.socialedge.hermes.backend.application.api.util;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.trim;

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
        return Sort.of(sortCsv);
    }

    /**
     * Parses CSV sort values (e.g. {@code property,asc}) into {@link Sort}
     * respecting default sorting direction (DESC)
     *
     * @param sortCsv CSV sort query param values
     * @return {@link Sort}
     */
    public static List<Sort> parse(String[] sortCsv) {
        return stream(sortCsv).map(Sorts::parse).collect(toList());
    }

    @EqualsAndHashCode @ToString
    @Getter @Accessors(fluent = true)
    @RequiredArgsConstructor
    public final static class Sort {

        private static final Direction DEFAULT_DIRECTION = Direction.DESC;

        public enum Direction {
            ASC, DESC;

            public static Direction from(String name) {
                try {
                    return Direction.valueOf(name.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return DEFAULT_DIRECTION;
                }
            }
        }

        private final String property;
        private final Direction direction;

        private static Sort of(String sortCsv) {
            val sortPropDir = sortCsv.split(",");
            val sortProp = trim(sortPropDir[0]);

            if (sortPropDir.length > 1) {
                val sortDir = trim(sortPropDir[1]);
                return new Sort(sortProp, Direction.from(sortDir));
            }

            return new Sort(sortProp, DEFAULT_DIRECTION);
        }
    }
}
