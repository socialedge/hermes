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

package eu.socialedge.hermes.backend.export;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@AllArgsConstructor
public class StationScheduleTemplateDto {
    private String lineName;
    private String vehicleType;
    private List<String> followingStations;
    private String firstStation;
    private String currentStation;
    private String availability;
    private Map<Integer, Set<Integer>> times;
    private String startDate;
}
