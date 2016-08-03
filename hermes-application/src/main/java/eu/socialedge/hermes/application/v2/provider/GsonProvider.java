/**
 * Hermes - The Municipal Transport Timetable System Copyright (c) 2016 SocialEdge <p> This program
 * is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version. <p> This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 */
package eu.socialedge.hermes.application.v2.provider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import eu.socialedge.hermes.domain.v2.operator.Email;
import eu.socialedge.hermes.domain.v2.operator.Phone;
import eu.socialedge.hermes.domain.v2.shared.EntityCode;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.ZoneOffset;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GsonProvider<T> implements MessageBodyReader<T>, MessageBodyWriter<T> {

    private static final String JS_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private final Gson gson;

    public GsonProvider() {
        this.gson = new GsonBuilder()
                .enableComplexMapKeySerialization()
                .registerTypeHierarchyAdapter(EntityCode.class, new EntityCodeJsonSerializer())
                .registerTypeAdapter(Email.class, new EmailJsonSerializer())
                .registerTypeAdapter(Phone.class, new PhoneJsonSerializer())
                .registerTypeAdapter(ZoneOffset.class, new ZoneOffsetJsonSerializer())
                .setDateFormat(JS_DATE_FORMAT)
                .setPrettyPrinting()
                .create();
    }


    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations,
                              MediaType mediaType) {
        return true;
    }

    @Override
    public T readFrom(Class<T> type, Type genericType, Annotation[] annotations, MediaType
            mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
            throws IOException, WebApplicationException {
        try (InputStreamReader reader = new InputStreamReader(entityStream, "UTF-8")) {
            return gson.fromJson(reader, type);
        }
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations,
                               MediaType mediaType) {
        return true;
    }

    @Override
    public long getSize(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType
            mediaType) {
        // deprecated by JAX-RS 2.0 and ignored by Jersey runtime
        return 0;
    }

    @Override
    public void writeTo(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType
            mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
            throws IOException, WebApplicationException {

        try (PrintWriter printWriter = new PrintWriter(entityStream)) {
            String json = gson.toJson(t);
            printWriter.write(json);
            printWriter.flush();
        }
    }

    private static class EntityCodeJsonSerializer implements JsonSerializer<EntityCode> {
        @Override
        public JsonElement serialize(EntityCode src, Type typeOfSrc, JsonSerializationContext
                context) {
            return new JsonPrimitive(src.toString());
        }
    }

    private static class PhoneJsonSerializer implements JsonSerializer<Phone> {
        @Override
        public JsonElement serialize(Phone src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.number());
        }
    }

    private static class EmailJsonSerializer implements JsonSerializer<Email> {
        @Override
        public JsonElement serialize(Email src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.address());
        }
    }

    private static class ZoneOffsetJsonSerializer implements JsonSerializer<ZoneOffset> {
        @Override
        public JsonElement serialize(ZoneOffset src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getId());
        }
    }
}
