import './stops-page.style.css';
import PopupService from '../../services/popup/popup.service';
import RecordManagementController from '../RecordManagementController'

class StopsPageController extends RecordManagementController {

  constructor($scope, $timeout, $mdBottomSheet, backend, popupService) {
    super($scope, $timeout, $mdBottomSheet, "todo:editController", "todo:editTemplate", popupService);
    this.backend = backend;
  }

  async $deleteRecord(id) {
    return (await this.backend).apis.stations.deleteStation({id: id});
  }

  async $loadRecords(params) {
    const records = (await (await this.backend).apis.stations.listStations(params)).body;

    this.markers = records.map((r) => {
      return {
        name: r.name,
        location: r.location
      }
    });

    return records;
  }

  static get $inject() {
    return ['$scope', '$timeout', '$mdBottomSheet', 'backend', PopupService.name];
  }

}
export default StopsPageController;
