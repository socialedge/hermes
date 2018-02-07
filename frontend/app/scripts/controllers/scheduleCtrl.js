'use strict';

angular.module('hermesApp').controller('ScheduleCtrl', function ($scope, $http, $routeParams, $location, env) {

  function fetchSchedule(id, callback) {
    $http.get(env.backendBaseUrl + "/schedules/" + id)
      .then(function(result) {
        callback(result.data);
      });
  }

  function fetchTrips(id, direction, callback) {
    var scheduleDirection = direction === "INBOUND" ? "inboundTrips" : "outboundTrips";
    $http.get(env.backendBaseUrl + "/schedules/" + id + "/" + scheduleDirection)
      .then(function(result) {
        callback(result.data);
      });
  }

  function fetchLine(id, callback) {
    $http.get(env.backendBaseUrl + "/lines/" + id)
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

  function segmentsToStations(segments) {
    var stations = [];
    for (var i = 0; i < segments.length; i++) {
      stations.push(segments[i].begin);
    }
    stations.push(segments[segments.length - 1].end);
    return stations;
  }

  $scope.loadPage = function(callback) {
    if (!$routeParams.show) {
      $location.path("/schedules");
    }
    $scope.page = {};
    const scheduleId = $routeParams.show;
    fetchSchedule(scheduleId, function(response) {
      $scope.page.scheduleId = scheduleId;
      const lineId = response.lineId;
      fetchLine(lineId, function(response) {
        $scope.page.lineName = response.name;
        $scope.page.inboundStations = segmentsToStations(response.inboundRoute);
        if (response.outboundRoute) {
          $scope.page.outboundStations = segmentsToStations(response.outboundRoute);
        }
      });

      fetchTrips(scheduleId, "INBOUND", function(response) {
        $scope.page.inboundTrips = response.sort(sortByArrivalTime);
      });
      fetchTrips(scheduleId, "OUTBOUND", function(response) {
        if (response.length != 0) {
          $scope.page.isBidirectional = true;
          $scope.page.outboundTrips = response.sort(sortByArrivalTime);
        }
      });

      if (typeof callback === "function")
        callback($scope.page);
      });
  };

});
