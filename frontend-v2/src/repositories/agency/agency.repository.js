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

'use strict';

import ResourceRepository from '../resource.repository';

export default class AgencyRepository extends ResourceRepository {

  constructor(backend) {
    super();
    this.backend = backend;
  }

  async _persist(resource) {
    return (await (await this.backend).apis.agencies.createAgency({body: record})).body;
  }

  async _merge(id, resource) {
    return (await (await this.backend).apis.agencies.replaceAgency({id: id, body: record})).body;
  }

  async _delete(id) {
    return (await this.backend).apis.agencies.deleteAgency({id: id});
  }

  async _list(params) {
    return (await (await this.backend).apis.agencies.listAgencies(params)).body;
  }

  async _get(id) {
    return (await (await this.backend).apis.agencies.getAgency({id: id})).body;
  }

  static get $inject() {
    return ['backend'];
  }

  static get name() {
    return 'agencyRepository';
  }
}
