'use strict';

angular.module('hermesApp').controller('LinesCtrl', function ($q, $scope, $http, $uibModal, $window, env) {
  const DEFAULT_PAGE_SIZE = 25;

  function fetchLines(pageIndex, callback, pageSize) {
    pageSize = pageSize || DEFAULT_PAGE_SIZE;

    $http.get(env.backendBaseUrl + "/lines?size=" + pageSize + "&page=" + pageIndex)
      .then(function (response) {
        var lines = response.data._embedded.lines;

        var embeddedPromises = [];
        angular.forEach(lines, function(line, index) {
          var agencyPromise = $http.get(line._links.agency.href)
            .then(function (agencyResponse) {
              lines[index].agency = agencyResponse.data;
            });
          var inboundRoutePromise = $http.get(line._links.inboundRoute.href)
            .then(function (inboundRouteResponse) {
              lines[index].inboundRoute = inboundRouteResponse.data;

              var inboundRouteStationsPromise = $http.get(inboundRouteResponse.data._links.stations.href)
                .then(function (inboundRouteStationsResponse) {
                  lines[index].inboundRoute.stations = inboundRouteStationsResponse.data._embedded.stations;
                });
              embeddedPromises.push(inboundRouteStationsPromise);
            });

          if (line._links.outboundRoute) {
            var outboundRoutePromise = $http.get(line._links.outboundRoute.href)
              .then(function (outboundRouteResponse) {
                lines[index].outboundRoute = outboundRouteResponse.data;

                var outboundRouteStationsPromise = $http.get(outboundRouteResponse.data._links.stations.href)
                  .then(function (outboundRouteStationsResponse) {
                    lines[index].outboundRoute.stations = outboundRouteStationsResponse.data._embedded.stations;
                  });
                embeddedPromises.push(outboundRouteStationsPromise);
              });
          }

          embeddedPromises.push(agencyPromise, outboundRoutePromise, inboundRoutePromise);
        });

        $q.all(embeddedPromises).then(function() {
          callback(response.data);
        });
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
});
