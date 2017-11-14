import template from './app-navbar.template.html';
import './app-navbar.style.css';

class AppHeaderComponent {

  constructor() {
    this.template = template;
  }

  static get name() {
    return 'appNavbar';
  }
}

export default AppHeaderComponent;
