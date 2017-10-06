import angular from 'angular';

import AppNavbar from '../app-navbar/app-navbar';

import AppContentComponent from './app-content.component';

export default angular.module('AppContent', [
  AppNavbar.name
]).component(AppContentComponent.name, new AppContentComponent);
