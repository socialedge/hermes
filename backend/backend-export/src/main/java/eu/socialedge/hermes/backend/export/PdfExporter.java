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

package eu.socialedge.hermes.backend.export;

import java.io.ByteArrayOutputStream;

import com.pdfcrowd.Client;
import com.pdfcrowd.PdfcrowdError;

public class PdfExporter<E> implements Exporter<E> {

    private final Client pdfClient;
    private final EntityConverter<E, String> entityConverter;

    public PdfExporter(String username, String apiKey, EntityConverter<E, String> entityConverter) {
        pdfClient = new Client(username, apiKey);
        this.entityConverter = entityConverter;
    }

    @Override
    public byte[] export(E entity) {
        return convertToPdf(entityConverter.convert(entity));
    }

    private byte[] convertToPdf(String entityString) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            pdfClient.convertHtml(entityString, outputStream);
            return outputStream.toByteArray();
        } catch(PdfcrowdError why) {
            throw new RuntimeException("Oops", why);
        }
    }
}
