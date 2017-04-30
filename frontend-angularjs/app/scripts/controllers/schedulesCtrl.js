'use strict';

angular.module('hermesApp').controller('SchedulesCtrl', function ($scope, $http, $uibModal, $window, env) {

  function fetchSchedules(pageIndex, callback, pageSize) {
    pageSize = pageSize || DEFAULT_PAGE_SIZE;
    $http.get(env.backendBaseUrl + "/schedules?size=" + pageSize + "&page=" + pageIndex)
      .then(function (response) {
        callback(response.data);
      });
  }

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
});
