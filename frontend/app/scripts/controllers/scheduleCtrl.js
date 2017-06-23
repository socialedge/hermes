'use strict';

angular.module('hermesApp').controller('ScheduleCtrl', function ($scope, $http, $routeParams, $location, env) {

  function fetchSchedule(url, direction, callback) {
    var projection = direction === "INBOUND" ? "inboundSchedule" : "outboundSchedule";
    $http.get(url + "?projection=" + projection)
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
    fetchSchedule($routeParams.show, "INBOUND", function(response) {
      $scope.page.lineName = response.line.name;
      $scope.page.isBidirectional = response.line.bidirectionalLine;
      $scope.page.inboundStations = response.line.inboundRoute.stations;
      $scope.page.inboundTrips = response.inboundTrips.sort(sortByArrivalTime);

      if (response.line.bidirectionalLine) {
        fetchSchedule($routeParams.show, "OUTBOUND", function(response) {
          $scope.page.outboundStations = response.line.outboundRoute.stations;
          $scope.page.outboundTrips = response.outboundTrips.sort(sortByArrivalTime);
        });
      }

      if (typeof callback === "function")
        callback($scope.page);
    });
  };

});
