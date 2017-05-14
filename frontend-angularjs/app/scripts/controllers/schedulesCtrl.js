'use strict';

angular.module('hermesApp').controller('SchedulesCtrl', function ($scope, $http, $uibModal, $window, env) {
  const DEFAULT_PAGE_SIZE = 25;

  function fetchSchedules(pageIndex, callback, pageSize) {
    pageSize = pageSize || DEFAULT_PAGE_SIZE;
    $http.get(env.backendBaseUrl + "/schedules?size=" + pageSize + "&page=" + pageIndex)
      .then(function (response) {
        callback(response.data);
      });
  }

  function fetchLines(callback) {
    $http.get(env.backendBaseUrl + "/lines").then(function (response) {
      callback(response.data._embedded.lines);
    });
  }

  function formatTime(time) {
    return (time.getHours() < 10 ? "0" : "") + time.getHours() + ":" + (time.getMinutes() < 10 ? "0" : "") + time.getMinutes();
  }

  function persistSchedule(line, desc, startDate, endDate, selectedDays, startTimeInbound, startTimeOutbound, endTime, headway,
                           dwellTime, averageSpeed, minLayover, callback) {
    const reqData = {
      availability: {
        weekDays: selectedDays,
        startDate: startDate,
        endDate: endDate,
        exceptionDays: []
      },
      line: line,
      description: desc,
      startTimeInbound: formatTime(startTimeInbound),
      startTimeOutbound: formatTime(startTimeOutbound),
      endTimeInbound: formatTime(endTime),
      endTimeOutbound: formatTime(endTime),
      headway: headway * 60,
      dwellTime: dwellTime,
      averageSpeed: averageSpeed + ' km/h',
      minLayover: minLayover * 60
    };

    $http.post(env.backendBaseUrl + "/schedules", reqData)
      .then(function (response) {
        if (typeof callback === 'function') {
          callback({href: response.headers("Location")});
        }
      }, function (error) {
        if (typeof callback === 'function') {
          callback({error: error});
        }
      });
  };

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

  $scope.isWorkingDay = function (schedule, day) {
    return schedule.availability.weekDays.indexOf(day) > -1;
  };

  $scope.loadPage = function (pageIndex, callback, pageSize) {
    pageIndex = pageIndex || 0;
    $scope.page = {};
    fetchSchedules(pageIndex, function (response) {
      $scope.page.totalItems = response.page.totalElements;
      $scope.page.itemsPerPage = response.page.size;
      $scope.page.maxSize = response.page.totalPages;
      $scope.page.currentPage = pageIndex + 1;

      $scope.page.schedules = response._embedded.schedules;

      if (typeof callback === 'function')
        callback($scope.page);
    }, pageSize);
    fetchLines(function (response) {
      $scope.page.lines = response;
    });
  };

  $scope.refreshPageSchedules = function () {
    fetchSchedules($scope.currentPageIndex(), function (response) {
      $scope.page.schedules = response._embedded.schedules;
    });
  };

  $scope.currentPage = function () {
    return $scope.page.currentPage;
  };

  $scope.currentPageIndex = function () {
    return $scope.currentPage() - 1;
  };

  $scope.lastPage = function () {
    return $scope.page.maxSize;
  };

  $scope.lastPageIndex = function () {
    return $scope.lastPage() - 1;
  };

  $scope.fetchLinesNames = function () {
    $http.get(env.backendBaseUrl + "/lines").then(function (response) {
      var names = response.data._embedded.lines.map(function (line) {
        return line.name;
      });
      return names;
    });
  };

  $scope.openModal = function () {
    angular.element(document.querySelector('#schedule')).modal('show');
    $scope.schedule = {
      days: {
        "ПН": {name: "MONDAY", enabled: false},
        "ВТ": {name: "TUESDAY", enabled: false},
        "СР": {name: "WEDNESDAY", enabled: false},
        "ЧТ": {name: "THURSDAY", enabled: false},
        "ПТ": {name: "FRIDAY", enabled: false},
        "СБ": {name: "SATURDAY", enabled: false},
        "ВС": {name: "SUNDAY", enabled: false}
      }
    };
  };

  $scope.closeModal = function () {
    $scope.refreshPageSchedules();
    angular.element(document.querySelector('#schedule')).modal('hide');
  };

  $scope.saveSchedule = function (schedule) {
    var selectedDays = [];
    for (var label in schedule.days) {
      if (schedule.days[label].enabled)
        selectedDays.push(schedule.days[label].name);
    }
    persistSchedule(schedule.line, schedule.description, schedule.startDate, schedule.endDate, selectedDays, schedule.startTimeInbound,
      schedule.startTimeOutbound, schedule.endTime, schedule.headway, schedule.dwellTime, schedule.averageSpeed, schedule.minLayover,
      function (result) {
        if (!result.error) {
          $scope.closeModal();
          addAlert("Schedule was successfully saved", "success");
        } else {
          addAlert(result.error);
        }
      });
  };

  $scope.deleteSchedule = function (url) {
    $http.delete(url).then(function () {
      $scope.loadPage($scope.currentPageIndex(), function () {
      });
    }, function (error) {
      $scope.addAlert('Error happened: \'' + error.data.message + ' \'');
    });
  };
});
