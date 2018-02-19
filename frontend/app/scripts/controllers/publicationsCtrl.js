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

  $scope.uploadContent = function (publication) {
    console.log("UPLOAD CONTENT FOR " + publication.name);
    // TODO
  };

  $scope.openNewPublicationModal = function () {
    $uibModal.open({
      templateUrl: 'publicationModal.html',
      controller: 'NewPublicationCtrl'
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

angular.module('hermesApp').controller('NewPublicationCtrl', function ($scope, $timeout, $http, $uibModalInstance, env) {

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
    $scope.publication = {};
  };

  $scope.savePublication = function (publication) {
    $scope.createPublication(publication, function(result) {
      $uibModalInstance.close(result);
    });
  };

  $scope.closeModal = function () {
    $uibModalInstance.dismiss('cancel');
  };
});
