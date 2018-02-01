'use strict';

angular.module('hermesApp').controller('SchedulesCtrl', function ($scope, $http, $uibModal, $window, env, headers) {
  const DEFAULT_PAGE_SIZE = 25;

  function fetchSchedules(pageIndex, callback, pageSize) {
    pageSize = pageSize || DEFAULT_PAGE_SIZE;
    $http.get(env.backendBaseUrl + "/schedules?size=" + pageSize + "&page=" + pageIndex)
      .then(function (response) {
        callback(response);
      });
  }

  function fetchLines(callback) {
    $http.get(env.backendBaseUrl + "/lines").then(function (response) {
      callback(response.data);
    });
  }

  function fetchLine(id, callback) {
    $http.get(env.backendBaseUrl + "/lines/" + id).then(function (response) {
      callback(response.data);
    });
  }

  function formatTime(time) {
    return (time.getHours() < 10 ? "0" : "") + time.getHours() + ":" + (time.getMinutes() < 10 ? "0" : "") + time.getMinutes();
  }

  function persistSchedule(line, desc, startDate, endDate, selectedDays, startTimeInbound, startTimeOutbound, endTime, headway,
                           averageSpeed, minLayover, callback) {
    const reqData = {
      availability: {
        dayOfWeek: selectedDays,
        startDate: startDate,
        endDate: endDate,
        exceptionDays: []
      },
      lineId: line,
      description: desc,
      startTimeInbound: formatTime(startTimeInbound),
      startTimeOutbound: formatTime(startTimeOutbound),
      endTimeInbound: formatTime(endTime),
      endTimeOutbound: formatTime(endTime),
      headway: headway * 60,
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
    return schedule.availability.dayOfWeek.indexOf(day) > -1;
  };

  $scope.loadPage = function (pageIndex, callback, pageSize) {
    pageIndex = pageIndex || 0;
    $scope.page = {};
    fetchSchedules(pageIndex, function (response) {
      $scope.page.totalItems = response.headers(headers.totalItemsHeader);
      $scope.page.itemsPerPage = response.headers(headers.itemsPerPageHeader);
      $scope.page.maxSize = response.headers(headers.totalPagesHeader);
      $scope.page.currentPage = pageIndex + 1;

      $scope.page.schedules = response.data;

      fetchLines(function (response) {
        $scope.page.lines = response;

        for (var i = 0; i < $scope.page.schedules.length; i++) {
          var scheduleLineId = $scope.page.schedules[i].lineId;
          for (var j = 0; j < $scope.page.lines.length; j++) {
            if ($scope.page.lines[j].id === scheduleLineId) {
              $scope.page.schedules[i].line = {
                id: scheduleLineId,
                name: $scope.page.lines[j].name
              };
            }
          }
        }
      });

      if (typeof callback === 'function')
        callback($scope.page);
    }, pageSize);
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
      schedule.startTimeOutbound, schedule.endTime, schedule.headway, schedule.averageSpeed, schedule.minLayover,
      function (result) {
        if (!result.error) {
          $scope.closeModal();
          addAlert("Schedule was successfully saved", "success");
        } else {
          addAlert(result.error);
        }
      });
  };

  $scope.deleteSchedule = function (id) {
    $http.delete(env.backendBaseUrl + "/schedules/" + id).then(function () {
      $scope.loadPage($scope.currentPageIndex(), function () {
      });
    }, function (error) {
      $scope.addAlert('Error happened: \'' + error.data.message + ' \'');
    });
  };
});
