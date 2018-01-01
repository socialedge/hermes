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

import angular from 'angular';

const DEFAULT_FILTER_PROPERTY = "name";
const DEFAULT_RESULT_PER_PAGE = 25;

/**
 * @deprecated in favor of {@code record-management.controller.js}
 */
class RecordManagementController {

  constructor($scope, $timeout, $mdBottomSheet, editController, editTemplate, popupService,
              disablePaging = false,
              resultsPerPage = DEFAULT_RESULT_PER_PAGE) {
    this.$scope = $scope;
    this.$timeout = $timeout;
    this.$mdBottomSheet = $mdBottomSheet;

    this.editController = editController;
    this.editTemplate = editTemplate;

    this.popupService = popupService;
    this.resultsPerPage = resultsPerPage;
    this.disablePaging = disablePaging;

    this.records = [];
    this.nextPage = 0;
    this.isLastPage = false;

    this.showLoading();
  }

  async createRecord(baseRecord = this.$emptyRecord()) {
    try {
      await this.$mdBottomSheet.show({
        template: this.editTemplate,
        controllerAs: '$ctrl',
        controller: this.editController,
        locals: {
          record: baseRecord
        }
      });

      this.popupService.notifyCreated();

      this.records = [baseRecord].concat(this.records);
    } catch(err) {
      // create dialog was canceled
    }
  }

  $emptyRecord() {
    return {};
  }

  async nextRecords(filter) {
    if (this.isLastPage)
      return;

    this.showLoading();

    const nextRecords = await this.loadRecords(this.nextPage, filter);

    if (!this.disablePaging) {
      this.nextPage++;
      this.isLastPage = nextRecords.length < this.resultsPerPage;
    } else {
      this.isLastPage = true;
    }

    this.records = this.records.concat(nextRecords);

    this.hideLoading();
  }

  async searchRecords(filter) {
    this.showLoading();

    this.nextPage = 0;
    this.records = await this.loadRecords(this.nextPage, filter);

    if (!this.disablePaging) {
      this.nextPage++;
      this.isLastPage = !this.records || this.records.length < this.resultsPerPage;
    } else {
      this.isLastPage = true;
    }

    this.hideLoading();
  }

  async editRecord(record) {
    const recordCopy = angular.copy(record);

    try {
      await this.$mdBottomSheet.show({
        template: this.editTemplate,
        controllerAs: '$ctrl',
        controller: this.editController,
        locals: {
          record: recordCopy
        }
      });

      angular.copy(recordCopy, record);
      this.popupService.notifySaved()
    } catch(err) {
      // edit dialog was canceled
    }
  }

  async deleteRecord(id) {
    const deleteConfirmed = await this.popupService.confirmRemoval();

    if (deleteConfirmed) {
      try {
        await this.$deleteRecord(id);

        this.records = this.records.filter(a => {
          return a.id !== id;
        });

        this.popupService.notifyRemoval();
      } catch (err) {
        throw Error(`Failed to delete a records id = ${id}`, err);
      }
    }
  }

  async $deleteRecord(id) {
    throw Error("Abstract method: Implementation required")
  }

  async loadRecords(offset, filter) {
    const params = this.disablePaging ? {} : {"page": offset, 'size': this.resultsPerPage};
    if (filter) {
      if (typeof filter === 'object') {
        params['filter'] = `${filter['property']},${filter['value']}`;
      } else {
        params['filter'] = `${DEFAULT_FILTER_PROPERTY},${filter}`;
      }
    }

    return this.$fetchRecords(params);
  }

  async $fetchRecords(params) {
    throw Error("Abstract method: Implementation required")
  }

  showLoading() {
    this.loading = true;
  }

  hideLoading() {
    this.$timeout(() => {
      this.loading = false;
    }, 1000);
  }
}

export default RecordManagementController;
