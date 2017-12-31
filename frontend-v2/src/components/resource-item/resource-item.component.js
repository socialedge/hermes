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

import template from './resource-item.template.html';

const DEFAULT_TRANSLATE = false;

class ResourceItemComponent {

  constructor() {
    this.controller = ResourceItemComponentController;
    this.template = template;

    this.bindings = {
      item: "=",
      itemTitle: "@",
      itemDescription: "@?",
      itemLabels: "@?",
      itemOnEdit: "&?",
      itemOnDelete: "&?",
      itemLabelsClass: "@?",
      itemLabelsTranslate: "<",
      itemLabelsI18nPrefix: "@?",
    };
  }

  static get name() {
    return 'resourceItem';
  }
}

class ResourceItemComponentController {

  constructor($translate) {
    this.$translate = $translate;
  }

  async $onInit() {
    if (!this.item)
      throw new Error("Item object is not specified in resource-item component");

    this.itemLabelsTranslate = this.itemLabelsTranslate !== undefined ? this.itemLabelsTranslate : DEFAULT_TRANSLATE;
    this.itemLabelsI18nPrefix = this.itemLabelsI18nPrefix !== undefined ? this.itemLabelsI18nPrefix : "";

    if (!this.itemLabelsI18nPrefix.endsWith("."))
      this.itemLabelsI18nPrefix += ".";

    if (this.itemLabelsTranslate) {
      const labelI18nKeys = this.item[this.itemLabels].map((label) => this.itemLabelsI18nPrefix + label);
      this.itemI18nLabels = await this.$translate(labelI18nKeys);
    }
  }

  static get $inject() {
    return ['$translate'];
  }
}

export default ResourceItemComponent;
