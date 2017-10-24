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

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.StringWriter;

public class TemplatedEntityConverter<E> implements EntityConverter<E, String> {

    private static final String TEMPLATES_FOLDER = "templates";

    private final Template template;

    public TemplatedEntityConverter(String templateName) {
        template = getTemplate(templateName);
    }

    static {
        Velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        Velocity.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        Velocity.init();
    }

    @Override
    public String convert(E entity) {
        StringWriter writer = new StringWriter();
        template.merge(buildContext(entity), writer);
        return writer.toString();
    }

    protected VelocityContext buildContext(E entity) {
        VelocityContext context = new VelocityContext();
        context.put("entity", entity);
        return context;
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
