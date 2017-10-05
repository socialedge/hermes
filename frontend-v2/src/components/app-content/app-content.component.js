import template from './app-content.template.html';
import './app-content.style.css';

class AppContentComponent {

  constructor() {
    this.template = template;
  }

  static get name() {
    return 'appContent';
  }
}

export default AppContentComponent;
