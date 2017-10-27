import template from './stops-page.template.html';
import './stops-page.style.css';
import PopupService from '../../services/popup/popup.service';

const RESULT_PER_PAGE = 25;

class StopsPageComponent {

  constructor() {
    this.controller = StopsPageComponentController;
    this.template = template;
  }

  static get name() {
    return 'stopsPage';
  }
}
class StopsPageComponentController {

  constructor($state, $scope, $timeout, $mdBottomSheet, backend, popupService) {
    this.$state = $state;
    this.$scope = $scope;
    this.$timeout = $timeout;
    this.$mdBottomSheet = $mdBottomSheet;
    this.backend = backend;
    this.popupService = popupService;

    this.stations = [];
    this.nextPage = 0;
    this.isLastPage = false;

    this.showLoading();
  }

  async nextStations(name) {
    console.log("nextStations");
    if (this.isLastPage)
      return;

    this.showLoading();

    const nextPageStations = (await this.fetchStations(this.nextPage, name)).body;

    this.nextPage++;
    this.isLastPage = nextPageStations.length < RESULT_PER_PAGE;

    this.stations = this.stations.concat(nextPageStations);
    this.$scope.$apply();

    this.hideLoading();
  }

  async resetStations(name) {
    this.showLoading();

    this.nextPage = 0;
    this.stations = (await this.fetchStations(this.nextPage, name)).body;

    this.nextPage++;
    this.isLastPage = !this.stations || this.stations.length < RESULT_PER_PAGE;
    this.$scope.$apply();

    this.hideLoading();
  }

  async fetchStations(page, name) {
    const client = await this.backend;

    const params = {"page": page, 'size': RESULT_PER_PAGE};
    if (name) params['filter'] = 'name,' + name;

    return client.apis.stations.listStations(params);
  }

  showLoading() {
    this.loading = true;
  }

  hideLoading() {
    let self = this;
    this.$timeout(() => {
      self.loading = false;
      console.log("HIDE!")
    }, 1000);
  }

  static get $inject() {
    return ['$state', '$scope', '$timeout', '$mdBottomSheet', 'backend', PopupService.name];
  }

}
export default StopsPageComponent;
