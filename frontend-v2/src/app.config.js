export default function (ENV, $compileProvider, $locationProvider, $translateProvider) {
  'ngInject';

  $locationProvider.html5Mode(true);
  $compileProvider.debugInfoEnabled(ENV !== 'prod' && ENV !== 'production');

  $translateProvider
    .useStaticFilesLoader({
      prefix: '',
      suffix: '.json'
    })
    .registerAvailableLanguageKeys(['en','uk'], {
      'en_*': 'en',
      'uk_*': 'uk',
      '*': 'en'
    })
    .useSanitizeValueStrategy("escape")
    .determinePreferredLanguage()
    .fallbackLanguage("en");
}
