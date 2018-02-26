/*
 * Hermes - The Municipal Transport Timetable System
 * Copyright (c) 2016-2018 SocialEdge
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

'use strict';

angular.module('hermesApp').controller('PublicationsCtrl', function ($scope, $http, $uibModal, $window, env, headers) {
  const DEFAULT_PAGE_SIZE = 25;

  function fetchPublications(pageIndex, callback, pageSize) {
    pageSize = pageSize || DEFAULT_PAGE_SIZE;

    $http.get(env.backendBaseUrl + "/publications?size=" + pageSize + "&page=" + pageIndex)
      .then(function (response) {
        callback(response);
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

  $scope.loadPage = function (pageIndex, callback, pageSize) {
    pageIndex = pageIndex || 0;

    $scope.page = {};
    fetchPublications(pageIndex, function (response) {
      $scope.page.totalItems = response.headers(headers.totalItemsHeader);
      $scope.page.itemsPerPage = response.headers(headers.itemsPerPageHeader);
      $scope.page.maxSize = response.headers(headers.totalPagesHeader);
      $scope.page.currentPage = pageIndex + 1;

      $scope.page.publications = response.data;

      for (var i = 0; i < $scope.page.publications.length; i++) {
        $scope.page.publications[i].contentsUrl = env.backendBaseUrl + "/publications/" + $scope.page.publications[i].id + "/contents";
      }

      if (typeof callback === 'function')
        callback($scope.page);
    }, pageSize);
  };

  $scope.refreshPagePublications = function () {
    $scope.loadPage($scope.currentPageIndex());
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

  $scope.openNewStationPublicationModal = function () {
    $uibModal.open({
      templateUrl: 'stationPublicationModal.html',
      controller: 'NewStationPublicationCtrl'
    }).result.then(function (newPublication) {
      if (newPublication.error) {
        $scope.addAlert('Error happened: \'' + newPublication.error + ' \'', 'danger');
      } else {
        $scope.loadPage($scope.lastPageIndex(), function () {
          $scope.addAlert('New \'' + newPublication.name + ' \' publication has been successfully made!', 'success');
        });
      }
    });
  };

  $scope.openNewLinePublicationModal = function () {
    $uibModal.open({
      templateUrl: 'linePublicationModal.html',
      controller: 'NewLinePublicationCtrl'
    }).result.then(function (newPublication) {
      if (newPublication.error) {
        $scope.addAlert('Error happened: \'' + newPublication.error + ' \'', 'danger');
      } else {
        $scope.loadPage($scope.lastPageIndex(), function () {
          $scope.addAlert('New \'' + newPublication.name + ' \' publication has been successfully made!', 'success');
        });
      }
    });
  };

});

angular.module('hermesApp').controller('AbstractNewPublicationCtrl', function ($q, $scope, $timeout, $http, $uibModalInstance, env) {

  $scope.createPublication = function (publication, callback) {
    const reqData = {
      lineId: publication.lineId,
      stationId: publication.stationId,
      scheduleIds: publication.scheduleIds
    };

    $http.post(env.backendBaseUrl + "/publications", reqData)
      .then(function (response) {
        if (typeof callback === 'function')
          callback({
            id: response.data.id,
            name: response.data.name,
            date: response.data.date
          });
      }, function (error) {
        if (typeof callback === 'function')
          callback({
            error: error
          });
      });
  };

  $scope.initModal = function () {
    $.material.init();
  };

  $scope.closeModal = function () {
    $uibModalInstance.dismiss('cancel');
  };
});

angular.module('hermesApp').controller('NewStationPublicationCtrl', function ($scope, $controller, $timeout, $http, $uibModalInstance, env) {
  angular.extend(this, $controller('AbstractNewPublicationCtrl', {$scope: $scope, $uibModalInstance: $uibModalInstance}));

  $scope.fetchStationsContaining = function (name) {
    $scope.showSchedules = false;
    return $http.get(env.backendBaseUrl + "/stations?filter=name," + name)
      .then(function (response) {
        return response.data;
      });
  };

  $scope.onStationAutocomplete = function ($station) {
    $scope.stationId = $station.id;
    $scope.showSchedules = true;
    $http.get(env.backendBaseUrl + "/schedules?filter=inboundTrips.stops.station.id," + $station.id)
      .then(function (response) {
        $scope.schedules = response.data;

        $http.get(env.backendBaseUrl + "/schedules?filter=outboundTrips.stops.station.id," + $station.id)
          .then(function (response) {
            for (let i = 0; i < response.data.length; i++) {
              let schedule = response.data[i];

              if ($scope.schedules.indexOf(schedule) === -1) {
                $scope.schedules.push(schedule);
              }
            }

            $timeout(function () {
              $.material.init();
            });
          });
      });
  };

  $scope.savePublication = function () {
    $scope.showLoading = true;
    $scope.createPublication({
      stationId: $scope.stationId,
      scheduleIds: $scope.schedulesSelected
    }, function(result) {
      $uibModalInstance.close(result);
    });
  };
});

angular.module('hermesApp').controller('NewLinePublicationCtrl', function ($scope, $controller, $timeout, $http, $uibModalInstance, env) {
  angular.extend(this, $controller('AbstractNewPublicationCtrl', {$scope: $scope, $uibModalInstance: $uibModalInstance}));

  $scope.fetchLineContaining = function (name) {
    $scope.showSchedules = false;
    return $http.get(env.backendBaseUrl + "/lines?filter=name," + name)
      .then(function (response) {
        return response.data;
      });
  };

  $scope.onLineAutocomplete = function ($line) {
    $scope.lineId = $line.id;
    $scope.showSchedules = true;
    $http.get(env.backendBaseUrl + "/schedules?filter=line.id," + $line.id)
      .then(function (response) {
        $scope.schedules = response.data;

        $timeout(function () {
          $.material.init();
        });
      });
  };

  $scope.savePublication = function () {
    $scope.showLoading = true;
    $scope.createPublication({
      lineId: $scope.lineId,
      scheduleIds: $scope.schedulesSelected
    }, function(result) {
      $uibModalInstance.close(result);
    });
  };
});
