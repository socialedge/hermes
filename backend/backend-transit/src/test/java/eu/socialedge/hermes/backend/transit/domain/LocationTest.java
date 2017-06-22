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

import javafx.util.Pair;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LocationTest {
    private static final Set<Pair<Double, Double>> badLocationCoords =
        new HashSet<Pair<Double, Double>>() {{
            add(new Pair<>(90D, 181D));
            add(new Pair<>(91D, -181D));
            add(new Pair<>(91D, -180D));
            add(new Pair<>(-91D, 180D));
        }};

    private static final Set<Pair<Double, Double>> goodLocationCoords = new
        HashSet<Pair<Double, Double>>() {{
            add(new Pair<>(90D, 180D));
            add(new Pair<>(0D, 0D));
            add(new Pair<>(-90D, -180D));
            add(new Pair<>(-90D, 180D));
            add(new Pair<>(90D, -180D));
            add(new Pair<>(50D, -50D));
        }};


    @Test
    public void shouldThrowExceptionForBadConstructorValues() {
        badLocationCoords.forEach(pair -> {
            assertThatThrownBy(() -> new Location(pair.getKey(), pair.getValue()))
                .isInstanceOf(IllegalArgumentException.class);
        });
    }

    @Test
    public void shouldNotThrowExceptionForGoodConstructorValues() {
        goodLocationCoords.forEach(pair -> {
            new Location(pair.getKey(), pair.getValue());
        });
    }
}
