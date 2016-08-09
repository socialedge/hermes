/**
 * Hermes - The Municipal Transport Timetable System Copyright (c) 2016 SocialEdge <p> This program
 * is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version. <p> This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 */
package eu.socialedge.hermes.application.provider.mapping.exception.message;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static eu.socialedge.hermes.domain.shared.util.Strings.isNotBlank;
import static eu.socialedge.hermes.domain.shared.util.Strings.requireNotBlank;
import static eu.socialedge.hermes.domain.shared.util.Values.requireNotNull;

public class ErrorMessage implements Serializable {

    private URI source;

    private String title;

    private Integer status;

    private String detail;

    private URL help;

    public ErrorMessage(URI source, String title, Integer httpStatus, String detail, URL help) {
        this.source = requireNotNull(source);
        this.title = requireNotNull(title);
        this.status = requireNotNull(httpStatus);
        this.detail = detail;
        this.help = help;
    }

    public ErrorMessage(String source, String title, Integer httpStatus, String detail, String
            help) {
        this(uri(source), title, httpStatus, detail, isNotBlank(help) ? url(help) : null);
    }

    public ErrorMessage(URI source, String title, Integer httpStatus, String detail) {
        this(source, title, httpStatus, detail, null);
    }

    public ErrorMessage(String source, String title, Integer httpStatus, String detail) {
        this(uri(source), title, httpStatus, detail);
    }

    public ErrorMessage(URI source, String title, Integer httpStatus) {
        this(source, title, httpStatus, null, null);
    }

    public ErrorMessage(String source, String title, Integer httpStatus) {
        this(uri(source), title, httpStatus);
    }

    public String title() {
        return title;
    }

    public void title(String title) {
        this.title = requireNotNull(title);
    }

    public Integer status() {
        return status;
    }

    public void status(Integer httpStatus) {
        this.status = requireNotNull(httpStatus);
    }

    public String detail() {
        return detail;
    }

    public void detail(String detail) {
        this.detail = detail;
    }

    public URL help() {
        return help;
    }

    public void help(URL help) {
        this.help = help;
    }

    public URI source() {
        return source;
    }

    public void source(URI source) {
        this.source = requireNotNull(source);
    }

    private static URL url(String url) {
        try {
            return new URL(requireNotBlank(url));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private static URI uri(String uri) {
        try {
            return new URI(requireNotBlank(uri));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
