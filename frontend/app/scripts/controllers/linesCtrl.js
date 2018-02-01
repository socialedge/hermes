'use strict';

angular.module('hermesApp').controller('LinesCtrl', function ($q, $scope, $http, $uibModal, $window, env, headers) {
  const DEFAULT_PAGE_SIZE = 25;

  function fetchLines(pageIndex, callback, pageSize) {
    pageSize = pageSize || DEFAULT_PAGE_SIZE;

    $http.get(env.backendBaseUrl + "/lines?size=" + pageSize + "&page=" + pageIndex)
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
    fetchLines(pageIndex, function (response) {
      $scope.page.totalItems = response.headers(headers.totalItemsHeader);
      $scope.page.itemsPerPage = response.headers(headers.itemsPerPageHeader);
      $scope.page.maxSize = response.headers(headers.totalPagesHeader);
      $scope.page.currentPage = pageIndex + 1;
      $scope.page.lines = response.data;

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

  $scope.deleteLine = function (line) {
    $http.delete(env.backendBaseUrl + "/lines/" + line.id).then(function () {
      $scope.loadPage($scope.currentPageIndex(), function () {
        $scope.addAlert('Line #' + line.name + ' has been deleted!', 'success');
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

  $scope.openEditLineModal = function(line) {
    $uibModal.open({
      templateUrl: 'lineModal.html',
      controller: 'EditLineCtrl',
      resolve: {
        lineData: function () {
          return angular.copy(line);
        }
      }
    }).result.then(function (editedLine) {
      if (editedLine.error) {
        $scope.addAlert('Error happened: \'' + editedLine.error + ' \'', 'danger');
      } else {
        $scope.loadPage($scope.currentPageIndex(), function () {
          $scope.addAlert('Line \'' + editedLine.name + ' \' has been successfully saved!', 'success');
        });
      }
    });
  };
});

angular.module('hermesApp').controller('AbstractLineModalCtrl', function ($q, $scope, $timeout, $http, $uibModalInstance, env) {

  $scope.sortableOptions = {
    'ui-floating': true,
    start: function (e, ui) {
      $('[ui-sortable]').sortable("refreshPositions");
    }
  };

  $scope.persistLine = function (name, agencyId, vehicleType, inboundRoute, outboundRoute, callback, id) {
    const reqData = {
      name: name,
      agencyId: agencyId,
      vehicleType: vehicleType,
      inboundRoute: inboundRoute,
      outboundRoute: outboundRoute
    };

    if (!id) {
      $http.post(env.backendBaseUrl + "/lines", reqData)
        .then(function (response) {
          if (typeof callback === 'function')
            callback({
              name: response.data.name,
              id: response.data.id
            });
        }, function (error) {
          if (typeof callback === 'function')
            callback({
              error: error
            });
        });
    } else {
      $http.put(env.backendBaseUrl + "/lines/" + id, reqData)
        .then(function (response) {
          if (typeof callback === 'function')
            callback({
              name: response.data.name,
              id: response.data.id
            });
        }, function (error) {
          if (typeof callback === 'function')
            callback({
              error: error
            });
        });
    }
  };

  $scope.initModal = function () {
    $.material.init();
  };

  $scope.fetchStationsContaining = function (name, filterList) {
    return $http.get(env.backendBaseUrl + "/stations?filter=name," + name)
      .then(function (response) {
        if (!filterList)
          return response.data;

        return response.data.filter(function(station) {
          return filterList.filter(function(filterStation) {
              return filterStation.name === station.name;
            }).length === 0;
        });
      });
  };

  $scope.onStationAutocomplete = function ($station, targetArray) {
    $station.stationId = $station.id;
    targetArray.push($station);
  };

  $scope.removeRouteStation = function($index, targetArray) {
    targetArray.splice($index, 1);
  };

  $scope.initAgenciesSelect = function () {
    $http.get(env.backendBaseUrl + "/agencies")
      .then(function (response) {
        $scope.agencies = response.data;
      });
  };

  $scope.closeModal = function () {
    $uibModalInstance.dismiss('cancel');
  };

  $scope.convertToSegmentedRoute = function (route) {
    var segments = [];
    for (var i = 0; i < route.length - 1; i++) {
      var segment = {
        begin: route[i],
        end: route[i + 1]
      };
      segments.push(segment);
    }
    return segments;
  };

  $scope.convertToStationsRoute = function (route) {
      var stations = [];
      for (var i = 0; i < route.length; i++) {
        stations.push(route[i].begin);
      }
      stations.push(route[route.length - 1].end);
      return stations;
  };
});


angular.module('hermesApp').controller('NewLineCtrl', function ($q, $scope, $controller, $http, $uibModalInstance, $timeout, env) {
  angular.extend(this, $controller('AbstractLineModalCtrl', {$scope: $scope, $uibModalInstance: $uibModalInstance}));
  var $ctrl = this;

  $scope.line = {};
  $scope.line.inboundRoute = [];
  $scope.line.outboundRoute = [];

  $scope.saveLine = function () {
    $scope.persistLine($scope.line.name,
                       $scope.line.agencyId,
                       $scope.line.vehicleType,
                       $scope.convertToSegmentedRoute($scope.line.inboundRoute),
                       $scope.convertToSegmentedRoute($scope.line.outboundRoute),
                       function(result) {$uibModalInstance.close(result);}
                      );
  };

});

angular.module('hermesApp').controller('EditLineCtrl', function ($q, $scope, $controller, $http, $timeout, $uibModalInstance, lineData) {
  angular.extend(this, $controller('AbstractLineModalCtrl', {$scope: $scope, $uibModalInstance: $uibModalInstance}));
  var $ctrl = this;

  $scope.initModal = function () {
    $.material.init();

    $scope.line = {
      name: lineData.name,
      agencyId: lineData.agencyId,
      vehicleType: lineData.vehicleType,
      inboundRoute: $scope.convertToStationsRoute(lineData.inboundRoute),
      outboundRoute: $scope.convertToStationsRoute(lineData.outboundRoute)
    };
  };

  $scope.saveLine = function () {
    $scope.persistLine($scope.line.name,
                       $scope.line.agencyId,
                       $scope.line.vehicleType,
                       $scope.convertToSegmentedRoute($scope.line.inboundRoute),
                       $scope.convertToSegmentedRoute($scope.line.outboundRoute),
                       function(result) {$uibModalInstance.close(result);},
                       lineData.id);
    };
});
