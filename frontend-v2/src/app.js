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

import './styles/main.css';
import 'angular-material/angular-material.css';

import appConfigEnv from './app.config.env';
import appConfigI18n from './app.config.i18n';
import appConfigRoute from './app.config.route';
import appConfigStyle from './app.config.style';
import appConfigSwagger from './app.config.swagger';
import PopupService from './services/popup/popup'

import AppNavbar from './components/app-navbar/app-navbar';

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

  // Components
  AppNavbar.name,

  PopupService.name
])
  .constant('ENV', process.env.ENV_NAME)
  .config(appConfigEnv)
  .config(appConfigI18n)
  .config(appConfigRoute)
  .config(appConfigStyle)
  .config(appConfigSwagger)
  .name;
