'use strict';

angular.module('hermesApp').controller('RouteStationsCtrl', function ($scope, $http, $routeParams, $q, env) {
    $http.get(env.backendBaseUrl + "/schedules?routeId=" + $routeParams.routeId)
        .success(function(data) {
            var schedules = [];

            $.each(data, function(scheduleIndex, schedule) {

                var arrivals = {};
                $.each(schedule.trips, function(stationsIndex, stations) {
                    $.each(stations, function(stationIndex, station) {
                        if (station.stationId == $routeParams.stationId) {
                            var arrivalTime = stations[stationIndex].arrival;
                            var arrivalHour = arrivalTime.split(":")[0];
                            var arrivalMinute = arrivalTime.split(":")[1];

                            if (!(arrivalHour in arrivals)) arrivals[arrivalHour] = [];
                            arrivals[arrivalHour].push(arrivalMinute);
                        }
                    });
                });

                schedules.push({
                    "description": schedule.description,
                    "availability": schedule.scheduleAvailability,
                    "arrivals": arrivals
                });
            });

            console.log(schedules);

            $scope.lineId = $routeParams.lineId;
            $scope.routeId = $routeParams.routeId;
            $scope.schedules = schedules;
        });
});
