package eu.socialedge.hermes.application.domain;

public interface DtoMapper<DTO, DO> {
    DTO toDto(DO entity);
    DO fromDto(DTO data);
}
