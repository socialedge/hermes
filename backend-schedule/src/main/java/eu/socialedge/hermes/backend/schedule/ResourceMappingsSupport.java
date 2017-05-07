/*
* Hermes - The Municipal Transport Timetable System
* Copyright (c) 2017 SocialEdge
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
package eu.socialedge.hermes.backend.schedule;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.rest.core.mapping.ResourceMappings;
import org.springframework.data.rest.webmvc.BaseUri;
import org.springframework.data.rest.webmvc.spi.BackendIdConverter;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriTemplate;

import java.io.Serializable;
import java.net.URL;

@Component
public class ResourceMappingsSupport {

    private final ResourceMappings resourceMappings;
    private final PluginRegistry<BackendIdConverter, Class<?>> idConverters;
    private final BaseUri baseUri;
    private final ConversionService conversionService;

    private static final String REPOSITORY_RESOURCE_MAPPING = "/{repository}/{id}";

    @Autowired
    public ResourceMappingsSupport(ResourceMappings resourceMappings, PluginRegistry<BackendIdConverter,
        Class<?>> idConverters, BaseUri baseUri,
                                   @Qualifier("defaultConversionService") ConversionService conversionService) {
        this.resourceMappings = resourceMappings;
        this.idConverters = idConverters;
        this.baseUri = baseUri;
        this.conversionService = conversionService;
    }

    public <T extends Serializable> T extractResourceId(Class<?> entity, Class<T> idType, URL url) {
        return extractResourceId(entity, idType, url.toExternalForm());
    }

    public <T extends Serializable> T extractResourceId(Class<?> entity, Class<T> idType, String request) {
        val resourceDomainType = resourceMappings.getMetadataFor(entity).getDomainType();

        val pluginFor = idConverters.getPluginFor(resourceDomainType, BackendIdConverter.DefaultIdConverter.INSTANCE);
        val lookupPath = baseUri.getRepositoryLookupPath(request);

        val backendId = pluginFor.fromRequestId(findMappingIdVariable(lookupPath), resourceDomainType);

        return conversionService.convert(backendId, idType);
    }

    private static String findMappingIdVariable(String lookupPath) {
        return new UriTemplate(REPOSITORY_RESOURCE_MAPPING).match(lookupPath).get("id");
    }
}
