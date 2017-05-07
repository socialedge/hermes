'use strict';

angular.module('hermesApp').controller('SchedulesCtrl', function ($scope, $http, $uibModal, $window, env) {

  var selectedDays = [];
  function fetchSchedules(pageIndex, callback, pageSize) {
    pageSize = pageSize || DEFAULT_PAGE_SIZE;
    $http.get(env.backendBaseUrl + "/schedules?size=" + pageSize + "&page=" + pageIndex)
      .then(function (response) {
        callback(response.data);
      });
  }

  function fetchLines(callback) {
    $http.get(env.backendBaseUrl + "/lines").then(function(response) {
      callback(response.data._embedded.lines);
    });
  };

  function formatTime(time) {
    return (time.getHours()<10?"0":"") + time.getHours() + ":" + (time.getMinutes()<10?"0":"") + time.getMinutes();
  }

  function persistSchedule (line, desc, startDate, endDate, startTimeInbound, startTimeOutbound, endTime, headway,
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

  function addModalAlert(message, type) {
    type = type || "danger";
    $scope.modalAlerts.push({message:message, type:type});
    $("#schedule").scrollTop(0);
  };

  function validateSchedule(schedule) {
    if (!schedule) {
      addModalAlert("All fields must be filled");
      return false;
    }

    var alerts = [];
    if (!schedule.line) {
      alerts.push("Line must be chosen");
    }

    if (!schedule.description) {
      alerts.push("Description must be written");
    }

    if (!schedule.startDate) {
      alerts.push("Start date must be chosen");
    }

    if (!schedule.endDate) {
      alerts.push("End date must be chosen");
    }

    if (!schedule.startTimeInbound) {
      alerts.push("Start time inbound must be set");
    }

    if (!schedule.startTimeOutbound) {
      alerts.push("Start time outbound must be set");
    }

    if (!schedule.endTime) {
      alerts.push("End time must be set");
    }

    if (!schedule.headway) {
      alerts.push("Headway value must be set");
    }

    if (!schedule.dwellTime) {
      alerts.push("Dwell time must be set");
    }

    if (!schedule.averageSpeed) {
      alerts.push("Average speed must be set");
    }

    if (!schedule.minLayover) {
      alerts.push("Min layover must be set");
    }

    if (alerts.length === 0) {
      return true;
    }

    alerts.forEach(function(alert) {
      addModalAlert(alert);
    });
  };

  $scope.initModalAlerts = function () {
    $scope.modalAlerts = [];
  };

  $scope.closeModalAlert = function (index) {
    $scope.modalAlerts.splice(index, 1);
  };

  $scope.isWorkingDay = function (schedule, day) {
    return schedule.availability.weekDays.indexOf(day) > -1;
  };

  $scope.selectDay = function($event, day) {
    var index = selectedDays.indexOf(day);
    if (index === -1) {
      selectedDays.push(day);
    } else {
      selectedDays.splice(index, 1);
    }
  }

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
    fetchLines(function(response) {
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

  $scope.fetchLinesNames = function() {
    $http.get(env.backendBaseUrl + "/lines").then(function(response) {
      var names = response.data._embedded.lines.map(function(line) {
        return line.name;
      });
      return names;
    });
  };

  $scope.saveSchedule = function (schedule) {
    $scope.initModalAlerts();
    if (validateSchedule(schedule)) {
      persistSchedule(schedule.line, schedule.description, schedule.startDate, schedule.endDate, schedule.startTimeInbound,
        schedule.startTimeOutbound, schedule.endTime, schedule.headway, schedule.dwellTime, schedule.averageSpeed, schedule.minLayover,
        function(result) {
          if (!result.error) {
            addModalAlert("Schedule was successfully saved", "success");
            $scope.schedule = {};
            $scope.refreshPageSchedules();
          } else {
            addModalAlert(result.error);
          }
        });
    }
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
