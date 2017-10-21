import template from './agencies-page.template.html';
import angular from 'angular';
import './agencies-page.style.css';
import PopupService from '../../services/popup/popup.service';

const RESULT_PER_PAGE = 25;

class AgenciesPageComponent {

  constructor() {
    this.controller = AgenciesPageComponentController;
    this.template = template;
  }

  static get name() {
    return 'agenciesPage';
  }
}

class AgenciesPageComponentController {

  constructor($state, $scope, $mdBottomSheet, backend, popupService) {
    this.$state = $state;
    this.$scope = $scope;
    this.$mdBottomSheet = $mdBottomSheet;
    this.backend = backend;
    this.popupService = popupService;

    this.agencies = [];
    this.nextPage = 0;
    this.isLastPage = false;
  }

  async nextAgencies(name) {
    if (this.isLastPage)
      return;

    const nextPageAgencies = (await this.fetchAgencies(this.nextPage, name)).body;

    this.nextPage++;
    this.isLastPage = nextPageAgencies.length < RESULT_PER_PAGE;

    this.agencies = this.agencies.concat(nextPageAgencies);
    this.$scope.$apply();
  }

  async resetAgencies(name) {
    this.nextPage = 0;

    this.agencies = (await this.fetchAgencies(this.nextPage, name)).body;

    this.nextPage++;
    this.isLastPage = !this.agencies || this.agencies.length < RESULT_PER_PAGE;

    this.$scope.$apply();
  }

  async fetchAgencies(page, name) {
    const client = await this.backend;

    const params = {"page": page, 'size': RESULT_PER_PAGE};
    if (name) params['filter'] = 'name,' + name;

    return client.apis.agencies.listAgencies(params);
  }

  async deleteAgency(agency) {
    const deleteConfirmed = await this.popupService.confirmRemoval();

    if (deleteConfirmed) {
      const client = await this.backend;

      try {
        await client.apis.agencies.deleteAgency({id: agency.id});

        this.agencies = this.agencies.filter(a => {
          return a.id !== agency.id;
        });
        this.$scope.$apply();

        this.popupService.notifyRemoval(agency.name)
      } catch (err) {
        throw Error('Failed to delete agency', err);
      }
    }
  }

  async editAgency(agency) {
    const oldAgency = angular.copy(agency);

    try {
      await this.$mdBottomSheet.show({
        template: '<agencies-edit agency="$ctrl.agency"></agencies-edit>',
        controllerAs: '$ctrl',
        controller: function () {
          this.agency = agency;
        }
      });
      this.popupService.notifySaved(agency.name)
    } catch(err) {
      angular.copy(oldAgency, agency);
      this.$scope.$apply();
    }
  }

  async createAgency() {
    const newAgency = {id: null, name: null, language: null, timeZone: null, phone: null};

    try {
      await this.$mdBottomSheet.show({
        template: '<agencies-edit agency="$ctrl.agency"></agencies-edit>',
        controllerAs: '$ctrl',
        controller: function () {
          this.agency = newAgency;
        }
      });

      this.popupService.notifySaved(newAgency.name);

      this.agencies.unshift(newAgency);
      this.$scope.$apply();
    } catch(err) {
      // create dialog was canceled
    }
  }


  static get $inject() {
    return ['$state', '$scope', '$mdBottomSheet', 'backend', PopupService.name];
  }

}
export default AgenciesPageComponent;
