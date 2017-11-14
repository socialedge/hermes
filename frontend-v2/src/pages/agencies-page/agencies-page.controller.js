import './agencies-page.style.css';

import RecordManagementController from '../RecordManagementController'
import PopupService from '../../services/popup/popup.service';

import AgencyEditController from './agency-edit/agency-edit.controller';
import AgencyEditTemplate from './agency-edit/agency-edit.template.html';

class AgenciesPageController extends RecordManagementController {

  constructor($scope, $timeout, $mdBottomSheet, backend, popupService) {
    super($scope, $timeout, $mdBottomSheet, AgencyEditController, AgencyEditTemplate, popupService);
    this.backend = backend;
  }

  async $deleteRecord(id) {
    return (await this.backend).apis.agencies.deleteAgency({id: id});
  }

  async $fetchRecords(params) {
    return (await (await this.backend).apis.agencies.listAgencies(params)).body;
  }

  static get $inject() {
    return ['$scope', '$timeout', '$mdBottomSheet', 'backend', PopupService.name];
  }

}
export default AgenciesPageController;
