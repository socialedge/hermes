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
package eu.socialedge.hermes.backend.timetable.domain.convert;

import eu.socialedge.hermes.backend.timetable.domain.Document;

public interface DocumentConverter {

    /**
     * Converts document of one type to the document of another type
     * @param source a document to convert
     * @return a converted document
     */
    Document convert(Document source);

    /**
     * Returns true if this converter implementation can convert given document
     * @param source a doc to test this converter against
     * @return true if this converter implementation can convert given document
     */
    default boolean supports(Document source) {
        return supports(source.type());
    }

    /**
     * Returns true if this converter implementation can convert documents of given type
     * @param sourceType a doc type to test this converter against
     * @return true/false
     */
    default boolean supports(Document.Type sourceType) {
        return true;
    }
}
