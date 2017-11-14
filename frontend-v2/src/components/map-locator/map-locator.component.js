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

const MARKER_ACTION_ICON = 'http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=%E2%80%A2|ddd';
const ACTION_BUTTON_OFFSET = 5;

class MapLocatorComponent {

  constructor() {
    this.controller = MapLocatorComponentController;
    this.template = template;

    this.bindings = {
      markers: '<',
      actionTitle: '@',
      actionCallback: '&',
      mapClickedCallback: '&',
      markerClickedCallback: '&',
      markerMoveCallback: '&',
      markerMovedCallback: '&',
      markerDeselectedCallback: '&'
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

    this.gMarkers = [];
    this.rClickAction = null;
  }

  async $onInit() {
    this.$initialized = true;

    const map = await(this.$ngMap.getMap());

    map.addListener('click', () => {
      this.hideAction();
      this.notifyMapClicked();

      this.$scope.$apply();
    });

    map.addListener('drag', () => {
      this.hideAction();

      this.$scope.$apply();
    });

    map.addListener('rightclick', async (e) => {
      await this.showAction(e.pixel.x, e.pixel.y, e.latLng);

      this.$scope.$apply();
    });
  }

  async $onChanges(change) {
    if (!this.$initialized)
      return;

    if (change.hasOwnProperty("markers")) {
      if (this.$onChangesPromise)
        await this.$onChangesPromise;

      this.clearMapMarkers();
      this.$onChangesPromise = this.renderMarkers();
    }
  }

  async renderMarkers() {
    const map = await(this.$ngMap.getMap());

    // Fallback to default location if no markers found
    if (!this.markers || this.markers.length === 0) {
      return this.resetMap();
    }

    const bounds = new google.maps.LatLngBounds();
    for (let i = 0; i < this.markers.length; i++) {
      const marker = this.markers[i];

      const id = marker.id;
      const latlng = new google.maps.LatLng(marker.location.latitude,
        marker.location.longitude);

      await this.addMapMarker(id, latlng);

      bounds.extend(latlng);
    }

    // Fit the map
    map.setCenter(bounds.getCenter());
    map.fitBounds(bounds);

    // Prevent overzooming
    if (this.markers.length <= 2) {
      map.setZoom(16);
    }
  }

  async addMapMarker(id, latLng) {
    const map = await(this.$ngMap.getMap());

    const mapMarker = new google.maps.Marker({
      position: latLng,
      map: map,
      draggable:true
    });
    this.gMarkers.push(mapMarker);

    mapMarker.addListener('dragstart', (e) => {
      this.notifyMarkerMove(id);
    });
    mapMarker.addListener('dragend', (e) => {
      this.notifyMarkerMoved(id, e.latLng);
    });

    mapMarker.addListener('click', async () => {
      await this.panMapTo(mapMarker.getPosition());
      this.notifyMarkerClicked(id);

      this.$scope.$apply();
    });
  }

  async showAction(x, y, latLng) {
    this.hideAction();

    const map = await(this.$ngMap.getMap());

    this.rClickAction = {
      button: {
        enabled: true,
        top: y + ACTION_BUTTON_OFFSET,
        left: x + ACTION_BUTTON_OFFSET
      },
      marker: new google.maps.Marker({
        position: latLng,
        map: map,
        icon: MARKER_ACTION_ICON
      })
    };
  }

  hideActionMarker() {
    if (this.rClickAction && this.rClickAction.marker)
      this.rClickAction.marker.setMap(null);
  }

  hideActionButton() {
    if (!this.rClickAction)
      return;

    this.rClickAction.button.enabled = false;
  }

  hideAction() {
    this.hideActionMarker();
    this.hideActionButton();
  }

  notifyMapClicked() {
    if (this.mapClickedCallback && typeof this.mapClickedCallback === "function") {
      this.mapClickedCallback();
    }
  }

  notifyMarkerClicked(id) {
    if (this.markerClickedCallback && typeof this.markerClickedCallback === "function") {
      this.markerClickedCallback({id: id});
    }
  }

  notifyMarkerMove(id) {
    if (this.markerMoveCallback && typeof this.markerMoveCallback === "function") {
      this.markerMoveCallback({id: id});
    }
  }

  notifyMarkerMoved(id, latLng) {
    if (this.markerMovedCallback && typeof this.markerMovedCallback === "function") {
      this.markerMovedCallback({id: id, location: {
        latitude: latLng.lat(),
        longitude: latLng.lng()}});
    }
  }

  async notifyActionMarkerTriggered() {
    if (!this.actionCallback || !(typeof this.actionCallback === "function"))
      return;

    const latLng = this.rClickAction.marker.getPosition();
    const location = {latitude: latLng.lat(), longitude: latLng.lng()};

    this.hideActionButton();
    await this.panMapTo(latLng);

    await this.actionCallback({location: location});

    this.hideActionMarker();
  }

  clearMapMarkers() {
    for (let i = 0; i < this.gMarkers.length; i++) {
      this.gMarkers[i].setMap(null);
    }
    this.gMarkers = [];
  }

  async resetMap() {
    return this.panMapTo({lat: appConfig.MAP_LOCATOR_DEFAULT_CENTER.lat,
      lng: appConfig.MAP_LOCATOR_DEFAULT_CENTER.lng});
  }

  async panMapTo(latLng) {
    const map = await(this.$ngMap.getMap());
    map.panTo(latLng);
  }

  static get $inject() {
    return ['$scope', '$state', '$timeout', 'NgMap'];
  }
}

export default MapLocatorComponent;
