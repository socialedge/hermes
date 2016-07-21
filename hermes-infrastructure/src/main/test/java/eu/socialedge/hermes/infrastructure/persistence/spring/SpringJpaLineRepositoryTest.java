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
package eu.socialedge.hermes.infrastructure.persistence.spring;

import eu.socialedge.hermes.domain.infrastructure.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringTestConfig.class, loader = AnnotationConfigContextLoader.class)
public class SpringJpaLineRepositoryTest {
    @Autowired private LineRepository lineRepository;
    @Autowired private OperatorRepository operatorRepository;

    private Operator operator1;
    private Operator operator2;
    private Line line1;
    private Line line2;

    @Before
    public void setUp() {
        operator1 = new Operator("operator1");
        operatorRepository.store(operator1);

        operator2 = new Operator("operator2");
        operatorRepository.store(operator2);

        line1 = new Line("line1", TransportType.BUS);
        line1.setOperator(operator1);
        lineRepository.store(line1);

        line2 = new Line("line2", TransportType.BUS);
        line2.setOperator(operator2);
        lineRepository.store(line2);
    }

    @Test
    public void testFindByOperatorOneLine() {
        Collection<Line> lines = lineRepository.findByOperatorId(operator1.getId());

        assertEquals(1, lines.size());
        assertTrue(lines.contains(line1));
    }

    @Test
    public void testFindByOperatorTwoLines() {
        line2.setOperator(operator1);
        lineRepository.store(line2);

        Collection<Line> lines = lineRepository.findByOperatorId(operator1.getId());

        assertEquals(2, lines.size());
        assertTrue(lines.contains(line1));
        assertTrue(lines.contains(line2));
    }


    @After
    public void tearDown() {
        lineRepository.remove(line1);
        lineRepository.remove(line2);
        operatorRepository.remove(operator1);
        operatorRepository.remove(operator2);
    }
}
