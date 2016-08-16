/**
 * Hermes - The Municipal Transport Timetable System Copyright (c) 2016 SocialEdge <p> This program
 * is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version. <p> This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 */
package eu.socialedge.hermes.application.provider.serialization.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.function.Consumer;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import static eu.socialedge.hermes.util.Values.requireNotNull;

@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GsonBodyConverter implements MessageBodyReader<Object>, MessageBodyWriter<Object> {

    private final Gson gson;

    public GsonBodyConverter(Gson gson) {
        this.gson = requireNotNull(gson);
    }

    public static GsonBodyConverter build(Consumer<GsonBuilder> builderConsumer) {
        GsonBuilder gsonBuilder = new GsonBuilder();

        builderConsumer.accept(gsonBuilder);
        return new GsonBodyConverter(gsonBuilder.create());
    }


    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations,
                              MediaType mediaType) {
        return true;
    }

    @Override
    public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations,
                           MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
                           InputStream entityStream)
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
    public long getSize(Object object, Class<?> type, Type genericType,
                        Annotation[] annotations, MediaType
            mediaType) {
        // deprecated by JAX-RS 2.0 and ignored by Jersey runtime
        return 0;
    }

    @Override
    public void writeTo(Object object, Class<?> type, Type genericType, Annotation[] annotations,
                        MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream)
            throws IOException, WebApplicationException {

        try (PrintWriter printWriter = new PrintWriter(entityStream)) {
            String json = gson.toJson(object);
            printWriter.write(json);
            printWriter.flush();
        }
    }
}
