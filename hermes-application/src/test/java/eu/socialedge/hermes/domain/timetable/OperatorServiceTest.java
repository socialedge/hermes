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
package eu.socialedge.hermes.domain.timetable;

import eu.socialedge.hermes.application.exception.BadRequestException;
import eu.socialedge.hermes.application.exception.NotFoundException;
import eu.socialedge.hermes.application.service.OperatorService;
import eu.socialedge.hermes.domain.infrastructure.Operator;
import eu.socialedge.hermes.domain.infrastructure.OperatorRepository;
import eu.socialedge.hermes.domain.infrastructure.Position;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.URL;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.AdditionalAnswers.*;

@RunWith(MockitoJUnitRunner.class)
public class OperatorServiceTest {

    @InjectMocks
    private OperatorService operatorService;

    @Mock
    private OperatorRepository operatorRepository;

    private final String name = "name";
    private final String description = "description";
    private final String url = "http://google.com";
    private final Position position = new Position(10, 10);

    @Test
    public void testFetchAllOperatorsEmptyResult() {
        when(operatorRepository.list()).thenReturn(Collections.emptyList());

        Collection<Operator> resultList = operatorService.fetchAllOperators();

        assertTrue(resultList.isEmpty());
        verify(operatorRepository).list();
        verifyNoMoreInteractions(operatorRepository);
    }

    @Test
    public void testFetchAllOperatorsNotEmptyResult() {
        Operator operator1 = new Operator("operator1");
        Operator operator2 = new Operator("operator2");

        List<Operator> operatorList = Arrays.asList(operator1, operator2);

        when(operatorRepository.list()).thenReturn(Arrays.asList(operator1, operator2));

        Collection<Operator> resultList = operatorService.fetchAllOperators();

        assertEquals(2, resultList.size());
        assertTrue(resultList.containsAll(operatorList));
        verify(operatorRepository).list();
        verifyNoMoreInteractions(operatorRepository);
    }

