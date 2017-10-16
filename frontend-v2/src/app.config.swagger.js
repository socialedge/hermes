import Swagger from 'swagger-client';
import appConfig from 'app.config';

var swaggerClient = new Swagger(appConfig.BACKEND_API_SPECS);

export default function ($provide) {
  'ngInject';

  $provide.provider('backend', function () {
    this.$get = () => swaggerClient;
  });
}
