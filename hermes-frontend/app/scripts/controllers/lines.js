'use strict';

angular.module('hermesApp').controller('LinesCtrl', function ($scope, $http, env) {
    $http.get(env.backendBaseUrl + "/lines?detailed")
        .success(function(data) {
            var _lines = {
                trolleys: [],
                buses: [],
                trams: [],
                trains: []
            };

            $.each(data, function(index, line) {
                if (line.transportType === "BUS")
                    _lines.buses.push(line);
                else if (line.transportType === "TROLLEY")
                    _lines.trolleys.push(line);
                else if (line.transportType === "TRAM")
                    _lines.trams.push(line);
                else if (line.transportType === "TRAIN")
                    _lines.trains.push(line);
                else
                    throw new Error("Unknown transportType = " + line.transportType);
            });

            console.log(_lines);

            $scope.lines = _lines;
        });
});