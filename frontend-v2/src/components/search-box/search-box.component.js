/*
 * Hermes - The Municipal Transport Timetable System
 * Copyright (c) 2016-2017 SocialEdge
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

import template from './search-box.template.html';

const DEFAULT_TOGGABLE = false;
const DEFAULT_IS_OPEN = true;
const DEFAULT_POSITION = 'right';

class SearchBoxComponent {

  constructor() {
    this.controller = SearchBoxComponentController;
    this.template = template;

    this.bindings = {
      searchBoxToggable: '<',
      searchBoxIsOpen: '<',
      searchBoxPosition: '@',
      searchBoxPlaceholder: '@',
      searchBoxOnChange: '&?',
      searchBoxNgModel: '=?'
    };
  }

  static get name() {
    return 'searchBox';
  }
}

class SearchBoxComponentController {

  $onInit() {
    this.toggable = this.toggable !== undefined ? this.toggable : DEFAULT_TOGGABLE;
    this.isOpen = this.isOpen !== undefined ? this.isOpen : DEFAULT_IS_OPEN;
    this.position = this.position !== undefined ? this.position : DEFAULT_POSITION;
    this.searchBoxOnChange = this.searchBoxOnChange !== undefined ? this.searchBoxOnChange : (t) => {console.log(t)};
  }
}

export default SearchBoxComponent;
