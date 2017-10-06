import template from './app-navbar.template.html';
import './app-navbar.style.css';

class AppHeaderComponent {

  constructor() {
    this.controller = AppHeaderComponentController;
    this.template = template;
  }

  static get name() {
    return 'appNavbar';
  }
}

class AppHeaderComponentController {

  constructor($state) {
    this.$state = $state;
  }

  static get $inject() {
    return ['$state'];
  }

}
export default AppHeaderComponent;
