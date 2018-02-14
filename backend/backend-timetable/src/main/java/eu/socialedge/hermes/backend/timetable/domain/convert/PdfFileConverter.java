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

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import eu.socialedge.hermes.backend.timetable.domain.File;
import eu.socialedge.hermes.backend.timetable.domain.FileType;
import lombok.val;

import java.io.IOException;

import static java.lang.String.format;

/**
 * {@code PdfFileConverter} converts {@link File} of {@link FileType#HTML}
 * to PDF file. It uses <a href="https://restpack.io">Restpack</a> service for html
 * 2 pdf conversion.
 */
public class PdfFileConverter implements FileConverter {

    private static final String RESTPACK_DEFAULT_ENDPOINT = "https://restpack.io/api/html2pdf/v2/convert";
    private static final MediaType RESTPACK_BODY_MEDIATYPE = MediaType.parse("application/json");
    private static final String RESTPACK_BODY_FORMAT = "{\"html\": \"%s\"}";
    private static final FileType RESTPACK_FILE_TYPE_SUPPORTED = FileType.HTML;
    private static final FileType RESTPACK_FIlE_OUTPUR_TYPE = FileType.PDF;

    private final String apiToken;
    private final String url;

    private final OkHttpClient client;

    public PdfFileConverter(String apiToken, String url, OkHttpClient client) {
        this.apiToken = apiToken;
        this.url = url;
        this.client = client;
    }

    public PdfFileConverter(String apiToken, String url) {
        this(apiToken, url, new OkHttpClient());
    }

    public PdfFileConverter(String apiToken) {
        this(apiToken, RESTPACK_DEFAULT_ENDPOINT);
    }

    @Override
    public File convert(File source) {
        if (!supports(source))
            throw new FileConversionException("Unsupported source file type by Restpack = " + source.type());

        val sourceContent = source.content();
        val sourceName = source.name();

        try {
            val request = createRequest(sourceContent);
            val response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                return new File(sourceName, response.body().bytes(), RESTPACK_FIlE_OUTPUR_TYPE);
            } else {
                throw new FileConversionException("Pdf generation failed: " + response.body().string());
            }
        } catch (IOException e) {
            throw new FileConversionException("Pdf generation failed:", e);
        }
    }

    @Override
    public boolean supports(FileType sourceType) {
        return RESTPACK_FILE_TYPE_SUPPORTED == sourceType;
    }

    private Request createRequest(byte[] contentBytes) {
        val content = new String(contentBytes);
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
