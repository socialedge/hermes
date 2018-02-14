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
package eu.socialedge.hermes.backend.timetable.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.nio.charset.Charset;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.abbreviate;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@Accessors(fluent = true)
public class File {

    private static final String NAME_WITH_EXTENSION_FORMAT = "%s.%s";

    private final String name;
    private final byte[] content;
    private final FileType type;

    public String nameWithExtension() {
        return format(NAME_WITH_EXTENSION_FORMAT, name, type.name().toLowerCase());
    }

    public File rename(String newName) {
        return new File(newName, this.content, this.type);
    }

    public String contentAsString() {
        return new String(content);
    }

    public String contentAsString(Charset charset) {
        return new String(content, charset);
    }

    @Override
    public String toString() {
        return "File{" +
            "name='" + name + '\'' +
            ", content=" + abbreviate(new String(content), 1024) +
            ", type=" + type +
            '}';
    }
}
