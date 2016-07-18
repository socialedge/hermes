/**
 * Hermes - The Municipal Transport Timetable System
 * Copyright (c) 2016 SocialEdge
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
package eu.socialedge.hermes.application.service;

import eu.socialedge.hermes.application.exception.BadRequestException;
import eu.socialedge.hermes.application.exception.NotFoundException;
import eu.socialedge.hermes.domain.infrastructure.Operator;
import eu.socialedge.hermes.domain.infrastructure.OperatorRepository;
import eu.socialedge.hermes.domain.infrastructure.Position;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

@Component
@Transactional(readOnly = true)
public class OperatorService {
    private final OperatorRepository operatorRepository;

    @Inject
    public OperatorService(OperatorRepository operatorRepository) {
        this.operatorRepository = operatorRepository;
    }

    @Transactional
    public Operator createOperator(String name, String desc, String url, Position position) {
        return createOperator(name, desc, toUrl(url), position);
    }

    @Transactional
    public Operator createOperator(String name, String desc, URL url, Position position) {
        Operator operator = new Operator(name);

        if (StringUtils.isNotBlank(desc))
            operator.setDescription(desc);
        if (url != null)
            operator.setWebsite(url);
        if (position != null)
            operator.setPosition(position);

        return operatorRepository.store(operator);
    }

    public Collection<Operator> fetchAllOperators() {
        return operatorRepository.list();
    }

    public Operator fetchOperator(int operatorId) {
        if (operatorId <= 0)
            throw new IllegalArgumentException("Invalid operator (not > 0)");

        return operatorRepository.get(operatorId).orElseThrow(() ->
                new NotFoundException("No operator was found with id + " + operatorId));
    }

    @Transactional
    public void updateOperator(int operatorId, String name, String desc, String url, Position position) {
        updateOperator(operatorId, name, desc, toUrl(url), position);
    }

    @Transactional
    public void updateOperator(int operatorId, String name, String desc, URL url, Position position) {
        Operator operator = fetchOperator(operatorId);

        if (StringUtils.isNotBlank(name))
            operator.setName(name);
        if (StringUtils.isNotBlank(desc))
            operator.setDescription(desc);
        if (url != null)
            operator.setWebsite(url);
        if (position != null)
            operator.setPosition(position);

        operatorRepository.store(operator);
    }

    @Transactional
    public void removeOperator(int operatorId) {
        operatorRepository.remove(fetchOperator(operatorId));
    }

    private URL toUrl(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new BadRequestException("Invalid url was passed as web site address: " + url, e);
        }
    }
}
