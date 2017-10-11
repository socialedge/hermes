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
public class AgencyMapper implements SelectiveMapper<Agency, AgencyDTO> {

    @Override
    public AgencyDTO toDTO(Agency agency) {
        if (agency == null)
            return null;

        val dto = new AgencyDTO();

        dto.setId(agency.getId());
        dto.setName(agency.getName());
        dto.setLanguage(agency.getLanguage().name());
        dto.setPhone(agency.getPhone());
        dto.setTimeZone(agency.getTimeZone().getId());

        if (agency.getUrl() != null)
            dto.setUrl(agency.getUrl().toString());

        return dto;
    }

    @Override
    public Agency toDomain(AgencyDTO dto) {
        if (dto == null)
            return null;

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
    public void update(Agency object, AgencyDTO dto) {
        try {
            if (!isBlank(dto.getName()))
                object.setName(dto.getName());
            if (!isBlank(dto.getLanguage()))
                object.setLanguage(LanguageCode.getByCodeIgnoreCase(dto.getLanguage()));
            if (!isBlank(dto.getPhone()))
                object.setPhone(dto.getPhone());
            if (!isBlank(dto.getTimeZone()))
                object.setTimeZone(ZoneId.of(dto.getTimeZone()));
            if (!isBlank(dto.getUrl()))
                object.setUrl(new URL(dto.getUrl()));
        } catch (MalformedURLException e) {
            throw new MappingException("Failed to update agency entity with dto", e);
        }
    }
}
