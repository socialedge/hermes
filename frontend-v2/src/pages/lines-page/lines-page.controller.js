import './lines-page.style.css';
import RecordManagementController from "../RecordManagementController";
import PopupService from "../../services/popup/popup.service";

const DISABLE_PAGING = true;

class LinesPageController extends RecordManagementController {

  constructor($scope, $timeout, $mdBottomSheet, backend, popupService) {
    super($scope, $timeout, $mdBottomSheet, null, null, popupService, DISABLE_PAGING);
    this.backend = backend;
  }

  async $deleteRecord(id) {
    return (await this.backend).apis.lines.deleteLine({id: id});
  }

  async $fetchRecords(params) {
    return (await (await this.backend).apis.lines.listLines(params)).body;
  }

  static get $inject() {
    return ['$scope', '$timeout', '$mdBottomSheet', 'backend', PopupService.name];
  }
}

export default LinesPageController;
