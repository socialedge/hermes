import StopsPageController from './pages/stops-page/stops-page.controller';
import StopsPageTemplate from './pages/stops-page/stops-page.template.html';

import AgenciesPageController from './pages/agencies-page/agencies-page.controller';
import AgenciesPageTemplate from './pages/agencies-page/agencies-page.template.html';

import LinesPageController from './pages/lines-page/lines-page.controller';
import LinesPageTemplate from './pages/lines-page/lines-page.template.html';

import PublicationsPageController from './pages/publications-page/publications-page.controller';
import PublicationsPageTemplate from './pages/publications-page/publications-page.template.html';

import SchedulesPageController from './pages/schedules-page/schedules-page.controller';
import SchedulesPageTemplate from './pages/schedules-page/schedules-page.template.html';

export default function ($stateProvider, $urlRouterProvider) {
  'ngInject';

  $stateProvider
    .state('agencies', {
      url: '/agencies',
      views: {
        "page@": {
          controller: AgenciesPageController,
          controllerAs: '$ctrl',
          template: AgenciesPageTemplate
        }
      }
    })
    .state('stops', {
      url: '/stops',
      views: {
        "page@": {
          controller: StopsPageController,
          controllerAs: '$ctrl',
          template: StopsPageTemplate
        }
      }
    })
    .state('lines', {
      url: '/lines',
      views: {
        "page@": {
          controller: LinesPageController,
          controllerAs: '$ctrl',
          template: LinesPageTemplate
        }
      }
    })
    .state('schedules', {
      url: '/schedules',
      views: {
        "page@": {
          controller: SchedulesPageController,
          controllerAs: '$ctrl',
          template: SchedulesPageTemplate
        }
      }
    })
    .state('publications', {
      url: '/publications',
      views: {
        "page@": {
          controller: PublicationsPageController,
          controllerAs: '$ctrl',
          template: PublicationsPageTemplate
        }
      }
    });

  $urlRouterProvider.otherwise('/agencies');
}
