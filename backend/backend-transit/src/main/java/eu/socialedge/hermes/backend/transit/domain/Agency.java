/*
 * Hermes - The Municipal Transport Timetable System
 * Copyright (c) 2017 SocialEdge
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package eu.socialedge.hermes.backend.transit.domain;

import com.neovisionaries.i18n.LanguageCode;
import lombok.*;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * An Agency is an operator of a public transit network, often a public
 * authority. Agencies can have URLs, phone numbers, and language indicators.
 */
@Document
@ToString
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
public class Agency {

    @Id @Getter
    private final String id;

    @Getter
    private @NotBlank String name;

    @Getter
    private @NotNull LanguageCode language;

    @Getter @Setter
    private String phone;

    @Getter
    private @NotNull TimeZone timeZone;

    @Getter @Setter
    private URL url;

    public Agency(String id, String name, LanguageCode language, String phone, TimeZone timeZone, URL url) {
        this.id = defaultIfBlank(id, UUID.randomUUID().toString());
        this.name = notBlank(name);
        this.language = notNull(language);
        this.phone = phone;
        this.timeZone = notNull(timeZone);
        this.url = url;
    }

    public Agency(String name, LanguageCode language, String phone, TimeZone timeZone, URL url) {
        this(null, name, language, phone, timeZone, url);
    }

    public Agency(String name) {
        this(null, name, defaultLanguageCode(), null, TimeZone.getDefault(), null);
    }

    public void setName(String name) {
        this.name = notBlank(name);
    }

    public void setLanguage(LanguageCode lang) {
        this.language = notNull(lang);
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = notNull(timeZone);
    }

    private static LanguageCode defaultLanguageCode() {
        val defLocaleLang = Locale.getDefault().getLanguage();
        val findLangCode = LanguageCode.getByCode(defLocaleLang);

        if (findLangCode == null)
            throw new RuntimeException("Failed to get default language code");

        return findLangCode;
    }
}
