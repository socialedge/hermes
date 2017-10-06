import template from './publications-page.template.html';
import './publications-page.style.css';

class PublicationsPageComponent {

  constructor() {
    this.template = template;
  }

  static get name() {
    return 'publicationsPage';
  }
}

export default PublicationsPageComponent;
