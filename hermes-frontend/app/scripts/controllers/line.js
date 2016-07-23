'use strict';

angular.module('hermesApp').controller('LineCtrl', function ($scope, $http, $routeParams, env) {
    $http.get(env.backendBaseUrl + "/lines/" + $routeParams.lineCode + "/routes")
        .success(function(data) {
            $scope.lineCode = $routeParams.lineCode;
            $scope.routes = data;
        });
});