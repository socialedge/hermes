import template from './lines-page.template.html';
import './lines-page.style.css';

class LinesPageComponent {

  constructor() {
    this.template = template;
  }

  static get name() {
    return 'linesPage';
  }
}

export default LinesPageComponent;
