'use strict';

var $angular = angular.module('hermesApp', ['ngAnimate', 'ngCookies', 'ngResource', 'ngRoute', 'ngSanitize', 'ngTouch']);

$angular.config(function ($routeProvider, $locationProvider) {
  $locationProvider.hashPrefix('');

  $routeProvider
    .when('/', {
      templateUrl: '../views/index.html',
      controller: 'IndexCtrl'
    })
    .when('/lines', {
      templateUrl: '../views/lines.html',
      controller: 'LinesCtrl',
      controllerAs: 'lines'
    })
    .when('/schedules', {
      templateUrl: '../views/schedules.html',
      controller: 'SchedulesCtrl',
      controllerAs: 'schedules'
    })
    .when('/schedules/:scheduleId', {
      templateUrl: '../views/schedule.html',
      controller: 'ScheduleCtrl',
      controllerAs: 'schedule'
    })
    .when('/stations', {
      templateUrl: '../views/stations.html',
      controller: 'StationsCtrl',
      controllerAs: 'stations'
    })
    .otherwise({
      redirectTo: '/'
    });
});
