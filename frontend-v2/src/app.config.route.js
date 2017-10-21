import AppContent from './components/app-content/app-content.component';
import AgenciesPage from './components/agencies-page/agencies-page.component';
import StopsPage from './components/stops-page/stops-page.component';
import LinesPage from './components/lines-page/lines-page.component';
import SchedulesPage from './components/schedules-page/schedules-page.component';
import PublicationsPage from './components/publications-page/publications-page.component';

export default function ($stateProvider, $urlRouterProvider) {
  'ngInject';

  $stateProvider
    .state('app', {
      url: '',
      abstract: true,
      component: AppContent.name
    })
    .state('app.agencies', {
      url: '/agencies',
      views: {
        "page@": {
          component: AgenciesPage.name
        }
      }
    })
    .state('app.stops', {
      url: '/stops',
      views: {
        "page@": {
          component: StopsPage.name
        }
      }
    })
    .state('app.lines', {
      url: '/lines',
      views: {
        "page@": {
          component: LinesPage.name
        }
      }
    })
    .state('app.schedules', {
      url: '/schedules',
      views: {
        "page@": {
          component: SchedulesPage.name
        }
      }
    })
    .state('app.publications', {
      url: '/publications',
      views: {
        "page@": {
          component: PublicationsPage.name
        }
      }
    });

  $urlRouterProvider.otherwise('/agencies');
}
