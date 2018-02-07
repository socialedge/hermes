'use strict';

angular.module('hermesApp').controller('EditScheduleCtrl', function ($scope, $http, $routeParams, $location, env) {

  $scope.initAlerts = function () {
    $scope.alerts = [];
  };

  $scope.addAlert = function (message, clz) {
    clz = clz || "warning";
    $scope.alerts.push({type: clz, msg: message});
    $window.scrollTo(0, 0);
  };

  $scope.closeAlert = function (index) {
    $scope.alerts.splice(index, 1);
  };

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

  $scope.updateSchedule = function () {
    const schedule = $scope.page.schedule;
    if (schedule.id) {
      $http.put(env.backendBaseUrl + "/schedules/" + schedule.id, schedule)
        .then(function (response) {
          console.log(response);
          $scope.addAlert('Розклад був успішно оновлений', 'success');
        }, function (error) {
          $scope.addAlert('Помилка при оновленні розкладу', 'danger');
          console.log(error);
        });
    }
  };

  $scope.loadPage = function(callback) {
    if (!$routeParams.show) {
      $location.path("/schedules");
    }
    $scope.page = {};
    const scheduleId = $routeParams.show;
    fetchSchedule(scheduleId, function(response) {
      $scope.page.schedule = response;
      fetchLine($scope.page.schedule.lineId, function(response) {
        $scope.page.lineName = response.name;
        $scope.page.inboundStations = segmentsToStations(response.inboundRoute);
        if (response.outboundRoute) {
          $scope.page.outboundStations = segmentsToStations(response.outboundRoute);
        }
      });

      fetchTrips(scheduleId, "INBOUND", function(response) {
        $scope.page.schedule.inboundTrips = response.sort(sortByArrivalTime);
      });
      fetchTrips(scheduleId, "OUTBOUND", function(response) {
        if (response.length != 0) {
          $scope.page.isBidirectional = true;
          $scope.page.schedule.outboundTrips = response.sort(sortByArrivalTime);
        }
      });

      if (typeof callback === "function")
        callback($scope.page);
      });
  };

});
