import template from './stops-page.template.html';
import './stops-page.style.css';
import PopupService from '../../services/popup/popup.service';
import RecordManagementController from '../RecordManagementController'

class StopsPageComponent {

  constructor() {
    this.controller = StopsPageComponentController;
    this.template = template;
  }

  static get name() {
    return 'stopsPage';
  }
}

class StopsPageComponentController extends RecordManagementController {

  constructor($scope, $timeout, $mdBottomSheet, backend, popupService) {
    super($scope, $timeout, $mdBottomSheet, "todo:editController", "todo:editTemplate", popupService);
    this.backend = backend;
  }

  async $deleteRecord(id) {
    return (await this.backend).apis.stations.deleteStation({id: id});
  }

  async $loadRecords(params) {
    return (await (await this.backend).apis.stations.listStations(params)).body;
  }

  static get $inject() {
    return ['$scope', '$timeout', '$mdBottomSheet', 'backend', PopupService.name];
  }

}
export default StopsPageComponent;
