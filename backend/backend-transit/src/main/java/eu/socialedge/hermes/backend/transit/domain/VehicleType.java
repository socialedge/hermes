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

import lombok.val;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Describes the type of transportation the Hermes supports
 *
 * @see <a href="https://goo.gl/XiMs19">
 *     Google Maps API - Directions Service - Vehicle Type</a>
 */
public enum VehicleType {

    BUS,

    INTERCITY_BUS,

    TROLLEYBUS,

    /**
     * Share taxi is a kind of bus with the ability to drop
     * off and pick up passengers anywhere on its route.
     */
    SHARE_TAXI,

    /**
     * All other vehicles will return this type.
     */
    OTHER;

    public static VehicleType fromNameOrOther(String name) {
        if (isBlank(name))
            return VehicleType.OTHER;

        for (val type : values()) {
            if (type.name().equalsIgnoreCase(name))
                return type;
        }

        return VehicleType.OTHER;
    }
}
