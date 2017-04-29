'use strict';

angular.module('hermesApp').controller('StationsCtrl', function ($scope, $http, $uibModal, $window, env) {
  $scope.alerts = [];
  $scope.station = {};

  function loadStations(page, callback, pageSize) {
    pageSize = pageSize || 25;

    $http.get(env.backendBaseUrl + "/stations?size=" + pageSize + "&page=" + page)
      .then(function (response) {
        callback(response.data);
      });
  }

  loadStations(0, function (data) {
    $scope.totalItems = data.page.totalElements;
    $scope.itemsPerPage = data.page.size;
    $scope.maxSize = data.page.totalPages;
    $scope.currentPage = 1;

    $scope.stations = data._embedded.stations;
  });

  $scope.pageChanged = function () {
    loadStations($scope.currentPage - 1, function (data) {
      $scope.stations = data._embedded.stations;
    });
  };

  $scope.mapPopover = function (e, name, latitude, longitude) {
    var locPreview = angular.element(e.currentTarget);

    if (locPreview.hasClass("enabled"))
      return;

    locPreview.find(".collapsible").remove();

    var popoverMap = locPreview.find(".gmap");
    popoverMap.css('display', 'block');

    var myLatLng = {lat: latitude, lng: longitude};

    var map = new google.maps.Map(popoverMap[0], {
      zoom: 18,
      center: myLatLng,
      scrollwheel: false
    });
    var infowindow = new google.maps.InfoWindow({
      content: name
    });
    var marker = new google.maps.Marker({
      position: myLatLng,
      map: map
    });
    infowindow.open(map, marker);

    locPreview.addClass("enabled");
  };

  $scope.openStationNewModal = function () {
    $uibModal.open({
      templateUrl: 'stationModal.html',
      controller: 'NewStationsCtrl'
    }).result.then(function (station) {
      if (station.href) {
        var lastPageIndex = $scope.maxSize - 1;
        loadStations(lastPageIndex, function (data) {
          $scope.totalItems = data.page.totalElements;
          $scope.itemsPerPage = data.page.size;
          $scope.maxSize = data.page.totalPages;
          $scope.currentPage = lastPageIndex + 1;

          $scope.stations = data._embedded.stations;

          $scope.alerts.push({
            type: 'success',
            msg: 'New \'' + station.name + ' \' station has been successfully added!'
          });
          $window.scrollTo(0, 0);
        });
      } else if (station.error) {
        $scope.alerts.push({type: 'danger', msg: 'Error happened: \'' + station.error + ' \''});
        $window.scrollTo(0, 0);
      }
    });
  };

  $scope.openStationEditModal = function (station) {
    $uibModal.open({
      templateUrl: 'stationModal.html',
      controller: 'EditStationsCtrl',
      resolve: {
        data: function () {
          return angular.copy(station);
        }
      }
    }).result.then(function (station) {
      if (station.href) {
        var currPageIndex = $scope.currentPage - 1;
        loadStations(currPageIndex, function (data) {
          $scope.totalItems = data.page.totalElements;
          $scope.itemsPerPage = data.page.size;
          $scope.maxSize = data.page.totalPages;
          $scope.currentPage = currPageIndex + 1;

          $scope.stations = data._embedded.stations;

          $scope.alerts.push({type: 'success', msg: 'Station \'' + station.name + ' \' has been successfully saved!'});
          $window.scrollTo(0, 0);
        });
      } else if (station.error) {
        $scope.alerts.push({type: 'danger', msg: 'Error happened: \'' + station.error + ' \''});
        $window.scrollTo(0, 0);
      }
    });
  };

  $scope.closeAlert = function (index) {
    $scope.alerts.splice(index, 1);
  };

  $scope.deleteStation = function (name, url) {
    $http.delete(url).then(function (response) {
      var currPageIndex = $scope.currentPage - 1;
      loadStations(currPageIndex, function (data) {
        $scope.totalItems = data.page.totalElements;
        $scope.itemsPerPage = data.page.size;
        $scope.maxSize = data.page.totalPages;
        $scope.currentPage = currPageIndex + 1;

        $scope.stations = data._embedded.stations;

        $scope.alerts.push({type: 'success', msg: 'Station \'' + name + ' \' has been deleted!'});
        $window.scrollTo(0, 0);
      });
    }, function (error) {
      $scope.alerts.push({type: 'danger', msg: 'Error happened: \'' + error.data.message + ' \''});
      $window.scrollTo(0, 0);
    });
  }
});

