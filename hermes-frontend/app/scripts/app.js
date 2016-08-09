'use strict';

var $angular = angular.module('hermesApp', ['ngAnimate', 'ngCookies', 'ngResource', 'ngRoute', 'ngSanitize', 'ngTouch']);

$angular.config(function ($routeProvider) {
    $routeProvider
        .when('/', {
            templateUrl: 'views/index.html',
            controller: 'IndexCtrl',
            controllerAs: 'index'
        })
        .when('/lines/:lineId', {
            templateUrl: 'views/lineRoutes.html',
            controller: 'LineRoutesCtrl',
            controllerAs: 'lineRoutes'
        })
        .when('/lines/:lineId/routes/:routeId/stations/:stationId', {
            templateUrl: 'views/routeStations.html',
            controller: 'RouteStationsCtrl',
            controllerAs: 'routeStations'
        })
        .otherwise({
            redirectTo: '/'
        });
});

$angular.constant("env", function (o) {
    o.backendBaseUrl = o.backend + "/api/" + o.apiVersion;
    return o;
}({
    backend: "http://localhost:9999",
    apiVersion: "v1.1"
}));

$angular.directive('scrollToItem', function () {
    return {
        restrict: 'A',
        scope: {
            scrollTo: "@"
        },
        link: function (scope, $elm) {
            $elm.on('click', function () {
                var tries = 3;
                var t = setInterval(function () {
                    if ($(scope.scrollTo).offset()) {
                        $('html,body').animate({scrollTop: $(scope.scrollTo).offset().top}, 300);
                        clearInterval(t);
                    }
                    if (!--tries) {
                        clearInterval(t);
                        throw new Error("scrollToItem not found = " + scope.scrollTo);
                    }
                }, 50);
            });
        }
    }
});


