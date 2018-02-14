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

import eu.socialedge.hermes.backend.timetable.domain.File;
import eu.socialedge.hermes.backend.timetable.domain.FileType;

public interface FileConverter {

    /**
     * Converts file of one type to the file of another type
     * @param source a file to convert
     * @return a converted file
     */
    File convert(File source);

    /**
     * Returns true if this converter implementation can convert given file
     * @param source a file to test this converter against
     * @return true if this converter implementation can convert given file
     */
    default boolean supports(File source) {
        return supports(source.type());
    }

    /**
     * Returns true if this converter implementation can convert files of given type
     * @param sourceType a file type to test this converter against
     * @return true/false
     */
    default boolean supports(FileType sourceType) {
        return true;
    }
}
