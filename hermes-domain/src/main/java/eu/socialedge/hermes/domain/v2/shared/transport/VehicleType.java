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
package eu.socialedge.hermes.domain.v2.shared.transport;

import eu.socialedge.hermes.domain.ext.ValueObject;

/**
 * Describes the type of transportation using
 * Hierarchical Vehicle Type (HVT) codes from the
 * European TPEG standard.
 *
 * @see <a href="https://goo.gl/U6cm9a">Google Transit APIs
 * > Static Transit > Extended GTFS Route Types</a>
 */
@ValueObject
public enum VehicleType {
    RAILWAY(100),
    HIGH_SPEED_RAIL(101),
    LONG_DISTANCE_TRAINS(102),
    INTER_REGIONAL_RAIL(103),
    SLEEPER_RAIL(105),
    REGIONAL_RAIL(106),
    TOURIST_RAILWAY(107),
    RAIL_SHUTTLE(108),
    SUBURBAN_RAILWAY(109),
    COACH(200),
    INTERNATIONAL_COACH(201),
    NATIONAL_COACH(202),
    REGIONAL_COACH(204),
    COMMUTER_COACH(208),
    URBAN_RAILWAY(400),
    METRO(401),
    UNDERGROUND(402),
    MONORAIL(405),
    BUS(700),
    REGIONAL_BUS(701),
    EXPRESS_BUS(702),
    LOCAL_BUS(704),
    TROLLEYBUS(800),
    TRAM(900),
    WATER_TRANSPORT(1000),
    TELECABIN(1300),
    FUNICULAR(1400),
    COMMUNAL_TAXI(1501),
    MISCELLANEOUS(1700),
    CABLE_CAR(1701),
    HORSE_DRAWN_CARRIAGE(1702);

    private final int value;

    VehicleType(int i) {
        this.value = i;
    }

    public int value() {
        return value;
    }
}
