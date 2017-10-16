export default function ($translateProvider) {
  'ngInject';

  $translateProvider
    .useStaticFilesLoader({
      prefix: '',
      suffix: '.json'
    })
    .registerAvailableLanguageKeys(['en', 'uk'], {
      'en_*': 'en',
      'uk_*': 'uk',
      '*': 'en'
    })
    .useSanitizeValueStrategy('escape')
    .determinePreferredLanguage()
    .fallbackLanguage('en');
}
