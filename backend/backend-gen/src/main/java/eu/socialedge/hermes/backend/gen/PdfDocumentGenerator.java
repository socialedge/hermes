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

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import eu.socialedge.hermes.backend.gen.exception.PdfGenerationException;
import lombok.val;

import java.io.IOException;

import static java.lang.String.format;

/**
 * Generates {@link Document} that represents pdf file.
 * This service uses <a href="https://restpack.io">Restpack</a> service for pdf generation
 */
public class PdfDocumentGenerator implements DocumentGenerator {

    private static final String RESTPACK_DEFAULT_ENDPOINT = "https://restpack.io/api/html2pdf/v2/convert";
    private static final MediaType RESTPACK_BODY_MEDIATYPE = MediaType.parse("application/json");
    private static final String RESTPACK_BODY_FORMAT = "{\"html\": \"%s\"}";

    private final String apiToken;
    private final String url;

    private final OkHttpClient client;

    public PdfDocumentGenerator(String apiToken, String url, OkHttpClient client) {
        this.apiToken = apiToken;
        this.url = url;
        this.client = client;
    }

    public PdfDocumentGenerator(String apiToken, String url) {
        this(apiToken, url, new OkHttpClient());
    }

    public PdfDocumentGenerator(String apiToken) {
        this(apiToken, RESTPACK_DEFAULT_ENDPOINT);
    }

    public Document generate(String name, String content) {
        try {
            val request = createRequest(content);
            val response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                return new Document(name, response.body().bytes(), Document.Type.PDF);
            } else {
                throw new PdfGenerationException("Pdf generation failed: " + response.body().string());
            }
        } catch (IOException e) {
            throw new PdfGenerationException("Pdf generation failed:", e);
        }
    }

    private Request createRequest(String content) {
        val reqContent = format(RESTPACK_BODY_FORMAT, escapeContentSlashes(content));
        val reqBody = RequestBody.create(RESTPACK_BODY_MEDIATYPE, tripSpaces(reqContent));

        return new Request.Builder()
            .url(url)
            .post(reqBody)
            .addHeader("x-access-token", apiToken)
            .addHeader("content-type", RESTPACK_BODY_MEDIATYPE.toString())
            .addHeader("cache-control", "no-cache").build();
    }

    private static String escapeContentSlashes(String content) {
        return content.replaceAll("\"", "\\\\\"");
    }

    private static String tripSpaces(String content) {
        return content.replaceAll("[\n|\t|\r]", "");
    }
}
