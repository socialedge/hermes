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

const DEFAULT_RESULT_PER_PAGE = 25;

export default class ResourceRepository {

  async findOne(resourceId) {
    return this._get(resourceId);
  }

  async find(page = null, search = null, size = DEFAULT_RESULT_PER_PAGE) {
    const reqParams = {};

    if (page !== null) {
      if (page < 0) {
        throw new Error("Page must be >= 0");
      } else if (!size || size <= 0) {
        throw new Error("If page is specified, the size param must be > 0");
      }

      reqParams['page'] = page;
      reqParams['size'] = size;
    }

    if (search !== null) {
      if (typeof search === 'object') {
        reqParams['filter'] = `${search['property']},${search['value']}`;
      } else {
        throw new Error("Search filter must have {property: '${name}', value: '${value}'} form");
      }
    }

    return this._list(reqParams);
  }

  async remove(resourceOrId) {
    const resourceId = resourceOrId === Object(resourceOrId) ? resourceOrId.id : resourceOrId;

    return this._delete(resourceId);
  }

  async save(resource) {
    if (resource.id) {
      const resourceCopy = Object.assign({}, resource);
      delete resourceCopy.id;

      return this._merge(resource.id, resourceCopy);
    } else {
      return this._persist(resource);
    }
  }

  async _persist(resource) {
    throw new Error("Abstract method: Implementation required")
  }

  async _merge(id, resource) {
    throw new Error("Abstract method: Implementation required")
  }

  async _delete(id) {
    throw new Error("Abstract method: Implementation required")
  }

  async _list(params) {
    throw new Error("Abstract method: Implementation required")
  }

  async _get(id) {
    throw new Error("Abstract method: Implementation required")
  }
}

