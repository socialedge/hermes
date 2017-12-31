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

import template from './resource-list.template.html';
import ResourceRepository from '../../repositories/resource.repository';
import PopupService from '../../services/popup/popup.service';
import angular from 'angular';

const DEFAULT_PAGEABLE = false;
const DEFAULT_SEARCH_PROPERTY = "name";
const DEFAULT_ITEMS_PER_PAGE = 25;

class ResourceListComponent {

  constructor() {
    this.controller = ResourceListComponentController;
    this.template = template;

    this.bindings = {
      itemTitle: "@",
      itemDescription: "@?",
      itemLabels: "@?",

      itemLabelsClass: "@?",
      itemLabelsTranslate: "<",
      itemLabelsI18nPrefix: "@?",

      listTitle: "@?",
      listRepository: "@",

      listSearchable: "<",
      listSearchProperty: "<?",
      listSearchTerm: "<",

      listPageable: "<",
      listItemsPerPage: "<",

      listReadOnly: "<",
      listOnItemAdd: "&?",
      listOnItemEdit: "&?",
      listOnItemDelete: "&?",

      searchBoxToggable: '<',
      searchBoxIsOpen: '<',
    };
  }

  static get name() {
    return 'resourceList';
  }
}

class ResourceListComponentController {

  constructor($injector, $timeout, $scope, popupService) {
    this.$injector = $injector;
    this.$timeout = $timeout;
    this.$scope = $scope;
    this.popupService = popupService;

    this.records = [];
    this.nextPage = 0;
    this.hasNextPage = true;
    this.listSearchTerm = null;
  }

  async $onInit() {
    this._processingState();

    try {
      this.repository = this.$injector.get(this.listRepository);
     } catch (e) {
      throw new Error("Failed to inject repository for resource-list component: " + e.toString());
    }

    if (!(this.repository instanceof ResourceRepository))
      throw new Error("Repository implementation must extend ResourceRepository");

    this.listSearchProperty = this.listSearchProperty !== undefined ? this.listSearchProperty : DEFAULT_SEARCH_PROPERTY;
    this.listPageable = this.listPageable !== undefined ? this.listPageable : DEFAULT_PAGEABLE;
    this.listItemsPerPage = this.listItemsPerPage !== undefined ? this.listItemsPerPage : DEFAULT_ITEMS_PER_PAGE;

    if (!this.listReadOnly && (!this.listOnItemAdd || !this.listOnItemEdit || !this.listOnItemDelete))
      throw new Error("Editable list must have listOnItemAdd, listOnItemEdit, listOnItemDelete callbacks declared");

    this.records = await this._fetchRecords();

    this._idleState();

    this.$scope.$apply(); // await records arrived
  }

  async loadNextRecords(searchTerm = null) {
    this._processingState();

    const nextRecords = await this._fetchRecords(searchTerm, ++this.nextPage);
    this.records = this.records.concat(nextRecords);

    if (this.listPageable) {
      this.hasNextPage = !(nextRecords.length < this.listItemsPerPage);
    }

    this._idleState();

    this.$scope.$apply(); // await records arrived
  }

  async applyFilter(searchTerm) {
    this._processingState();

    this.records = await this._fetchRecords(searchTerm);

    this.nextPage = 0; // reset paging
    if (this.listPageable) {
      this.hasNextPage = !(this.records.length < this.listItemsPerPage);
    }

    this._idleState();

    this.$scope.$apply(); // await records arrived
  }

  async addResourceItem() {
    const newRecord = await this.listOnItemAdd();

    if (newRecord !== null) {
      this.records = [newRecord].concat(this.records);

      this.$scope.$apply();
      this.popupService.notifyCreated();
    }
  }

  async removeResourceItem(itemId) {
    const removed = await this.listOnItemDelete({itemId: itemId});

    if (removed === true) {
      this.records = this.records.filter(item => {
        return item.id !== itemId;
      });

      this.$scope.$apply();
      this.popupService.notifyRemoval();
    }
  }

  async editResourceItem(item) {
    const editedItem = await this.listOnItemEdit({item: angular.copy(item)});

    if (editedItem !== null) {
      const targetRecordIndex = this.records.findIndex(record => record.id === item.id);
      this.records[targetRecordIndex] = editedItem;

      this.$scope.$apply();
      this.popupService.notifySaved();
    }
  }

  async _fetchRecords(searchTerm = null, pageIndex = 0) {
    const page = this.listPageable ? pageIndex : null;
    const search = searchTerm ? {property: this.listSearchProperty, value: searchTerm} : null;

    return this.repository.find(page, search, this.listItemsPerPage);
  }

  isLastPage() {
    if (!this.listPageable)
      return true;

    return !this.hasNextPage;
  }

  _processingState() {
    this.loading = true
  }

  isProcessing() {
    return this.loading;
  }

  _idleState() {
    this.loading = false;
  }

  isIdle() {
    return !this.loading;
  }

  static get $inject() {
    return ['$injector', '$timeout', '$scope', PopupService.name];
  }
}

export default ResourceListComponent;
