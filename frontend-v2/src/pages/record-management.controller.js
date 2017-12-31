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

export default class RecordManagementController {

  constructor(repository, $mdBottomSheet, editController, editTemplate) {
    this.repository = repository;
    this.$mdBottomSheet = $mdBottomSheet;
    this.editController = editController;
    this.editTemplate = editTemplate;
  }

  repository() {
    return this.repository;
  }

  async createRecord() {
    try {
      let newRecord = {};

      await this.$mdBottomSheet.show({
        template: this.editTemplate,
        controllerAs: '$ctrl',
        controller: this.editController,
        locals: {
          record: newRecord
        }
      });

      return newRecord;
    } catch (err) { // $mdBottomSheet throws error on closing windows without submitting data
      return null;
    }
  }

  async editRecord(item) {
    try {
      await this.$mdBottomSheet.show({
        template: this.editTemplate,
        controllerAs: '$ctrl',
        controller: this.editController,
        locals: {
          record: item
        }
      });

      return item;
    } catch (err) { // $mdBottomSheet throws error on closing windows without submitting data
      return null;
    }
  }

  async removeRecord(itemId) {
    try {
      await this.repository.remove(itemId);
      return true;
    } catch (e) {
      throw new Error('Failed to remove agency record: ' + e.toString());
    }
  }
}
