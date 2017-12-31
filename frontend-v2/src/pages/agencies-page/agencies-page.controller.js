import './agencies-page.style.css';

import RecordManagementController from '../record-management.controller';
import AgencyRepository from '../../repositories/agency/agency.repository';
import AgencyEditController from './agency-edit/agency-edit.controller';
import AgencyEditTemplate from './agency-edit/agency-edit.template.html';

class AgenciesPageController extends RecordManagementController {

  constructor(agencyRepository, $mdBottomSheet) {
    super(agencyRepository, $mdBottomSheet, AgencyEditController, AgencyEditTemplate);
  }

  static get $inject() {
    return [AgencyRepository.name, '$mdBottomSheet'];
  }
}

export default AgenciesPageController;
