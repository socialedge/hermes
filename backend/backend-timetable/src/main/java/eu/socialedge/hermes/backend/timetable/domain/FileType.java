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
package eu.socialedge.hermes.backend.timetable.domain;

public enum FileType {
    ZIP ("application/zip", "zip"),
    PDF("application/pdf", "pdf"),
    HTML ("text/html", "html"),
    DOCX ("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "docx"),
    ODF ("application/vnd.oasis.opendocument.formula", "odf"),
    UNKNOWN ("application/octet-stream", "");

    private String mediaType;
    private String extension;

    FileType(String mediaType, String extension) {
        this.mediaType = mediaType;
        this.extension = extension;
    }

    public String mediaType() {
        return mediaType;
    }

    public String extension() {
        return extension;
    }
}
