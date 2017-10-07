'use strict';

import angular from 'angular';
import ngAnimate from 'angular-animate';
import ngAria from 'angular-aria';
import ngMaterial from 'angular-material';
import ngMdIcons from 'angular-material-icons';
import ngTranslate from 'angular-translate';
import ngTranslateLoaderStaticFiles from 'angular-translate-loader-static-files';
import uiRouter from '@uirouter/angularjs'

import './styles/main.css';
import 'angular-material/angular-material.css';

import appConfig from './app.config';
import appRoute from './app.route';

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
.config(appConfig)
.config(appRoute)
.constant('ENV', process.env.ENV_NAME)
.name;