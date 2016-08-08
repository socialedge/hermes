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
                            var arrivalHour = stations[stationIndex].arrival.hour;
                            if (!(arrivalHour in arrivals)) arrivals[arrivalHour] = [];

                            var arrivalMinute = stations[stationIndex].arrival.minute + "";
                            if (arrivalMinute.length == 1) arrivalMinute = "0" + arrivalMinute;

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
