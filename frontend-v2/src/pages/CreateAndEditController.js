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

class CreateAndEditController {

  constructor($mdBottomSheet, record) {
    this.$mdBottomSheet = $mdBottomSheet;
    this.record = record;
  }

  async saveRecord() {
    try {
      if (!this.record.id) {
        await this.$persistRecord(this.record);
      } else {
        await this.$mergeRecord(this.record.id, this.record);
      }

      this.$mdBottomSheet.hide();
    } catch (err) {
      this.$mdBottomSheet.cancel();
      throw Error('Failed to update/create record', err);
    }
  }

  async $persistRecord(record) {
    throw Error("Abstract method: Implementation required")
  }

  async $mergeRecord(id, record) {
    throw Error("Abstract method: Implementation required")
  }
}

export default CreateAndEditController;
