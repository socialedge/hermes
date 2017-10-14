package eu.socialedge.hermes.backend.application.api.util;

import lombok.val;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static java.util.Objects.nonNull;

/**
 * {@code PageRequests} provides convenient util methods for creating
 * Spring's {@link PageRequest} for Data Repositories paging and sorting.
 *
 * @see Sorts
 * @since Hermes 3.0
 */
public final class PageRequests {

    private static final int DEFAULT_PAGE_SIZE = 25;

    private PageRequests() {
        throw new AssertionError("No instance for you");
    }

    public static Optional<Pageable> from(Integer size, Integer page, String sorting) {
        if (page == null)
            return Optional.empty();

        val pageNumber = page < 0 ? 0 : page;
        val pageSize = nonNull(size) && size >=0 ? size : DEFAULT_PAGE_SIZE;
        val sortOpt = Sorts.parse(sorting);

        if (sortOpt.isPresent()) {
            return Optional.of(new PageRequest(pageNumber, pageSize, sortOpt.get()));
        } else {
            return Optional.of(new PageRequest(pageNumber, pageSize));
        }
    }
}
