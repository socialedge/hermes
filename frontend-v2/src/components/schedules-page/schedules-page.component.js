import template from './schedules-page.template.html';
import './schedules-page.style.css';

class SchedulesPageComponent {

  constructor() {
    this.template = template;
  }

  static get name() {
    return 'schedulesPage';
  }
}

export default SchedulesPageComponent;
