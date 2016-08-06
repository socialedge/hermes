'use strict';

angular.module('hermesApp').controller('LinesCtrl', function ($scope, $http, env) {
    $http.get(env.backendBaseUrl + "/lines")
        .success(function(data) {
            var _lines = {
                trolleys: [],
                buses: [],
                trams: [],
                trains: []
            };

            $.each(data, function(index, line) {
                if (line.vehicleType === "BUS")
                    _lines.buses.push(line);
                else if (line.vehicleType === "TROLLEYBUS")
                    _lines.trolleys.push(line);
                else if (line.vehicleType === "TRAM")
                    _lines.trams.push(line);
                else if (line.vehicleType === "TRAIN")
                    _lines.trains.push(line);
                else
                    throw new Error("Unknown transportType = " + line.transportType);
            });

            $scope.lines = _lines;
        });
});