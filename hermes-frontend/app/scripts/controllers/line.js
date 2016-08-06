'use strict';

angular.module('hermesApp').controller('LineCtrl', function ($scope, $http, $routeParams, $q, env) {
    $http.get(env.backendBaseUrl + "/lines/" + $routeParams.lineCode)
        .success(function(data) {
            var routeIds = data.routeIds;

            var routes = [];
            var _promises = [];
            $.each(routeIds, function(index, routeId) {
                var _promise = $http.get(env.backendBaseUrl + "/routes/" + routeId)
                    .success(function(data) {
                        routes.push(data);
                    });

                _promises.push(_promise);
            });

            $q.all(_promises).then(() => {
                _promises = [];
                $.each(routes, function(index, route) {
                    $.each(route.waypoints, function(index, waypoint) {
                       var _promise = $http.get(env.backendBaseUrl + "/stations/" + waypoint.stationId)
                            .success(function(data) {
                                delete waypoint.stationId;
                                waypoint.station = data;
                            });
                        _promises.push(_promise);
                    });
                });

                $q.all(_promises).then(() => {
                    $scope.routes = routes;
                    console.log(routes);
                });
            });
        });
});