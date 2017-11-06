/*
 * Hermes - The Municipal Transport Timetable System
 * Copyright (c) 2016-2017 SocialEdge
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

import template from './map-locator.template.html';
import './map-locator.style.css';
import appConfig from 'app.config';

const DEFAULT_AUTO_HIDE_MARKER = true;
const MARKER_ACTION_ICON = 'http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=%E2%80%A2|ddd';

class MapLocatorComponent {

  constructor() {
    this.controller = MapLocatorComponentController;
    this.template = template;

    this.bindings = {
      markers: '<',
      actionMarkerTitle: '@',
      actionMarkerCallback: '&'
    }
  }

  static get name() {
    return 'mapLocator';
  }
}

class MapLocatorComponentController {

  constructor($scope, $state, $timeout, NgMap) {
    this.$scope = $scope;
    this.$state = $state;
    this.$timeout = $timeout;
    this.$ngMap = NgMap;
  }

  async $onInit() {
    this.initEnv();
    this.initActionMarker();
    this._initialized = true;
  }

  initEnv() {
    this._mapMarkers = [];
    this._mapInfoWindows = [];
    this._actionMarker = {left:0, top:0, enabled:false, marker:null};
  }

  async initActionMarker() {
    const map = await(this.$ngMap.getMap());

    map.addListener('click', () => {
      this.hideAllInfoWindows();
    });

    map.addListener('click', (e) => {
      if (this._actionMarker.enabled) {
        this.hideActionMarker();
        return;
      }

      this._actionMarker.enabled = true;
      this._actionMarker.top = e.pixel.y + 5;
      this._actionMarker.left = e.pixel.x + 5;

      if (this._actionMarker.marker) {
        this._actionMarker.marker.setMap(null);
      }

      this._actionMarker.marker = new google.maps.Marker({
        position: e.latLng,
        map: map,
        icon: MARKER_ACTION_ICON
      });

      this.$scope.$apply();
    });
  }

  hideActionMarker() {
    this._actionMarker.marker.setMap(null);
    this._actionMarker.enabled = false;
    this.$scope.$apply();
  }

  async triggerActionMarker() {
    const lat = this._actionMarker.marker.getPosition().lat();
    const lng = this._actionMarker.marker.getPosition().lng();

    const location = {latitude: lat, longitude: lng};

    await this.actionMarkerCallback({location: location});

    this.hideActionMarker();
  }

  async $onChanges() {
    if (!this._initialized)
      return;

    this.clearMapMarkers();

    const map = await(this.$ngMap.getMap());

    // Fallback to default location if no markers found
    if (this.markers.length === 0) {
      const DEFAULT_LATLNG = new google.maps.LatLng(appConfig.MAP_LOCATOR_FALLBACK_LOCATION[0],
        appConfig.MAP_LOCATOR_FALLBACK_LOCATION[1]);

      map.setCenter(DEFAULT_LATLNG);
      return;
    }

    const bounds = new google.maps.LatLngBounds();

    for (let i = 0; i < this.markers.length; i++) {
      const marker = this.markers[i];
      const latlng = new google.maps.LatLng(marker.location.latitude, marker.location.longitude);

      this.addMapMarker(map, latlng, marker.name);

      bounds.extend(latlng);
    }

    // Fit the map
    map.setCenter(bounds.getCenter());
    map.fitBounds(bounds);

    // Prevent overzooming
    if (this.markers.length <= 2) {
      map.setZoom(18);
    }
  }

  addMapMarker(map, latLng, info) {
    const mapMarker = new google.maps.Marker({
      position: latLng,
      map: map,
      title: info
    });
    this._mapMarkers.push(mapMarker);

    const infoWindow = new google.maps.InfoWindow({
      content: info
    });
    this._mapInfoWindows.push(infoWindow);

    mapMarker.addListener('click', () => {
      this.hideAllInfoWindows();
      infoWindow.open(map, mapMarker);
    });
  }

  hideAllInfoWindows() {
    for (let i = 0; i < this._mapInfoWindows.length; i++) {
      this._mapInfoWindows[i].close();
    }
  }

  clearMapMarkers() {
    for (let i = 0; i < this._mapMarkers.length; i++) {
      this._mapMarkers[i].setMap(null);
    }
    this._mapMarkers = [];
  }

  static get $inject() {
    return ['$scope', '$state', '$timeout', 'NgMap'];
  }
}

export default MapLocatorComponent;