angular.module('hermesApp').controller('EditStationsCtrl', function ($scope, $http, $timeout, $uibModalInstance, data, env) {
  console.log(data);
  var $ctrl = this;

  $scope.station = {
    name: data.name,
    desc: data.description,
    isHail: data.isHailStop,
    location: {lat: data.location.latitude, lng: data.location.longitude}
  };

  $scope.station.type = {};
  if (data.vehicleTypes.indexOf("BUS") !== -1) $scope.station.type.bus = true;
  if (data.vehicleTypes.indexOf("TROLLEYBUS") !== -1) $scope.station.type.trolley = true;

  $scope.initModal = function () {
    $.material.init();
  };

  $scope.drawMap = function () {
    var oldLoc = new google.maps.LatLng(data.location.latitude, data.location.longitude);

    var map, marker;

    function markerLocation() {
      var currentLocation = marker.getPosition();

      $scope.$apply(function () {
        $scope.station.location.lat = currentLocation.lat();
        $scope.station.location.lng = currentLocation.lng();
        data.location = {latitude: $scope.station.location.lat, longitude: $scope.station.location.lng};
      });
    }

    map = new google.maps.Map(document.getElementById('gmap-picker'), {
      mapTypeId: google.maps.MapTypeId.ROADMAP,
      zoom: 17,
      center: oldLoc
    });
    marker = new google.maps.Marker({
      position: oldLoc,
      map: map,
      draggable: true
    });

    google.maps.event.addListener(marker, 'dragend', function (event) {
      markerLocation();
    });
    google.maps.event.addListener(map, 'click', function (event) {
      var clickedLocation = event.latLng;
      marker.setPosition(clickedLocation);
      markerLocation();
    });

    $timeout(function () {
      var currCenter = map.getCenter();
      google.maps.event.trigger(map, 'resize');
      map.setCenter(currCenter);
    }, 100);
  };

  $scope.saveStation = function (station) {
    var vehTypes = [];
    if (station.type.bus) vehTypes.push("BUS");
    if (station.type.trolley) vehTypes.push("TROLLEYBUS");

    data.vehicleTypes = vehTypes;
    data.name = station.name;
    data.description = station.desc;
    data.location = {latitude: station.location.lat, longitude: station.location.lng};
    data.isHailStop = station.isHail;

    var location = data._links.station.href;
    delete data._links;

    $http.patch(location, data)
      .then(function (response) {
        $uibModalInstance.close({
          name: response.data.name,
          href: response.data._links.station.href
        });
      }, function (error) {
        $uibModalInstance.close({
          error: error
        });
        console.log("Error happened!", error);
      });
  };

  $scope.closeModal = function () {
    $uibModalInstance.dismiss('cancel');
  };
});


angular.module('hermesApp').controller('NewStationsCtrl', function ($scope, $http, $uibModalInstance, $timeout, env) {
  var $ctrl = this;

  const defLat = 50.90755228411547;
  const defLng = 34.798035621643066;
  const defLatLng = new google.maps.LatLng(defLat, defLng);
  $scope.station = {location: {lat: defLat, lng: defLng}};

  $scope.initModal = function () {
    $.material.init();
  };

  $scope.drawMap = function () {

    function markerLocation() {
      var currentLocation = marker.getPosition();

      $scope.$apply(function () {
        $scope.station.location.lat = currentLocation.lat();
        $scope.station.location.lng = currentLocation.lng();
      })
    }

    console.log(defLatLng, defLatLng.lat(), defLatLng.lng());
    var map = new google.maps.Map(document.getElementById('gmap-picker'), {
      mapTypeId: google.maps.MapTypeId.ROADMAP,
      zoom: 17,
      center: defLatLng
    });
    var marker = new google.maps.Marker({
      position: defLatLng,
      map: map,
      draggable: true
    });

    google.maps.event.addListener(marker, 'dragend', function (event) {
      markerLocation();
    });
    google.maps.event.addListener(map, 'click', function (event) {
      var clickedLocation = event.latLng;
      marker.setPosition(clickedLocation);
      markerLocation();
    });

    $timeout(function () {
      var currCenter = map.getCenter();
      google.maps.event.trigger(map, 'resize');
      map.setCenter(currCenter);
    }, 100);
  };


  $scope.saveStation = function (station) {
    var vehTypes = [];
    if (station.type.bus) vehTypes.push("BUS");
    if (station.type.trolley) vehTypes.push("TROLLEYBUS");

    var dataReq = {
      vehicleTypes: vehTypes,
      name: station.name,
      description: station.desc,
      location: {latitude: station.location.lat, longitude: station.location.lng},
      isHailStop: station.isHail
    };

    $http.post(env.backendBaseUrl + "/stations", dataReq)
      .then(function (response) {
        $uibModalInstance.close({
          name: response.data.name,
          href: response.data._links.station.href
        });
      }, function (error) {
        $uibModalInstance.close({
          error: error
        });
        console.log("Error happened!", error);
      });
  };

  $scope.closeModal = function () {
    $uibModalInstance.dismiss('cancel');
  };
});
