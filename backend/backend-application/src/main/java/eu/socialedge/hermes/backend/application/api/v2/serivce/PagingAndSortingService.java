package eu.socialedge.hermes.backend.application.api.v2.serivce;

import eu.socialedge.hermes.backend.application.api.util.Sorts;
import eu.socialedge.hermes.backend.application.api.v2.mapping.SelectiveMapper;
import lombok.val;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * {@code PagingAndSortingService} is a base class for application services
 * that support paging and sorting
 *
 * @param <E> entity type
 * @param <I> entity's id type
 * @param <D> entity DTO
 */
@Transactional(readOnly = true)
abstract class PagingAndSortingService<E, I extends Serializable, D extends Serializable> {

    private static final int DEFAULT_PAGE_SIZE = 25;

    private static final String PAGE_SIZE_HEADER = "X-Page-Size";
    private static final String PAGE_NUM_HEADER = "X-Page-Number";
    private static final String PAGE_TOTAL_HEADER = "X-Page-Total";
    private static final String RESOURCE_TOTAL_HEADER = "X-Resource-Total-Records";

    protected final PagingAndSortingRepository<E, I> repository;
    protected final SelectiveMapper<E, D> mapper;

    protected PagingAndSortingService(PagingAndSortingRepository<E, I> repository, SelectiveMapper<E, D> mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public ResponseEntity<List<D>> list(Integer size, Integer page, String sorting) {
        val sortingGiven = !isBlank(sorting);
        val pageGiven = nonNull(page);
        val sizeGiven = nonNull(size);

        List<D> entities;
        HttpHeaders headers = null;

        if (pageGiven) {
            val pageNumber = page < 0 ? 0 : page;
            val pageSize = sizeGiven && size >=0 ? size : DEFAULT_PAGE_SIZE;

            if (sortingGiven) {
                val pageReq = new PageRequest(pageNumber, pageSize, Sorts.parse(sorting));
                entities = list(pageReq);
            } else {
                val pageReq = new PageRequest(pageNumber, pageSize);
                entities = list(pageReq);
            }

            val totalEntities = total();
            headers = compilePageHeaders(pageSize, pageNumber, totalEntities);
        } else if (sortingGiven) {
            entities = list(Sorts.parse(sorting));
        } else {
            entities = list();
        }

        return new ResponseEntity<>(entities, headers, HttpStatus.OK);
    }

    protected List<D> list() {
        val entities = repository.findAll();
        return mapper.toDTO(entities);
    }

    protected List<D> list(Sort sorting) {
        val entities = repository.findAll(sorting);
        return mapper.toDTO(entities);
    }

    protected List<D> list(Pageable paging) {
        val entities = repository.findAll(paging);
        return mapper.toDTO(entities);
    }

    public ResponseEntity<D> get(I id) {
        val entity = repository.findOne(id);
        if (entity == null)
            return ResponseEntity.notFound().build();

        return new ResponseEntity<>(mapper.toDTO(entity), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<D> save(D dto) {
        val entity = mapper.toDomain(dto);
        val savedEntity = repository.save(entity);

        return new ResponseEntity<>(mapper.toDTO(savedEntity), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<D> update(I id, D dto) {
        val entity = repository.findOne(id);
        if (entity == null)
            return ResponseEntity.notFound().build();

        mapper.update(entity, dto);

        return new ResponseEntity<>(mapper.toDTO(entity), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Void> delete(I id) {
        if (!repository.exists(id))
            return ResponseEntity.notFound().build();

        repository.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    public long total() {
        return repository.count();
    }

    protected HttpHeaders compilePageHeaders(int size, int page, long totalEntities) {
        val httpHeaders = new HttpHeaders();

        httpHeaders.add(PAGE_SIZE_HEADER, String.valueOf(size));
        httpHeaders.add(PAGE_NUM_HEADER, String.valueOf(page));
        httpHeaders.add(PAGE_TOTAL_HEADER, String.valueOf((int) Math.ceil((double) totalEntities / size)));
        httpHeaders.add(RESOURCE_TOTAL_HEADER, String.valueOf(totalEntities));

        return httpHeaders;
    }
}
