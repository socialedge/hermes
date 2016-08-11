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
package eu.socialedge.hermes.util;

public class Numbers {

    private Numbers() {
        throw new AssertionError("No " + Numbers.class + " instances for you!");
    }
    
    public static int requireBetween(int number, int from, int to, String msg) {
        if (number < from || number > to)
            throw new IllegalArgumentException(msg);

        return number;
    }

    public static int requireBetween(int number, int from, int to) {
        return requireBetween(number, from, to, String.format("Number between (inclusive) %d and %d is required. " +
                "Found = %d", from, to, number));
    }

    public static float requireBetween(float number, float from, float to, String msg) {
        if (number < from || number > to)
            throw new IllegalArgumentException(msg);

        return number;
    }

    public static float requireBetween(float number, float from, float to) {
        return requireBetween(number, from, to, String.format("Number between (inclusive) %f and %f is required. " +
                "Found = %f", from, to, number));
    }

    public static double requireBetween(double number, double from, double to, String msg) {
        if (number < from || number > to)
            throw new IllegalArgumentException(msg);

        return number;
    }

    public static double requireBetween(double number, double from, double to) {
        return requireBetween(number, from, to, String.format("Number between (inclusive) %f and %f is required. " +
                "Found = %f", from, to, number));
    }

    public static <T extends Number & Comparable<T>> T requireBetween(T number, T from, T to, String msg) {
        if (number == null || number.compareTo(from) < 0 || number.compareTo(to) > 0)
            throw new IllegalArgumentException(msg);

        return number;
    }

    public static <T extends Number & Comparable<T>> T requireBetween(T number, T from, T to) {
        return requireBetween(number, from, to, String.format("Number between (inclusive) %s and %s is required. " +
                "Found = %s", from, to, number));
    }

    public static <T extends Number & Comparable<T>> T requireExclusiveBetween(T number, T from, T to, String msg) {
        if(number == null || number.compareTo(from) <= 0 || number.compareTo(to) >= 0)
            throw new IllegalArgumentException(msg);

        return number;
    }

    public static <T extends Number & Comparable<T>> T requireExclusiveBetween(T number, T from, T to) {
        return requireExclusiveBetween(number, from, to, String.format("Number between (exclusive) %s and %s is required. " +
                "Found = %s", from, to, number));
    }

    public static int requireExclusiveBetween(int number, int from, int to, String msg) {
        if (number <= from || number >= to)
            throw new IllegalArgumentException(msg);

        return number;
    }

    public static int requireExclusiveBetween(int number, int from, int to) {
        return requireExclusiveBetween(number, from, to, String.format("Number between (exclusive) %d and %d is required. " +
                "Found = %d", from, to, number));
    }

    public static float requireExclusiveBetween(float number, float from, float to, String msg) {
        if (number <= from || number >= to)
            throw new IllegalArgumentException(msg);

        return number;
    }

    public static float requireExclusiveBetween(float number, float from, float to) {
        return requireExclusiveBetween(number, from, to, String.format("Number between (exclusive) %f and %f is required. " +
                "Found = %f", from, to, number));
    }

    public static double requireExclusiveBetween(double number, double from, double to, String msg) {
        if (number <= from || number >= to)
            throw new IllegalArgumentException(msg);

        return number;
    }

    public static double requireExclusiveBetween(double number, double from, double to) {
        return requireExclusiveBetween(number, from, to, String.format("Number between (exclusive) %f and %f is required. " +
                "Found = %f", from, to, number));
    }
}