    @Test
    public void testFetchOperatorSuccess() {
        Operator operator1 = new Operator("operator1");

        when(operatorRepository.get(anyInt())).thenReturn(Optional.of(operator1));

        Operator operator = operatorService.fetchOperator(1);

        assertEquals(operator1, operator);
        verify(operatorRepository).get(anyInt());
        verifyNoMoreInteractions(operatorRepository);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFetchOperatorOperatorIdLessThenZero() {
        operatorService.fetchOperator(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFetchOperatorOperatorIdZero() {
        operatorService.fetchOperator(0);
    }

    @Test(expected = NotFoundException.class)
    public void testFetchOperatorNotFound() {
        when(operatorRepository.get(anyInt())).thenReturn(Optional.empty());

        operatorService.fetchOperator(1);
    }

    @Test
    public void testCreateOperatorSuccess() {
        when(operatorRepository.store(any(Operator.class))).then(returnsFirstArg());

        Operator operator = operatorService.createOperator(name, description, url, position);

        assertEquals(name, operator.getName());
        assertEquals(description, operator.getDescription());
        assertEquals(url, operator.getWebsite().toString());
        assertEquals(position, operator.getPosition());

        verify(operatorRepository).store(operator);
        verifyNoMoreInteractions(operatorRepository);
    }

    @Test(expected = NullPointerException.class)
    public void testCreateOperatorNameNull() {
        operatorService.createOperator(null, description, url, position);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateOperatorNameEmpty() {
        operatorService.createOperator("", description, url, position);
    }

    @Test(expected = BadRequestException.class)
    public void testCreateOperatorWebsiteNull() {
        operatorService.createOperator(name, description, (String) null, position);
    }

    @Test(expected = BadRequestException.class)
    public void testCreateOperatorWebsiteMalformed() {
        operatorService.createOperator(name, description, "wrong web site url", position);
    }

    @Test
    public void testCreateOperatorEmptyFields() {
        when(operatorRepository.store(any(Operator.class))).then(returnsFirstArg());

        Operator operator = operatorService.createOperator(name, "", (URL) null, null);

        assertEquals(name, operator.getName());
        assertNull(operator.getDescription());
        assertNull(operator.getWebsite());
        assertNull(operator.getPosition());

        verify(operatorRepository).store(operator);
        verifyNoMoreInteractions(operatorRepository);
    }

    @Test
    public void testUpdateOperatorSuccess() throws Exception {
        when(operatorRepository.get(1)).thenReturn(Optional.of(initialOperator()));
        when(operatorRepository.store(any(Operator.class))).then(invocation ->  {
                Object firstParam = invocation.getArguments()[0];
                if (firstParam instanceof Operator) {
                    Operator updatedOperator = (Operator) firstParam;

                    assertEquals(name, updatedOperator.getName());
                    assertEquals(description, updatedOperator.getDescription());
                    assertEquals(url, updatedOperator.getWebsite().toString());
                    assertEquals(position, updatedOperator.getPosition());
                }
                return null;
        });

        operatorService.updateOperator(1, name, description, url, position);

        verify(operatorRepository).get(1);
        verify(operatorRepository).store(any(Operator.class));
        verifyNoMoreInteractions(operatorRepository);
    }

    @Test
    public void testUpdateOperatorNameNull() throws Exception {
        Operator initialOperator = initialOperator();
        when(operatorRepository.get(1)).thenReturn(Optional.of(initialOperator));
        when(operatorRepository.store(any(Operator.class))).then(invocation ->  {
            Object firstParam = invocation.getArguments()[0];
            if (firstParam instanceof Operator) {
                Operator updatedOperator = (Operator) firstParam;

                assertEquals(initialOperator.getName(), updatedOperator.getName());
                assertEquals(description, updatedOperator.getDescription());
                assertEquals(url, updatedOperator.getWebsite().toString());
                assertEquals(position, updatedOperator.getPosition());

                return updatedOperator;
            }
            return null;
        });

        operatorService.updateOperator(1, null, description, url, position);

        verify(operatorRepository).get(1);
        verify(operatorRepository).store(any(Operator.class));
        verifyNoMoreInteractions(operatorRepository);
    }

    @Test
    public void testUpdateOperatorNameEmpty() throws Exception {
        Operator initialOperator = initialOperator();
        when(operatorRepository.get(1)).thenReturn(Optional.of(initialOperator));
        when(operatorRepository.store(any(Operator.class))).then(invocation ->  {
            Object firstParam = invocation.getArguments()[0];
            if (firstParam instanceof Operator) {
                Operator updatedOperator = (Operator) firstParam;

                assertEquals(initialOperator.getName(), updatedOperator.getName());
                assertEquals(description, updatedOperator.getDescription());
                assertEquals(url, updatedOperator.getWebsite().toString());
                assertEquals(position, updatedOperator.getPosition());

                return updatedOperator;
            }
            return null;
        });

        operatorService.updateOperator(1, "", description, url, position);

        verify(operatorRepository).get(1);
        verify(operatorRepository).store(any(Operator.class));
        verifyNoMoreInteractions(operatorRepository);
    }

    @Test(expected = BadRequestException.class)
    public void testUpdateOperatorWebsiteNull() throws Exception {
        when(operatorRepository.get(1)).thenReturn(Optional.of(initialOperator()));

        operatorService.updateOperator(1, name, description, (String) null, position);

        verify(operatorRepository).get(1);
        verifyNoMoreInteractions(operatorRepository);
    }

    @Test(expected = BadRequestException.class)
    public void testUpdateOperatorWebsiteMalformed() throws Exception {
        when(operatorRepository.get(1)).thenReturn(Optional.of(initialOperator()));

        operatorService.updateOperator(1, name, description, "some bad url", position);

        verify(operatorRepository).get(1);
        verifyNoMoreInteractions(operatorRepository);
    }

    @Test
    public void testUpdateOperatorEmptyFields() throws Exception {
        Operator initialOperator = initialOperator();
        when(operatorRepository.get(1)).thenReturn(Optional.of(initialOperator));
        when(operatorRepository.store(any(Operator.class))).then(invocation ->  {
            Object firstParam = invocation.getArguments()[0];
            if (firstParam instanceof Operator) {
                Operator updatedOperator = (Operator) firstParam;

                assertEquals(initialOperator.getName(), updatedOperator.getName());
                assertEquals(initialOperator.getDescription(), updatedOperator.getDescription());
                assertEquals(initialOperator.getWebsite(), updatedOperator.getWebsite());
                assertEquals(initialOperator.getPosition(), updatedOperator.getPosition());

                return updatedOperator;
            }
            return null;
        });

        operatorService.updateOperator(1, "", "", (URL) null, null);

        verify(operatorRepository).get(1);
        verify(operatorRepository).store(any(Operator.class));
        verifyNoMoreInteractions(operatorRepository);
    }

    private Operator initialOperator() throws Exception {
        Operator operator = new Operator("previousName");
        operator.setDescription("previousDescription");
        operator.setWebsite(new URL("http://vk.com"));
        operator.setPosition(new Position(15, 15));
        return operator;
    }
}
