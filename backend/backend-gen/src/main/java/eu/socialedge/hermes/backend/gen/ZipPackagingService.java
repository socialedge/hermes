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
 *
 */
package eu.socialedge.hermes.backend.gen;

import eu.socialedge.hermes.backend.gen.exception.ZipPackagingException;
import lombok.val;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipPackagingService {

    public byte[] packToZip(List<Document> files) {
        try (val baos = new ByteArrayOutputStream(); val zos = new ZipOutputStream(baos)) {
            for (val file : files) {
                zos.putNextEntry(new ZipEntry(file.getName()));
                zos.write(file.getContent());
                zos.closeEntry();
            }
            zos.finish();
            return baos.toByteArray();
        } catch (IOException ioe) {
            throw new ZipPackagingException("Exception while zip packaging", ioe);
        }
    }

}
