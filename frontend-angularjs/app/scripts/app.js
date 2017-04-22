'use strict';

var $angular = angular.module('hermesApp', ['ngAnimate', 'ngCookies', 'ngResource', 'ngRoute', 'ngSanitize', 'ngTouch']);

$angular.config(function ($routeProvider) {
  $routeProvider
    .when('/', {
      templateUrl: '../views/index.html',
      controller: 'IndexCtrl',
      controllerAs: 'index'
    })
    .otherwise({
      redirectTo: '/'
    });
});
