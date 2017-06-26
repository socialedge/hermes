/*
 * Hermes - The Municipal Transport Timetable System
 * Copyright (c) 2016-2017 SocialEdge
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
package eu.socialedge.hermes.backend.transit.domain.provider;

import com.neovisionaries.i18n.LanguageCode;
import lombok.*;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.net.URL;
import java.time.ZoneId;
import java.util.Locale;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * An Agency is an operator of a public transit network, often a public
 * authority.
 *
 * @see <a href="https://goo.gl/gXY3Rk">
 *     Google Static Transit (GTFS) - agency.txt File</a>
 */
@Document
@ToString @EqualsAndHashCode(of = "id")
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
    private @NotNull ZoneId timeZone;

    @Getter @Setter
    private URL url;

    public Agency(String id, String name, LanguageCode language, String phone, ZoneId timeZone, URL url) {
        this.id = defaultIfBlank(id, UUID.randomUUID().toString());
        this.name = notBlank(name);
        this.language = notNull(language);
        this.phone = phone;
        this.timeZone = notNull(timeZone);
        this.url = url;
    }

    public Agency(String name, LanguageCode language, String phone, ZoneId timeZone, URL url) {
        this(null, name, language, phone, timeZone, url);
    }

    public Agency(String name) {
        this(null, name, defaultLanguageCode(), null, ZoneId.systemDefault(), null);
    }

    private Agency(Builder builder) {
        this(builder.id, builder.name, builder.language, builder.phone, builder.timeZone, builder.url);
    }

    public void setName(String name) {
        this.name = notBlank(name);
    }

    public void setLanguage(LanguageCode lang) {
        this.language = notNull(lang);
    }

    public void setTimeZone(ZoneId timeZone) {
        this.timeZone = notNull(timeZone);
    }

    private static LanguageCode defaultLanguageCode() {
        val defLocaleLang = Locale.getDefault().getLanguage();
        val findLangCode = LanguageCode.getByCode(defLocaleLang);

        if (findLangCode == null)
            throw new RuntimeException("Failed to get default language code");

        return findLangCode;
    }

    public static final class Builder {

        private String id;

        private String name;

        private LanguageCode language;

        private String phone;

        private ZoneId timeZone;

        private URL url;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder language(LanguageCode language) {
            this.language = language;
            return this;
        }

        public Builder language(String languageCode) {
            this.language = LanguageCode.getByCode(languageCode);
            return this;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder timeZone(ZoneId timeZone) {
            this.timeZone = timeZone;
            return this;
        }

        public Builder timeZone(String timeZone) {
            this.timeZone = ZoneId.of(timeZone);
            return this;
        }

        public Builder url(URL url) {
            this.url = url;
            return this;
        }

        public Agency build() {
            return new Agency(this);
        }
    }
}
