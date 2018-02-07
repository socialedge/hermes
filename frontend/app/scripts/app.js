'use strict';

var $angular = angular.module('hermesApp', ['ngAnimate', 'ngCookies', 'ngResource', 'ngRoute', 'ngSanitize', 'ngTouch', 'ui.bootstrap', 'ui.sortable']);

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
    .when('/schedule', {
      templateUrl: '../views/schedule.html',
      controller: 'ScheduleCtrl',
      controllerAs: 'schedule'
    })
    .when('/schedule/edit', {
      templateUrl: '../views/editSchedule.html',
      controller: 'EditScheduleCtrl',
      controllerAs: 'editSchedule'
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

$angular.config(function ($httpProvider) {
  $httpProvider.defaults.headers.post['Content-Type'] = 'application/json';
  $httpProvider.defaults.headers.patch['Content-Type'] = 'application/json';
});

$angular.config(function ($qProvider) {
  $qProvider.errorOnUnhandledRejections(false);
});

$angular.constant('env', {
  backendBaseUrl: process.env.backendBaseUrl
});

$angular.constant('headers', {
  totalItemsHeader: "X-Resource-Total-Records",
  totalPagesHeader: "X-Page-Total",
  itemsPerPageHeader: "X-Page-Size"
});
