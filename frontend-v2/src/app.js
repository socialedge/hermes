'use strict';

import angular from 'angular';
import ngAnimate from 'angular-animate';
import ngAria from 'angular-aria';
import ngMaterial from 'angular-material';
import ngMdIcons from 'angular-material-icons';
import ngTranslate from 'angular-translate';
import ngTranslateLoaderStaticFiles from 'angular-translate-loader-static-files';
import uiRouter from '@uirouter/angularjs';

import './styles/main.css';
import 'angular-material/angular-material.css';

import appConfigEnv from './app.config.env';
import appConfigI18n from './app.config.i18n';
import appConfigRoute from './app.config.route';
import appConfigStyle from './app.config.style';
import appConfigSwagger from './app.config.swagger';

import AppContent from './components/app-content/app-content';

export default angular.module('hermes-frontend-v2', [
  ngAnimate,
  ngAria,
  ngMaterial,
  ngMdIcons,
  ngTranslate,
  ngTranslateLoaderStaticFiles,
  uiRouter,

  AppContent.name
])
  .constant('ENV', process.env.ENV_NAME)
  .config(appConfigEnv)
  .config(appConfigI18n)
  .config(appConfigRoute)
  .config(appConfigStyle)
  .config(appConfigSwagger)
  .name;
