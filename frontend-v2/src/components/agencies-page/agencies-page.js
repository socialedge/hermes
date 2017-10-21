import angular from 'angular';
import AgenciesPageComponent from './agencies-page.component';
import AgenciesEditComponent from './agencies-edit/agencies-edit';

export default angular.module('AgenciesPage', [
  AgenciesEditComponent.name
]).component(AgenciesPageComponent.name, new AgenciesPageComponent);
