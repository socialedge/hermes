'use strict';

angular.module('hermesApp').controller('LineRoutesCtrl', function ($scope, $http, $routeParams, $q, env) {
    $scope.lineId = $routeParams.lineId;

    $http.get(env.backendBaseUrl + "/lines/" + $routeParams.lineId + "/routes")
        .success(function(routes) {

            var _promises = [];
            $.each(routes, function(index, route) {
                route.waypoints = [];
                $.each(route.stationIds, function(index, stationId) {
                    var _promise = $http.get(env.backendBaseUrl + "/stations/" + stationId)
                                            .success(function(data) {
                                                route.waypoints.push(data);
                                            });
                    _promises.push(_promise);
                });
                delete route.stationIds;
            });

            $q.all(_promises).then(() => {
                $scope.routes = routes;
            });
        });
});