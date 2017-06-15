'use strict';

angular.module('hermesApp').controller('ScheduleCtrl', function ($scope, $http, $routeParams, $location, env) {

  function fetchSchedule(url, callback) {
    $http.get(url + "?projection=richScheduleProjection")
      .then(function(result) {
        callback(result.data);
      });
  }

  function sortByArrivalTime(t1, t2) {
    const someDate = "12/12/2012 ";
    const t1Arrival = new Date(someDate + t1.stops[0].arrival);
    const t2Arrival = new Date(someDate + t2.stops[0].arrival);
    if (t1Arrival > t2Arrival)
      return 1;
    if (t1Arrival < t2Arrival)
      return -1;
    return 0;
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

      var trips = response.trips.sort(sortByArrivalTime);
      $scope.page.inboundTrips = trips.filter(function(trip) {
        return trip.route._links.self.href === response.line.inboundRoute._links.self.href;
      });
      $scope.page.outboundTrips = trips.filter(function(trip) {
        return trip.route._links.self.href === response.line.outboundRoute._links.self.href;
      });

      if (typeof callback === "function")
        callback($scope.page);
    });
  };

});
