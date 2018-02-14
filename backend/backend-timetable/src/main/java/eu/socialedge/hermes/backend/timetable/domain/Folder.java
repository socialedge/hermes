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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.val;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * {@code Folder} represents a set of related files.
 */
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode @ToString
public class Folder {

    private final List<File> files = new ArrayList<>();

    public Folder(List<File> files) {
        this.files.addAll(files);
    }

    public static Folder of(List<File> files) {
        return new Folder(files);
    }

    public byte[] toZip() {
        try (val baos = new ByteArrayOutputStream(); val zos = new ZipOutputStream(baos)) {
            for (val file : files) {
                zos.putNextEntry(new ZipEntry(file.nameWithExtension()));
                zos.write(file.content());
                zos.closeEntry();
            }
            zos.finish();
            return baos.toByteArray();
        } catch (IOException ioe) {
            throw new ZippingException("Exception while zip packaging", ioe);
        }
    }
}
