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

import eu.socialedge.hermes.backend.gen.data.StationScheduleTemplate;
import lombok.val;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.StringWriter;

public class PdfGenerator {
    private static final String TEMPLATES_FOLDER = "templates";

    static {
        Velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        Velocity.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        Velocity.init();
    }

    private final String apiToken;
    private final String url;
    private final String templateName;

    public PdfGenerator(String apiToken, String url, String templateName) {
        this.apiToken = apiToken;
        this.url = url;
        this.templateName = templateName;
    }

    public byte[] createPdf(StationScheduleTemplate entity) {
       // val entityString = String.format("{\"html\": \"%s\"}", entityToTemplateString(entity))
        //    .replaceAll("\"", "\\\\\"").replaceAll("[\n|\t|\r]", "");
        // OkHttpClient client = new OkHttpClient();
        // MediaType mediaType = MediaType.parse("application/json");
        // RequestBody body = RequestBody.create(mediaType, entityString);
        // Request request = new Request.Builder()
        // .url(url)
        // .post(body)
        // .addHeader("x-access-token", apiToken)
        // .addHeader("content-type", "application/json")
        // .addHeader("cache-control", "no-cache")
        // .build();
        //
        // try {
        // Response response = client.newCall(request).execute();
        // if (response.isSuccessful()) {
        // return response.body().bytes();
        // } else {
        // throw new PdfGenerationException("Pdf generation failed: " + response.body().string());
        // }
        // } catch (IOException e) {
        // throw new PdfGenerationException("Pdf generation failed:", e);
        // }
        return entityToTemplateString(entity).getBytes(); // TODO bitch
        // TODO val
    }

    private String entityToTemplateString(StationScheduleTemplate entity) {
        val writer = new StringWriter();
        val context = new VelocityContext();
        context.put("entity", entity);
        getTemplate(templateName).merge(context, writer);
        return writer.toString();
    }

    private static Template getTemplate(String templateName) {
        try {
            return Velocity.getTemplate("/" + TEMPLATES_FOLDER + "/" + templateName);
        } catch (ResourceNotFoundException rnfe) {
            throw new RuntimeException("Template '" + templateName + "' not found", rnfe);
        } catch (ParseErrorException pee) {
            throw new RuntimeException("Template '" + templateName + "' is not parsable", pee);
        }
    }

}
