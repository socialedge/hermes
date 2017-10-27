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

class RecordManagementController {

  constructor($scope, $timeout, $mdBottomSheet, editController, editTemplate, popupService, resultsPerPage = DEFAULT_RESULT_PER_PAGE) {
    this.$scope = $scope;
    this.$timeout = $timeout;
    this.$mdBottomSheet = $mdBottomSheet;

    this.editController = editController;
    this.editTemplate = editTemplate;

    this.popupService = popupService;
    this.resultsPerPage = resultsPerPage;

    this.records = [];
    this.nextPage = 0;
    this.isLastPage = false;

    this.showLoading();
  }

  async createRecord() {
    const newRecord = this.$emptyRecord();

    try {
      await this.$mdBottomSheet.show({
        template: this.editTemplate,
        controllerAs: '$ctrl',
        controller: this.editController,
        locals: {
          record: newRecord
        }
      });

      this.popupService.notifyCreated();

      this.records.unshift(newRecord);
      this.$scope.$apply();
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

    const nextRecords = await this.loadRecords(this.nextPage, name);

    this.nextPage++;
    this.isLastPage = nextRecords.length < this.resultsPerPage;

    this.records = this.records.concat(nextRecords);
    this.$scope.$apply();

    this.hideLoading();
  }

  async searchRecords(filter) {
    this.showLoading();

    this.nextPage = 0;
    this.records = await this.loadRecords(this.nextPage, filter);

    this.nextPage++;
    this.isLastPage = !this.records || this.records.length < this.resultsPerPage;
    this.$scope.$apply();

    this.hideLoading();
  }

  async editRecord(record, description) {
    const oldRecord = angular.copy(record);

    try {
      await this.$mdBottomSheet.show({
        template: this.editTemplate,
        controllerAs: '$ctrl',
        controller: this.editController,
        locals: {
          record: record
        }
      });

      this.popupService.notifySaved(description)
    } catch(err) {
      angular.copy(oldRecord, record);
      this.$scope.$apply();
    }
  }

  async deleteRecord(id, description) {
    const deleteConfirmed = await this.popupService.confirmRemoval();

    if (deleteConfirmed) {
      try {
        await this.$deleteRecord(id);

        this.records = this.records.filter(a => {
          return a.id !== id;
        });
        this.$scope.$apply();

        this.popupService.notifyRemoval(description);
      } catch (err) {
        throw Error(`Failed to delete a records id = ${id}, description = ${description}`, err);
      }
    }
  }

  async $deleteRecord(id) {
    throw Error("Abstract method: Implementation required")
  }

  async loadRecords(offset, filter) {
    const params = {"page": offset, 'size': this.resultsPerPage};
    if (filter) {
      if (typeof filter === 'object') {
        params['filter'] = `${filter['property']},${filter['value']}`;
      } else {
        params['filter'] = `${DEFAULT_FILTER_PROPERTY},${filter}`;
      }
    }

    return this.$loadRecords(params);
  }

  async $loadRecords(params) {
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
