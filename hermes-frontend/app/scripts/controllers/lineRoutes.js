'use strict';

angular.module('hermesApp').controller('LineRoutesCtrl', function ($scope, $http, $routeParams, $q, env) {
    $http.get(env.backendBaseUrl + "/lines/" + $routeParams.lineId)
        .success(function(data) {
            $scope.lineId = $routeParams.lineId;

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
                    $.each(route.waypoints, function(index, stationId) {
                       var _promise = $http.get(env.backendBaseUrl + "/stations/" + stationId)
                            .success(function(data) {
                                route.waypoints[index] = data;
                            });
                        _promises.push(_promise);
                    });
                });

                $q.all(_promises).then(() => {
                    $scope.routes = routes;
                });
            });
        });
});