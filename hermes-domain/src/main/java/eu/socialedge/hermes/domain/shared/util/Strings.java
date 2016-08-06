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
package eu.socialedge.hermes.domain.shared.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.StringJoiner;
import java.util.stream.Stream;

public class Strings {

    private static final String DEFAULT_JOIN_DELIMITER = ",";

    private Strings() {
        throw new AssertionError("No " + Strings.class + " instances for you!");
    }

    public static String join(String delimiter, Stream<String> stringsStream) {
        StringJoiner joiner = new StringJoiner(delimiter);

        stringsStream.forEach(joiner::add);
        return joiner.toString();
    }

    public static String join(Stream<String> stringsStream) {
        return join(DEFAULT_JOIN_DELIMITER, stringsStream);
    }

    public static String join(String delimiter, String... strings) {
        return join(delimiter, Arrays.stream(strings));
    }

    public static String join(String... strings) {
        return join(DEFAULT_JOIN_DELIMITER, strings);
    }

    public static String join(String delimiter, Object... objects) {
        return join(delimiter, Arrays.stream(objects).map(String::valueOf));
    }

    public static String join(Object... objects) {
        return join(DEFAULT_JOIN_DELIMITER, objects);
    }

    public static String join(String delimiter, Collection<?> objects) {
        return join(delimiter, objects.stream()
                                      .filter(o -> o != null)
                                      .map(String::valueOf));
    }

    public static String join(Collection<?> objects) {
        return join(DEFAULT_JOIN_DELIMITER, objects);
    }

    public static boolean isBlank(String str) {
        return str == null || str.length() == 0;
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    public static String requireNotBlank(String str, String msg) {
        if (isBlank(str))
            throw new IllegalArgumentException(msg);

        return str;
    }

    public static String requireNotBlank(String str) {
        return requireNotBlank(str, "Not blank string is required.");
    }

    public static String requireLongerThan(String str, int minLetters, String msg) {
        if (requireNotBlank(str).length() < minLetters)
            throw new IllegalArgumentException(msg);

        return str;
    }

    public static String requireLongerThan(String str, int minLetters) {
        return requireLongerThan(str, minLetters, String.format("String longer than %s is required. " +
                "Found = (%s).length = %d", minLetters, str, str.length()));
    }

    public static String requireShorterThan(String str, int maxLetters, String msg) {
        if (requireNotBlank(str).length() > maxLetters)
            throw new IllegalArgumentException(msg);

        return str;
    }

    public static String requireShorterThan(String str, int maxLetters) {
        return requireShorterThan(str, maxLetters, String.format("String shorter than %s is required. " +
                "Found = (%s).length = %d", maxLetters, str, str.length()));
    }
}
