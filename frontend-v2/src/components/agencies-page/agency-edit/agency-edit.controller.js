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

class AgencyEditController {

  constructor($mdBottomSheet, backend, record) {
    this.$mdBottomSheet = $mdBottomSheet;
    this.backend = backend;
    this.agency = record;
  }

  async saveAgency(agency) {
    const client = await this.backend;

    try {
      if (this.agency.id === null) {
        await client.apis.agencies.createAgency({body: agency});
      } else {
        await client.apis.agencies.replaceAgency({id: agency.id, body: agency});
      }

      this.$mdBottomSheet.hide();
    } catch (err) {
      this.$mdBottomSheet.cancel();
      throw Error('Failed to update agency', err);
    }
  }

  static get $inject() {
    return ['$mdBottomSheet', 'backend', 'record'];
  }

}
export default AgencyEditController;
