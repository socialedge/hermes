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

import CreateAndEditController from '../../CreateAndEditController';

class AgencyEditController extends CreateAndEditController {

  constructor($mdBottomSheet, backend, record) {
    super($mdBottomSheet, record);
    this.backend = backend;
  }

  async $persistRecord(record) {
    return (await (await this.backend).apis.agencies.createAgency({body: record})).body;
  }

  async $mergeRecord(id, record) {
    return (await (await this.backend).apis.agencies.replaceAgency({id: id, body: record})).body;
  }

  static get $inject() {
    return ['$mdBottomSheet', 'backend', 'record'];
  }

}
export default AgencyEditController;
