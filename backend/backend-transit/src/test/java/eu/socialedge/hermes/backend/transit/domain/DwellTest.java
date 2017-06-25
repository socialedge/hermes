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
 */

package eu.socialedge.hermes.backend.transit.domain;

import org.junit.Test;

import java.time.Duration;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DwellTest {

    @Test
    public void validatesFromToTimesOrder() {
        assertThatThrownBy(() -> {
            Dwell.equallyLikely(LocalTime.MAX, LocalTime.NOON, Duration.ofDays(1));
        }).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("after to");

        assertThatThrownBy(() -> {
            Dwell.regular(LocalTime.MAX, LocalTime.NOON, Duration.ofDays(1));
        }).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("after to");

        assertThatThrownBy(() -> {
            Dwell.hail(LocalTime.MAX, LocalTime.NOON, Duration.ofDays(1), 0.5);
        }).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("after to");
    }

    @Test
    public void validatesZeroDuration() {
        assertThatThrownBy(() -> {
            Dwell.hail(LocalTime.NOON, LocalTime.MAX, Duration.ZERO, 0);
        }).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("zero-timed");

        assertThatThrownBy(() -> {
            Dwell.regular(LocalTime.NOON, LocalTime.MAX, Duration.ZERO);
        }).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("zero-timed");

        assertThatThrownBy(() -> {
            Dwell.equallyLikely(LocalTime.NOON, LocalTime.MAX, Duration.ZERO);
        }).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("zero-timed");

        assertThatThrownBy(() -> {
            Dwell.allDayRegular(Duration.ZERO);
        }).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("zero-timed");

        assertThatThrownBy(() -> {
            Dwell.allDayEquallyLikely(Duration.ZERO);
        }).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("zero-timed");

        assertThatThrownBy(() -> {
            Dwell.allDayHail(Duration.ZERO, 0);
        }).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("zero-timed");
    }

    @Test
    public void validatesProbabilityToBeFrom0to1() {
        assertThatThrownBy(() -> {
            Dwell.hail(LocalTime.NOON, LocalTime.MAX, Duration.ofHours(1), -0.1);
        }).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("probability");

        assertThatThrownBy(() -> {
            Dwell.hail(LocalTime.NOON, LocalTime.MAX, Duration.ofHours(1), 1.1);
        }).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("probability");

        assertThatThrownBy(() -> {
            Dwell.allDayHail(Duration.ofHours(1), -0.1);
        }).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("probability");

        assertThatThrownBy(() -> {
            Dwell.allDayHail(Duration.ofHours(1), 1.1);
        }).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("probability");
    }

    @Test
    public void validatesDurationToBeShorterThanValidityInterval() {
        assertThatThrownBy(() -> {
            Dwell.hail(LocalTime.NOON, LocalTime.MAX,
                Duration.between(LocalTime.NOON, LocalTime.MAX).plusNanos(1), 0);
        }).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("validity interval");

        assertThatThrownBy(() -> {
            Dwell.regular(LocalTime.NOON, LocalTime.MAX,
                Duration.between(LocalTime.NOON, LocalTime.MAX).plusNanos(1));
        }).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("validity interval");

        assertThatThrownBy(() -> {
            Dwell.equallyLikely(LocalTime.NOON, LocalTime.MAX,
                Duration.between(LocalTime.NOON, LocalTime.MAX).plusNanos(1));
        }).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("validity interval");
    }

    @Test
    public void setsCorrectAllDayValues() {
        assertEquals(LocalTime.MIN, Dwell.allDayEquallyLikely(Duration.ofMinutes(1)).getFrom());
        assertEquals(LocalTime.MAX, Dwell.allDayEquallyLikely(Duration.ofMinutes(1)).getTo());

        assertEquals(LocalTime.MIN, Dwell.allDayHail(Duration.ofMinutes(1), 0.1).getFrom());
        assertEquals(LocalTime.MAX, Dwell.allDayHail(Duration.ofMinutes(1), 0.1).getTo());

        assertEquals(LocalTime.MIN, Dwell.allDayRegular(Duration.ofMinutes(1)).getFrom());
        assertEquals(LocalTime.MAX, Dwell.allDayRegular(Duration.ofMinutes(1)).getTo());
    }

    @Test
    public void setsCorrectEquallyLikelyProbability() {
        assertEquals(0.5, Dwell.equallyLikely(LocalTime.NOON, LocalTime.MAX,
            Duration.ofMinutes(1)).getProbability(), 0.01);

        assertEquals(0.5, Dwell.allDayEquallyLikely(Duration.ofMinutes(1)).getProbability(), 0.01);
    }

    @Test
    public void detectsTailHeadOverlapping() {
        // 1  MIN 0 -------- 0 NOON
        // 2     NOON-1 0 ------ 0 MAX
        assertTrue(Dwell.equallyLikely(LocalTime.MIN, LocalTime.NOON, Duration.ofMinutes(1)).overlaps(          // 1
                    Dwell.equallyLikely(LocalTime.NOON.minusHours(1), LocalTime.MAX, Duration.ofMinutes(1))));  // 2
    }

    @Test
    public void detectsHeadTailOverlapping() {
        // 1      NOON 0 ------ 0 MAX
        // 2  MIN 0 ------ 0 NOON+1

        assertTrue(Dwell.equallyLikely(LocalTime.NOON, LocalTime.MAX, Duration.ofMinutes(1)).overlaps( // 1
            Dwell.equallyLikely(LocalTime.MIN, LocalTime.NOON.plusHours(1), Duration.ofMinutes(1))));  // 2
    }

    @Test
    public void detectsThisIncludingOverlapping() {
        // 1  MIN 0 ---------- 0 MAX
        // 2  MIN+1  0 --- 0 NOON

        assertTrue(Dwell.equallyLikely(LocalTime.MIN, LocalTime.MAX, Duration.ofMinutes(1)).overlaps(  // 1
            Dwell.equallyLikely(LocalTime.MIN.plusHours(1), LocalTime.NOON, Duration.ofMinutes(1))));  // 2
    }

    @Test
    public void detectsThatIncludingOverlapping() {
        // 1  MIN+1  0 --- 0 NOON
        // 2  MIN 0 ---------- 0 MAX

        assertTrue(Dwell.equallyLikely(LocalTime.MIN.plusHours(1), LocalTime.NOON, Duration.ofMinutes(1)).overlaps( // 1
            Dwell.equallyLikely(LocalTime.MIN, LocalTime.MAX, Duration.ofMinutes(1))));                             // 2
    }

    @Test
    public void checksApplicabilityCorrectly() {
        // time      NOON v
        // dwell  MIN 0 ------ 0 MAX
        assertTrue(Dwell.equallyLikely(LocalTime.MIN, LocalTime.MAX, Duration.ofMinutes(1))
            .applies(LocalTime.NOON));

        // time   MIN v
        // dwell  MIN 0 ------ 0 MAX
        assertTrue(Dwell.equallyLikely(LocalTime.MIN, LocalTime.MAX, Duration.ofMinutes(1))
            .applies(LocalTime.MIN));

        // time            MAX v
        // dwell  MIN 0 ------ 0 MAX
        assertTrue(Dwell.equallyLikely(LocalTime.MIN, LocalTime.MAX, Duration.ofMinutes(1))
            .applies(LocalTime.MAX));

        // time               MAX v
        // dwell  MIN 0 ------ 0 MAX-1
        assertFalse(Dwell.equallyLikely(LocalTime.MIN, LocalTime.MAX.minusSeconds(1), Duration.ofMinutes(1))
            .applies(LocalTime.MAX));

        // time   MIN v
        // dwell  MIN+1 0 ------ 0 MAX
        assertFalse(Dwell.equallyLikely(LocalTime.MIN.plusSeconds(1), LocalTime.MAX.minusSeconds(1), Duration.ofMinutes(1))
            .applies(LocalTime.MIN));
    }
}
