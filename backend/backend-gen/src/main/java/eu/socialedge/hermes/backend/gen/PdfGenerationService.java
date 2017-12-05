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

import java.io.IOException;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import lombok.val;

/**
 * Generates byte[] representation of the pdf file.
 * This service uses <a href="https://restpack.io">Restpack</a> service for pdf generation
 */
public class PdfGenerationService {
    private final String apiToken;
    private final String url;

    public PdfGenerationService(String apiToken, String url) {
        this.apiToken = apiToken;
        this.url = url;
    }

    /*
     * Generates pdf based on string content
     */
    public byte[] generate(String content) {
        content = content.replaceAll("\"", "\\\\\"");
        content = String.format("{\"html\": \"%s\"}", content).replaceAll("[\n|\t|\r]", "");
        val client = new OkHttpClient();
        val mediaType = MediaType.parse("application/json");
        val body = RequestBody.create(mediaType, content);
        val request = new Request.Builder().url(url).post(body).addHeader("x-access-token", apiToken)
                .addHeader("content-type", "application/json").addHeader("cache-control", "no-cache").build();

        try {
            val response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().bytes();
            } else {
                throw new PdfGenerationException("Pdf generation failed: " + response.body().string());
            }
        } catch (IOException e) {
            throw new PdfGenerationException("Pdf generation failed:", e);
        }
    }

}
