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
package eu.socialedge.hermes.backend.domain;

import com.neovisionaries.i18n.LanguageCode;
import eu.socialedge.hermes.backend.domain.ext.Identifiable;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.Locale;
import java.util.TimeZone;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * An Agency is an operator of a public transit network, often a public
 * authority. Agencies can have URLs, phone numbers, and language indicators.
 */
@ToString
@Entity @Access(AccessType.FIELD)
@Getter @Setter @Accessors(fluent = true)
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
public class Agency extends Identifiable<Long> {

    @Column(name = "name", nullable = false)
    private @NotBlank String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "lang", nullable = false)
    private @NotNull LanguageCode language;

    @Column(name = "phone")
    private String phone;

    @Column(name = "timezone", nullable = false)
    private @NotNull TimeZone timeZone;

    @Column(name = "url")
    private URL url;

    public Agency(String name, LanguageCode language, String phone, TimeZone timeZone, URL url) {
        this.name = notBlank(name);
        this.language = notNull(language);
        this.phone = phone;
        this.timeZone = notNull(timeZone);
        this.url = url;
    }

    public Agency(String name) {
        this(name, defaultLanguageCode(), null, TimeZone.getDefault(), null);
    }

    public void name(String name) {
        this.name = notBlank(name);
    }

    public void language(LanguageCode lang) {
        this.language = notNull(lang);
    }

    public void timeZone(TimeZone timeZone) {
        this.timeZone = notNull(timeZone);
    }

    private static LanguageCode defaultLanguageCode() {
        val defLocaleLang = Locale.getDefault().getLanguage();
        val findLangCode = LanguageCode.findByName(defLocaleLang);

        if (findLangCode.isEmpty()) throw new RuntimeException("Failed to get default language code");

        return findLangCode.get(0);
    }
}
