package eu.socialedge.hermes.domain;

public interface SpecificationMapper<DTO, DO> {

    DTO toDto(DO entity);

    DO fromDto(DTO data);
}
