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

import eu.socialedge.hermes.backend.transit.domain.service.Line;
import lombok.val;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static eu.socialedge.hermes.backend.gen.DomainTestUtils.createLine;
import static eu.socialedge.hermes.backend.gen.DomainTestUtils.createSchedule;
import static eu.socialedge.hermes.backend.gen.DomainTestUtils.createStation;

@RunWith(MockitoJUnitRunner.class)
public class SchedulePdfGeneratorTest {

    @Mock
    private PdfGenerator pdfGenerator;

    @InjectMocks
    private SchedulePdfGenerator schedulePdfGenerator;

    private Line line = createLine(5, 5);

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhenSchedulesDontMatchTheLine() {
        val schedule1 = createSchedule(line, 10);
        val schedule2 = createSchedule(createLine(4,4), 3);

        schedulePdfGenerator.generateSingleLineStationPdf(line, createStation(), Arrays.asList(schedule1, schedule2));
    }
}
