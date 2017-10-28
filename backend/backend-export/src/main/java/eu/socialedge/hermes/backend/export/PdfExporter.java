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

import com.squareup.okhttp.*;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.IOException;
import java.io.StringWriter;

public class PdfExporter<E> {
    private static final String TEMPLATES_FOLDER = "templates";

    static {
        Velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        Velocity.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        Velocity.init();
    }

    private final String apiToken;
    private final String url;
    private final Template template;

    public PdfExporter(String apiToken, String url, String templateName) {
        this.apiToken = apiToken;
        this.url = url;
        template = getTemplate(templateName);
    }

    public byte[] export(E entity) {
        return convertToPdf(entityToTemplateString(entity));
    }

    private byte[] convertToPdf(String entityString) {
        entityString = String.format("{\"html\": \"%s\"}", entityString)
            .replaceAll("[\n|\t|\r]", "");
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, entityString);
        Request request = new Request.Builder()
            .url(url)
            .post(body)
            .addHeader("x-access-token", apiToken)
            .addHeader("content-type", "application/json")
            .addHeader("cache-control", "no-cache")
            .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().bytes();
            } else {
                throw new RuntimeException("Not successful!!!!!!!!!!");
            }
        } catch (IOException e) {
            throw new RuntimeException("OOOPPTSPPSPS", e);
        }
    }

    private String entityToTemplateString(E entity) {
        StringWriter writer = new StringWriter();
        VelocityContext context = new VelocityContext();
        context.put("entity", entity);
        template.merge(context, writer);
        return writer.toString();
    }

    private static Template getTemplate(String templateName) {
        try {
            return Velocity.getTemplate("/" + TEMPLATES_FOLDER + "/" + templateName);
        } catch (ResourceNotFoundException enfe) {
            throw new RuntimeException("Template '" + templateName + "' not found", enfe);
        } catch (ParseErrorException pee) {
            throw new RuntimeException("Template '" + templateName + "' is not parsable", pee);
        }
    }
}
