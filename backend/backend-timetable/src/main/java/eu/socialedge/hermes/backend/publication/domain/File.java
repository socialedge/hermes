/*
 * Hermes - The Municipal Transport Timetable System
 * Copyright (c) 2016-2018 SocialEdge
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
package eu.socialedge.hermes.backend.publication.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static java.lang.String.format;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

@Getter
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
public class File {
    private static final String NAME_WITH_EXTENSION = "%s.%s";

    private String name;

    private byte[] contents;

    private FileType type;

    public File(String name, byte[] contents, FileType type) {
        this.name = notBlank(name);
        this.contents = notNull(contents);
        this.type = type;
    }

    public File(String name, byte[] contents) {
        this(name, contents, null);
    }

    public String nameWithExtension() {
        if (type != null) {
            return format(NAME_WITH_EXTENSION, name, type.extension());
        }
        return name;
    }
}
