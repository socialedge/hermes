'use strict';

import angular from 'angular';
import ngAnimate from 'angular-animate';
import ngAria from 'angular-aria';
import ngMaterial from 'angular-material';
import ngMessages from 'angular-messages';
import ngTranslate from 'angular-translate';
import ngTranslateLoaderStaticFiles from 'angular-translate-loader-static-files';
import ngInfiniteScroll from 'ng-infinite-scroll';
import uiRouter from '@uirouter/angularjs';
import ngMap from 'ngmap';

import './styles/main.css';
import 'angular-material/angular-material.css';

import appConfigEnv from './app.config.env';
import appConfigI18n from './app.config.i18n';
import appConfigRoute from './app.config.route';
import appConfigStyle from './app.config.style';
import appConfigSwagger from './app.config.swagger';

import PopupService from './services/popup/popup';

import AgencyRepository from './repositories/agency/agency';
import LineRepository from './repositories/line/line';
import ScheduleRepository from './repositories/schedule/schedule';
import StationRepository from './repositories/station/station';

import AppNavbar from './components/app-navbar/app-navbar';
import MapLocator from './components/map-locator/map-locator';
import SearchBox from './components/search-box/search-box';
import ResourceItem from './components/resource-item/resource-item';

export default angular.module('hermes-frontend-v2', [
  // 3-d Party Dependencies
  ngAnimate,
  ngAria,
  ngMaterial,
  ngMessages,
  ngTranslate,
  ngTranslateLoaderStaticFiles,
  ngInfiniteScroll,
  uiRouter,
  ngMap,

  // Components
  AppNavbar.name,
  MapLocator.name,
  SearchBox.name,
  ResourceItem.name,

  // Services
  PopupService.name,

  // Repositories
  AgencyRepository.name,
  LineRepository.name,
  ScheduleRepository.name,
  StationRepository.name,
])
  .constant('ENV', process.env.ENV_NAME)
  .config(appConfigEnv)
  .config(appConfigI18n)
  .config(appConfigRoute)
  .config(appConfigStyle)
  .config(appConfigSwagger)
  .name;
