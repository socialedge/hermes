import './stops-page.style.css';
import PopupService from '../../services/popup/popup.service';
import RecordManagementController from '../RecordManagementController';

import StopEditController from './stop-edit/stop-edit.controller';
import StopEditTemplate from './stop-edit/stop-edit.template.html';

const DISABLE_PAGING = true;

class StopsPageController extends RecordManagementController {

  constructor($scope, $timeout, $mdBottomSheet, backend, popupService) {
    super($scope, $timeout, $mdBottomSheet, StopEditController, StopEditTemplate, popupService, DISABLE_PAGING);
    this.backend = backend;

    this.markers = [];
    this.highlightedStationId = null;
  }

  $onInit() {
    this.$scope.$watch(() => this.records, () => {
      this.refreshMarkers();
    }, true);
  }

  refreshMarkers() {
    this.markers = this.records.map((station) => {
      return {
        id: station.id,
        location: {
          latitude: station.location.latitude,
          longitude: station.location.longitude
        }
      }
    });
  }

  async moveStation(id, location) {
    this.highlightStation(id);

    const stationMoved = this.records.find(station => station.id === id);
    stationMoved.location = location;

    await (await this.backend).apis.stations.replaceStation({id: stationMoved.id, body: stationMoved});

    this.popupService.notifySaved();
  }

  async searchRecords(filter) {
    this.highlightedStationId = null;
    return super.searchRecords(filter);
  }

  async createRecord(location) {
    return super.createRecord({location: location});
  }

  async $deleteRecord(id) {
    return (await this.backend).apis.stations.deleteStation({id: id});
  }

  async $fetchRecords(params) {
    return (await (await this.backend).apis.stations.listStations(params)).body;
  }

  highlightStation(id) {
    this.unHighlightStation();
    this.highlightedStationId = id;
    this.$scope.$apply();

    this.$timeout(() => {
      document.querySelector('.stops-list').scrollTop = document.querySelector('.stops-list .stop-item-highlighted').offsetTop;
    }, 100);
  }

  unHighlightStation() {
    this.highlightedStationId = null;
    this.$scope.$apply();
  }

  static get $inject() {
    return ['$scope', '$timeout', '$mdBottomSheet', 'backend', PopupService.name];
  }

}
export default StopsPageController;
