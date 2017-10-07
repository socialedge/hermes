import angular from 'angular';

import AppContentComponent from './app-content.component';
import AppNavbar from '../app-navbar/app-navbar';

import StopsPage from '../stops-page/stops-page'
import LinesPage from '../lines-page/lines-page'
import SchedulesPage from '../schedules-page/schedules-page'
import PublicationsPage from '../publications-page/publications-page'

export default angular.module('AppContent', [
  AppNavbar.name,
  StopsPage.name,
  LinesPage.name,
  SchedulesPage.name,
  PublicationsPage.name
]).component(AppContentComponent.name, new AppContentComponent);
