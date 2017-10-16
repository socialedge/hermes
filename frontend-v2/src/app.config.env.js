export default function (ENV, $compileProvider, $locationProvider) {
  'ngInject';

  $locationProvider.html5Mode(true);
  $compileProvider.debugInfoEnabled(ENV !== 'prod' && ENV !== 'production');
}
