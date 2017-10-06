import template from './stops-page.template.html';
import './stops-page.style.css';

class StopsPageComponent {

  constructor() {
    this.template = template;
  }

  static get name() {
    return 'stopsPage';
  }
}

export default StopsPageComponent;
