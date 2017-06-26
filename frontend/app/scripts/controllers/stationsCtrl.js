'use strict';

angular.module('hermesApp').controller('StationsCtrl', function ($scope, $http, $uibModal, $window, env) {
  const DEFAULT_PAGE_SIZE = 25;

  function fetchStations(pageIndex, callback, pageSize) {
    pageSize = pageSize || DEFAULT_PAGE_SIZE;

    $http.get(env.backendBaseUrl + "/stations?size=" + pageSize + "&page=" + pageIndex)
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
    fetchStations(pageIndex, function (response) {
      $scope.page.totalItems = response.page.totalElements;
      $scope.page.itemsPerPage = response.page.size;
      $scope.page.maxSize = response.page.totalPages;
      $scope.page.currentPage = pageIndex + 1;

      $scope.page.stations = response._embedded.stations;

      if (typeof callback === 'function')
        callback($scope.page);
    }, pageSize);
  };

  $scope.refreshPageStations = function () {
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

  $scope.openNewStationModal = function () {
    $uibModal.open({
      templateUrl: 'stationModal.html',
      controller: 'NewStationsCtrl'
    }).result.then(function (newStation) {
      if (newStation.error) {
        $scope.addAlert('Error happened: \'' + newStation.error + ' \'', 'danger');
      } else {
        $scope.loadPage($scope.lastPageIndex(), function () {
          $scope.addAlert('New \'' + newStation.name + ' \' station has been successfully added!', 'success');
        });
      }
    });
  };

  $scope.openEditStationModal = function (station) {
    $uibModal.open({
      templateUrl: 'stationModal.html',
      controller: 'EditStationsCtrl',
      resolve: {
        stationData: function () {
          return angular.copy(station);
        }
      }
    }).result.then(function (editedStation) {
      if (editedStation.error) {
        $scope.addAlert('Error happened: \'' + editedStation.error + ' \'', 'danger');
      } else {
        $scope.loadPage($scope.currentPageIndex(), function () {
          $scope.addAlert('Station \'' + editedStation.name + ' \' has been successfully saved!');
        });
      }
    });
  };

  $scope.deleteStation = function (name, location) {
    $http.delete(location).then(function () {
      $scope.loadPage($scope.currentPageIndex(), function () {
        $scope.addAlert('Station \'' + name + ' \' has been deleted!', 'success');
      });
    }, function (error) {
      $scope.addAlert('Error happened: \'' + error.data.message + ' \'', 'danger');
    });
  };
});

angular.module('hermesApp').controller('StationLocPopoverCtrl', function ($scope, $timeout) {
  const GMAP_ID_PREFIX = "gmap-loc";
  const GMAPS_GEOCODER = new google.maps.Geocoder();

  $scope.location = {};

  $scope.initLocPopover = function (index, label, lat, lng) {
    $scope.location.label = label;
    $scope.location.lat = lat;
    $scope.location.lng = lng;
    $scope.location.gmapId = GMAP_ID_PREFIX + index;

    decodeAddress(lat, lng, function (address) {
      $timeout(function () {
        $scope.location.address = address;
      });
    });
  };

  $scope.displayLocation = function (label, lat, lng) {
    $timeout(function () {
      const GMAP_EL = document.getElementById($scope.location.gmapId);

      const locGMapStationLatLng = new google.maps.LatLng(lat, lng);
      const locGMapObj = new google.maps.Map(GMAP_EL, {
        zoom: 18,
        center: locGMapStationLatLng,
        scrollwheel: false
      });
      const locGMapStationTooltip = new google.maps.InfoWindow({
        content: label
      });
      const locGMapStationMarker = new google.maps.Marker({
        position: locGMapStationLatLng,
        map: locGMapObj
      });

      locGMapStationTooltip.open(locGMapObj, locGMapStationMarker);
    });
  };

  function decodeAddress(lat, lng, callback) {
    if (!lat || !lng)
      callback("N/A");

    var latlng = new google.maps.LatLng(lat, lng);
    GMAPS_GEOCODER.geocode({
      'latLng': latlng
    }, function (results, status) {
      if (status === google.maps.GeocoderStatus.OK && results[1]) {
        const num = results[1].address_components[0].short_name;
        const str = results[1].address_components[1].short_name;
        callback(num + " " + str);
      } else {
        callback("N/A");
      }
    });
  }
});

angular.module('hermesApp').controller('AbstractStationModalCtrl', function ($scope, $timeout, $http, $uibModalInstance, env) {
  $scope.drawMap = function (lat, lng, listener, mapElement, zoom) {
    zoom = zoom || 17;
    mapElement = mapElement || document.getElementById('gmap-picker');

    var map, marker,
      initialLocation = new google.maps.LatLng(lat, lng);

    function notifyListener() {
      var currentLocation = marker.getPosition();
      if (typeof listener === 'function')
        listener(currentLocation.lat(), currentLocation.lng());
    }

    map = new google.maps.Map(mapElement, {
      mapTypeId: google.maps.MapTypeId.ROADMAP,
      zoom: zoom,
      center: initialLocation
    });
    marker = new google.maps.Marker({
      position: initialLocation,
      map: map,
      draggable: true
    });

    google.maps.event.addListener(marker, 'dragend', function (event) {
      notifyListener();
    });
    google.maps.event.addListener(map, 'click', function (event) {
      var clickedLocation = event.latLng;
      marker.setPosition(clickedLocation);
      notifyListener();
    });

    $timeout(function () {
      const currCenter = map.getCenter();
      google.maps.event.trigger(map, 'resize');
      map.setCenter(currCenter);
    }, 100);
  };

  $scope.persistStation = function (name, desc, vehTypes, loc, isHail, dwellTime, callback, url) {
    const reqData = {
      name: name,
      description: desc,
      vehicleTypes: vehTypes,
      location: {latitude: loc.lat, longitude: loc.lng},
      dwells: [{
        probability: isHail ? 0 : 1,
        dwellTime: "PT" + dwellTime + "S",
        from: "00:00:00",
        to: "23:59:59"
      }],
      hailStop: isHail
    };

    if (!url) {
      $http.post(env.backendBaseUrl + "/stations", reqData)
        .then(function (response) {
          if (typeof callback === 'function')
            callback({
              name: response.data.name,
              href: response.data._links.station.href
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
              href: response.data._links.station.href
            });
        }, function (error) {
          if (typeof callback === 'function')
            callback({
              error: error
            });
        });
    }
  };

  $scope.closeModal = function () {
    $uibModalInstance.dismiss('cancel');
  };
});

angular.module('hermesApp').controller('EditStationsCtrl', function ($scope, $controller, $http, $timeout, $uibModalInstance, stationData) {
  angular.extend(this, $controller('AbstractStationModalCtrl', {$scope: $scope, $uibModalInstance: $uibModalInstance}));
  var $ctrl = this;

  $scope.initModal = function () {
    $.material.init();

    // Fill form with old stationData
    $scope.station = {
      name: stationData.name,
      desc: stationData.description,
      isHail: stationData.dwells[0].probability < 1,
      dwellTime: parseFloat(stationData.dwells[0].dwellTime.replace(/PT(\d.+)S/, "$1")),
      location: {lat: stationData.location.latitude, lng: stationData.location.longitude}
    };
    console.log(stationData.dwells[0].probability);
    $scope.station.type = {};
    if (stationData.vehicleTypes.indexOf("BUS") !== -1) $scope.station.type.bus = true;
    if (stationData.vehicleTypes.indexOf("TROLLEYBUS") !== -1) $scope.station.type.trolley = true;
  };


  $scope.initMap = function () {
    $scope.drawMap(stationData.location.latitude, stationData.location.longitude, function (lat, lng) {
      $scope.$apply(function () {
        $scope.station.location.lat = lat;
        $scope.station.location.lng = lng;
      });
    })
  };

  $scope.saveStation = function (station) {
    $scope.persistStation(station.name, station.desc, (function () {
      const vehTypes = [];

      if (station.type.bus)
        vehTypes.push("BUS");
      if (station.type.trolley)
        vehTypes.push("TROLLEYBUS");

      return vehTypes;
    })(), {lat: station.location.lat, lng: station.location.lng}, station.isHail, station.dwellTime, function(result) {
      $uibModalInstance.close(result);
    }, stationData._links.station.href);
  };
});


angular.module('hermesApp').controller('NewStationsCtrl', function ($scope, $controller, $http, $uibModalInstance, $timeout, env) {
  angular.extend(this, $controller('AbstractStationModalCtrl', {$scope: $scope, $uibModalInstance: $uibModalInstance}));
  var $ctrl = this;

  // Sumy City
  const defLat = 50.90755228411547;
  const defLng = 34.798035621643066;

  $scope.initModal = function () {
    $.material.init();
    $scope.station = {location: {lat: defLat, lng: defLng}};
  };

  $scope.initMap = function () {
    $scope.drawMap(defLat, defLng, function (lat, lng) {
      $scope.$apply(function () {
        $scope.station.location.lat = lat;
        $scope.station.location.lng = lng;
      });
    })
  };

  $scope.saveStation = function (station) {
    $scope.persistStation(station.name, station.desc, (function () {
      const vehTypes = [];

      if (station.type.bus)
        vehTypes.push("BUS");
      if (station.type.trolley)
        vehTypes.push("TROLLEYBUS");

      return vehTypes;
    })(), {lat: station.location.lat, lng: station.location.lng}, station.isHail, station.dwellTime, function(result) {
      $uibModalInstance.close(result);
    });
  };
});
