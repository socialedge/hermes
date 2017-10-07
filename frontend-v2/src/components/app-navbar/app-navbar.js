import angular from 'angular';
import AppHeaderComponent from './app-navbar.component';

export default angular.module('AppHeader', []).component(AppHeaderComponent.name, new AppHeaderComponent);
