package eu.socialedge.hermes.backend.application.api.v2.mapping;

import com.neovisionaries.i18n.LanguageCode;
import eu.socialedge.hermes.backend.application.api.dto.AgencyDTO;
import eu.socialedge.hermes.backend.transit.domain.provider.Agency;
import lombok.val;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZoneId;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
public class AgencyEntityMapper implements EntityMapper<Agency, AgencyDTO> {

    @Override
    public AgencyDTO toDTO(Agency entity) {
        val dto = new AgencyDTO();

        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setLanguage(entity.getLanguage().name());
        dto.setPhone(entity.getPhone());
        dto.setTimeZone(entity.getTimeZone().getId());

        if (entity.getUrl() != null)
            dto.setUrl(entity.getUrl().toString());

        return dto;
    }

    @Override
    public Agency toEntity(AgencyDTO dto) {
        try {
            val id = dto.getId();
            val name = dto.getName();
            val lang = LanguageCode.getByCodeIgnoreCase(dto.getLanguage());
            val phone = dto.getPhone();
            val timezone = ZoneId.of(dto.getTimeZone());
            val url = isBlank(dto.getUrl()) ? (URL) null : new URL(dto.getUrl());

            return new Agency(id, name, lang, phone, timezone, url);
        } catch (MalformedURLException e) {
            throw new MappingException("Failed to map dto to agency entity", e);
        }
    }

    @Override
    public void updateEntity(Agency entity, AgencyDTO dto) {
        try {
            if (!isBlank(dto.getName()))
                entity.setName(dto.getName());
            if (!isBlank(dto.getLanguage()))
                entity.setLanguage(LanguageCode.getByCodeIgnoreCase(dto.getLanguage()));
            if (!isBlank(dto.getPhone()))
                entity.setPhone(dto.getPhone());
            if (!isBlank(dto.getTimeZone()))
                entity.setTimeZone(ZoneId.of(dto.getTimeZone()));
            if (!isBlank(dto.getUrl()))
                entity.setUrl(new URL(dto.getUrl()));
        } catch (MalformedURLException e) {
            throw new MappingException("Failed to update agency entity with dto", e);
        }
    }
}
