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

  $scope.persistRoute = function (code, vehicleType, stations, callback, url) {
    const reqData = {
      code: code,
      vehicleType: vehicleType,
      stations: stations
    };

    if (!url) {
      return $http.post(env.backendBaseUrl + "/routes", reqData)
                  .then(function(result) {callback(result);},
                        function(error) {$scope.addAlert(error);});
    }
    return $http.patch(url, reqData)
                .then(function(result) {callback(result);},
                      function(error) {$scope.addAlert(error);});
  };

  $scope.persistLine = function (code, name, agency, infoUrl, inboundRoute, outboundRoute, callback, url) {
    const reqData = {
      code: code,
      name: name,
      agency: agency,
      url: infoUrl,
      inboundRoute: inboundRoute,
      outboundRoute: outboundRoute
    };

    if (!url) {
      $http.post(env.backendBaseUrl + "/lines", reqData)
        .then(function (response) {
          if (typeof callback === 'function')
            callback({
              name: response.data.name,
              href: response.data._links.line.href
            });
        }, function (error) {
          if (typeof callback === 'function')
            callback({
              error: error
            });
        });
    } else {
      $http.patch(url, reqData)
        .then(function (response) {
          if (typeof callback === 'function')
            callback({
              name: response.data.name,
              href: response.data._links.line.href
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

  $scope.mapToUrls = function (stations) {
    return stations.map(function(station) {
      return station._links.self.href;
    });
  }
});


angular.module('hermesApp').controller('NewLineCtrl', function ($q, $scope, $controller, $http, $uibModalInstance, $timeout, env) {
  angular.extend(this, $controller('AbstractLineModalCtrl', {$scope: $scope, $uibModalInstance: $uibModalInstance}));
  var $ctrl = this;

  $scope.line = {};
  $scope.line.inboundRoute = {stations: []};
  $scope.line.outboundRoute = {stations: []};

  $scope.saveLine = function () {
    var inboundRouteUrl, outboundRouteUrl;
    var inboundRoutePromise = $scope.persistRoute($scope.line.inboundRoute.code,
                                                  $scope.line.inboundRoute.vehicleType,
                                                  $scope.mapToUrls($scope.line.inboundRoute.stations),
                                                  function(response) {inboundRouteUrl=response.data._links.self.href;}
                                                 );
    var outboundRoutePromise = $scope.persistRoute($scope.line.outboundRoute.code,
                                                   $scope.line.outboundRoute.vehicleType,
                                                   $scope.mapToUrls($scope.line.outboundRoute.stations),
                                                   function(response) {outboundRouteUrl=response.data._links.self.href;}
                                                  );

    $q.all([inboundRoutePromise, outboundRoutePromise]).then(function() {
      if (!inboundRouteUrl || !outboundRouteUrl)
        return;

      $scope.persistLine($scope.line.code,
                         $scope.line.name,
                         $scope.line.agencyUrl,
                         $scope.line.url,
                         inboundRouteUrl,
                         outboundRouteUrl,
                         function(result) {$uibModalInstance.close(result);}
                        );
    });
  };

});

angular.module('hermesApp').controller('EditLineCtrl', function ($q, $scope, $controller, $http, $timeout, $uibModalInstance, lineData) {
  angular.extend(this, $controller('AbstractLineModalCtrl', {$scope: $scope, $uibModalInstance: $uibModalInstance}));
  var $ctrl = this;

  $scope.initModal = function () {
    $.material.init();

    $scope.line = {
      code: lineData.code,
      name: lineData.name,
      agencyUrl: lineData.agency._links.self.href,
      url: lineData.url,
      inboundRoute: lineData.inboundRoute,
      outboundRoute: lineData.outboundRoute
    };
  };

  $scope.saveLine = function () {
    var routesUpdated = true;
    var inboundRoutePromise = $scope.persistRoute($scope.line.inboundRoute.code,
                                                  $scope.line.inboundRoute.vehicleType,
                                                  $scope.mapToUrls($scope.line.inboundRoute.stations),
                                                  function(response) {if(!response.data) {routesUpdated = false;}},
                                                  $scope.line.inboundRoute._links.self.href
                                                 );
    var outboundRoutePromise = $scope.persistRoute($scope.line.outboundRoute.code,
                                                   $scope.line.outboundRoute.vehicleType,
                                                   $scope.mapToUrls($scope.line.outboundRoute.stations),
                                                   function(response) {if(!response.data) {routesUpdated = false;}},
                                                   $scope.line.outboundRoute._links.self.href
                                                  );

    $q.all([inboundRoutePromise, outboundRoutePromise]).then(function() {
      if (!routesUpdated)
        return;

      $scope.persistLine($scope.line.code,
                         $scope.line.name,
                         $scope.line.agencyUrl,
                         $scope.line.url,
                         $scope.line.inboundRoute.url,
                         $scope.line.outboundRoute.url,
                         function(result) {$uibModalInstance.close(result);},
                         lineData._links.self.href);
    });
  };
});
