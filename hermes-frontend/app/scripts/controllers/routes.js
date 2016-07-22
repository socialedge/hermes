'use strict';

angular.module('hermesApp').controller('RoutesCtrl', function ($scope, $http, $routeParams, env) {
    $http.get(env.backendBaseUrl + "/lines/" + $routeParams.routeId + "/routes")
        .success(function(data) {
            $scope.routes = data;
        });
});