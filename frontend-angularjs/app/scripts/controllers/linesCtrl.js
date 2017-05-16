'use strict';

angular.module('hermesApp').controller('LinesCtrl', function ($q, $scope, $http, $uibModal, $window, env) {
  const DEFAULT_PAGE_SIZE = 25;

  function fetchLines(pageIndex, callback, pageSize) {
    pageSize = pageSize || DEFAULT_PAGE_SIZE;

    $http.get(env.backendBaseUrl + "/lines?size=" + pageSize + "&page=" + pageIndex + "&projection=richLineProjection")
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

  $scope.loadPage = function (pageIndex, callback, pageSize) {
    pageIndex = pageIndex || 0;

    $scope.page = {};
    fetchLines(pageIndex, function (response) {
      $scope.page.totalItems = response.page.totalElements;
      $scope.page.itemsPerPage = response.page.size;
      $scope.page.maxSize = response.page.totalPages;
      $scope.page.currentPage = pageIndex + 1;

      $scope.page.lines = response._embedded.lines;

      if (typeof callback === 'function')
        callback($scope.page);
    }, pageSize);
  };

  $scope.refreshPage = function () {
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

  $scope.deleteLine = function (name, location) {
    $http.delete(location).then(function () {
      $scope.loadPage($scope.currentPageIndex(), function () {
        $scope.addAlert('Line #' + name + ' has been deleted!', 'success');
      });
    }, function (error) {
      $scope.addAlert('Error happened: \'' + error.data.message + ' \'', 'danger');
    });
  };

  $scope.openNewLineModal = function () {
    $uibModal.open({
      templateUrl: 'lineModal.html',
      controller: 'NewLineCtrl'
    }).result.then(function (newLine) {
      if (newLine.error) {
        $scope.addAlert('Error happened: \'' + newLine.error + ' \'', 'danger');
      } else {
        $scope.loadPage($scope.lastPageIndex(), function () {
          $scope.addAlert('New \'' + newLine.name + ' \' line has been successfully added!', 'success');
        });
      }
    });
  };
});

angular.module('hermesApp').controller('AbstractLineModalCtrl', function ($scope, $timeout, $http, $uibModalInstance, env) {
  $scope.initModal = function () {
    $.material.init();

    // Init sortable
    $('.sortable-list').each(function(i, obj) {
     Sortable.create(obj, {handle: '.list-group-sortable', draggable: ".list-group-sortable-item"});
    });
  };

  $scope.fetchStationsContaining = function (name, filterList) {
    return $http.get(env.backendBaseUrl + "/stations/search/findByNameContainingIgnoreCase?name=" + name)
      .then(function (response) {
        if (!filterList)
          return response.data._embedded.stations;

        return response.data._embedded.stations.filter(function(station) {
          return filterList.filter(function(filterStation) {
              return filterStation.name === station.name;
            }).length === 0;
        });
      });
  };

  $scope.onStationAutocomplete = function ($station, targetArray) {
    targetArray.push($station);
  };

  $scope.removeRouteStation = function($index, targetArray) {
    targetArray.splice($index, 1);
  };

  $scope.initAgenciesSelect = function () {
    $http.get(env.backendBaseUrl + "/agencies")
      .then(function (response) {
        $scope.agencies = response.data._embedded.agencies;
      });
  };

  $scope.closeModal = function () {
    $uibModalInstance.dismiss('cancel');
  };
});


angular.module('hermesApp').controller('NewLineCtrl', function ($scope, $controller, $http, $uibModalInstance, $timeout, env) {
  angular.extend(this, $controller('AbstractLineModalCtrl', {$scope: $scope, $uibModalInstance: $uibModalInstance}));
  var $ctrl = this;

  $scope.line = {};
  $scope.line.inboudRoute = {stations: []};
  $scope.line.outboundRoute = {stations: []};

  $scope.saveLine = function () {
    console.log($scope.line);
  };

});

