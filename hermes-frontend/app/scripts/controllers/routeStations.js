'use strict';

angular.module('hermesApp').controller('RouteStationsCtrl', function ($scope, $http, $routeParams, $q, env) {
    $scope.lineId = $routeParams.lineId;
    $scope.routeId = $routeParams.routeId;

    $http.get(env.backendBaseUrl + "/schedules?routeId=" + $routeParams.routeId)
            .success(function(schedules) {
                var timetables = [];

                $.each(schedules, function(scheduleIndex, schedule) {

                    var _promises = [];
                    $.each(schedule.tripIds, function(tripIdIndex, tripId) {
                        schedule.trips = [];
                        var _promise =
                            $http.get(env.backendBaseUrl + "/schedules/" + schedule.id + "/trips/" + tripId)
                                    .success(function(trip) {
                                        schedule.trips.push(trip);
                                    });
                        _promises.push(_promise);
                    });

                    $q.all(_promises).then(() => {
                        delete schedule.tripIds;

                        var arrivals = {};
                        $.each(schedule.trips, function(tripIndex, trip) {
                            $.each(trip.stops, function(stopIndex, stop) {
                                if (stop.stationId == $routeParams.stationId) {
                                    var arrivalHour = stop.arrival.split(":")[0];
                                    var arrivalMinute = stop.arrival.split(":")[1];

                                    if (!(arrivalHour in arrivals)) arrivals[arrivalHour] = [];
                                    arrivals[arrivalHour].push(arrivalMinute);
                                }
                            });
                        });

                        timetables.push({
                            "name": schedule.name,
                            "availability": schedule.scheduleAvailability,
                            "arrivals": arrivals
                        });

                        $scope.timetables = timetables;
                    });
                });
            });
});
