'use strict';

angular.module('hermesApp').controller('EditScheduleCtrl', function ($scope, $http, $routeParams, $location, $uibModal, $window, env) {

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

  $scope.updateSchedule = function () {
    const schedule = $scope.page.schedule;
    for (var i = 0; i < schedule.inboundTrips.length; i++) {
      schedule.inboundTrips[i] = setDepartureTimes(schedule.inboundTrips[i]);
    }
    if (schedule.outboundTrips) {
      for (var i = 0; i < schedule.outboundTrips.length; i++) {
        schedule.outboundTrips[i] = setDepartureTimes(schedule.outboundTrips[i]);
      }
    }
    if (schedule.id) {
      $http.put(env.backendBaseUrl + "/schedules/" + schedule.id, schedule)
        .then(function (response) {
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
      });

      fetchTrips(scheduleId, "INBOUND", function(response) {
        $scope.page.schedule.inboundTrips = response.sort(sortByArrivalTime);
        $scope.page.inboundStations = tripToStations($scope.page.schedule.inboundTrips[0]);
        for (var i = 0; i < $scope.page.schedule.inboundTrips.length; i++) {
          $scope.page.schedule.inboundTrips[i] = addDwellTimes($scope.page.schedule.inboundTrips[i]);
        }
      });
      fetchTrips(scheduleId, "OUTBOUND", function(response) {
        if (response.length != 0) {
          $scope.page.isBidirectional = true;
          $scope.page.schedule.outboundTrips = response.sort(sortByArrivalTime);
          $scope.page.outboundStations = tripToStations($scope.page.schedule.outboundTrips[0]);
          for (var i = 0; i < $scope.page.schedule.outboundTrips.length; i++) {
            $scope.page.schedule.outboundTrips[i] = addDwellTimes($scope.page.schedule.outboundTrips[i]);
          }
        }
      });

      if (typeof callback === "function")
        callback($scope.page);
      });
  };

  $scope.openSelectStationModal = function(direction) {
    $uibModal.open({
      templateUrl: 'selectStationModal.html',
      controller: 'SelectStationModalCtrl',
      resolve: {
        stationsFilter: function() {return direction === "INBOUND" ? $scope.page.inboundStations : $scope.page.outboundStations}
      }
    }).result.then(function (station) {
      if (station) {
        $scope.addColumn(direction, station);
      }
    });
  };

  $scope.addRow = function(direction) {
    if (direction === "INBOUND") {
      var newTrip = duplicateTrip($scope.page.schedule.inboundTrips[0]);
      $scope.page.schedule.inboundTrips.push(newTrip);
    } else {
      var newTrip = duplicateTrip($scope.page.schedule.outboundTrips[0]);
      $scope.page.schedule.outboundTrips.push(newTrip);
    }
  }

  $scope.addColumn = function(direction, station) {
    if (direction === "INBOUND") {
      $scope.page.inboundStations.push(station);
      for (var i = 0; i < $scope.page.schedule.inboundTrips.length; i++) {
        $scope.page.schedule.inboundTrips[i].stops.push(newStop(station));
      }
    } else {
      $scope.page.outboundStations.push(station);
      for (var i = 0; i < $scope.page.schedule.outboundTrips.length; i++) {
        $scope.page.schedule.outboundTrips[i].stops.push(newStop(station));
      }
    }
  };

  $scope.removeRow = function(index, direction) {
    if (direction === "INBOUND") {
      $scope.page.schedule.inboundTrips.splice(index, 1);
    } else {
      $scope.page.schedule.outboundTrips.splice(index, 1);
    }
  };

  $scope.removeColumn = function(index, direction) {
    if (direction === "INBOUND") {
      for (var i = 0; i < $scope.page.schedule.inboundTrips.length; i++) {
        $scope.page.schedule.inboundTrips[i].stops.splice(index, 1);
      }
      $scope.page.inboundStations.splice(index, 1);
    } else {
      for (var i = 0; i < $scope.page.schedule.outboundTrips.length; i++) {
        $scope.page.schedule.outboundTrips[i].stops.splice(index, 1);
      }
      $scope.page.outboundStations.splice(index, 1);
    }
  };

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

  function tripToStations(trip) {
    var stations = [];
    for (var i = 0; i < trip.stops.length; i++) {
      stations.push({id:trip.stops[i].stationId,name:trip.stops[i].name});
    }
    return stations;
  }

  function addDwellTimes(trip) {
    const someDate = "12/12/2012 ";
    for (var i = 0; i < trip.stops.length; i++) {
      var stopArrival = new Date(someDate + trip.stops[i].arrival);
      var stopDeparture = new Date(someDate + trip.stops[i].departure);
      var dwellTime = Math.abs(stopArrival.getTime() - stopDeparture.getTime());
      trip.stops[i].dwellTime = dwellTime;
    }
    return trip;
  }

  function setDepartureTimes(trip) {
    const someDate = "12/12/2012 ";
    for (var i = 0; i < trip.stops.length; i++) {
      var stopArrival = new Date(someDate + trip.stops[i].arrival);
      var stopDeparture = new Date();
      stopDeparture.setTime(stopArrival.getTime() + trip.stops[i].dwellTime);
      trip.stops[i].departure = toTimeString(stopDeparture);
    }
    return trip;
  }

  function toTimeString(date) {
    var result = "";
    if (date.getHours() < 10) {
      result = result + "0";
    }
    result = result + date.getHours() + ":";
    if (date.getMinutes() < 10) {
      result = result + "0";
    }
    result = result + date.getMinutes();
    if (date.getSeconds() > 0) {
      result = result + ":";
      if (date.getSeconds() < 10) {
        result = result + "0";
      }
      result = result + date.getSeconds();
    }
    return result;
  }

  function newStop(station) {
    var newStop = {
          arrival: "",
          departure: "",
          dwellTime: station.dwell * 1000,
          name: station.name,
          stationId: station.id
        };
    return newStop;
  }

  function duplicateTrip(trip) {
    var newTrip = {headsign: trip.headsign, stops: []};
    for (var i = 0; i < trip.stops.length; i++) {
      newTrip.stops.push({name:trip.stops[i].name, stationId:trip.stops[i].stationId, dwellTime: trip.stops[i].dwellTime, arrival:"",departure:""});
    }
    return newTrip;
  }

});

angular.module('hermesApp').controller('SelectStationModalCtrl', function ($q, $scope, $timeout, $http, $uibModalInstance, env, stationsFilter) {

  $scope.initModal = function () {
    $.material.init();
  };

  $scope.closeModal = function () {
    $uibModalInstance.dismiss('cancel');
  };

  $scope.fetchStationsContaining = function (name) {
    return $http.get(env.backendBaseUrl + "/stations?filter=name," + name)
      .then(function (response) {
        if (!stationsFilter)
          return response.data;

        return response.data.filter(function(station) {
          return stationsFilter.filter(function(filterStation) {
              return filterStation.name === station.name;
            }).length === 0;
        });
      });
  };

  $scope.f = function() {
    console.log($scope.page.selectedStation);
    $uibModalInstance.close($scope.page.selectedStation);
  }

});
