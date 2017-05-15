'use strict';

angular.module('hermesApp').controller('ScheduleCtrl', function ($scope, $http, $routeParams, $location, env) {

  function fetchSchedule(url, callback) {
    $http.get(url + "?projection=richScheduleProjection")
      .then(function(result) {
        callback(result.data);
      });
  }

  $scope.loadPage = function(callback) {
    if (!$routeParams.show) {
      $location.path("/schedules");
    }
    $scope.page = {};
    fetchSchedule($routeParams.show, function(response) {
      $scope.page.lineCode = response.line.code;
      $scope.page.inboundRouteCode = response.line.inboundRoute.code;
      $scope.page.outboundRouteCode = response.line.outboundRoute.code;
      $scope.page.inboundStations = response.line.inboundRoute.stations;
      $scope.page.outboundStations = response.line.outboundRoute.stations;
      $scope.page.inboundTrips = response.trips.filter(function(trip) {
        return trip.route.code === response.line.inboundRoute.code;
      });
      $scope.page.outboundTrips = response.trips.filter(function(trip) {
        return trip.route.code === response.line.outboundRoute.code;
      });

      if (typeof callback === "function")
        callback($scope.page);
    });
  };

});
