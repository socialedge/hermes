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

export default class PopupService {

  constructor($mdDialog, $mdToast, $translate) {
    this.$mdDialog = $mdDialog;
    this.$mdToast = $mdToast;
    this.$translate = $translate;
  }

  async confirmRemoval() {
    const removeDialogLocale = await this.$translate([
      'shared.dialog.remove.title',
      'shared.dialog.remove.body',
      'shared.dialog.remove.button.confirm',
      'shared.dialog.remove.button.cancel']);

    const removeDialog = this.$mdDialog.confirm()
      .title(removeDialogLocale['shared.dialog.remove.title'])
      .textContent(removeDialogLocale['shared.dialog.remove.body'])
      .ok(removeDialogLocale['shared.dialog.remove.button.confirm'])
      .cancel(removeDialogLocale['shared.dialog.remove.button.cancel']);

    return await this.$mdDialog.show(removeDialog);
  }

  async notifyRemoval(title) {
    const toastRemovedLocale = await this.$translate('shared.toast.remove', {name: title});
    return this.showToast(toastRemovedLocale);
  }

  async notifyCreated() {
    const toastSavedLocale = await this.$translate('shared.toast.created');
    return this.showToast(toastSavedLocale);
  }

  async notifySaved(title) {
    const toastSavedLocale = await this.$translate('shared.toast.saved', {name: title});
    return this.showToast(toastSavedLocale);
  }

  async showToast(text) {
    return this.$mdToast.show(
      this.$mdToast.simple()
        .textContent(text)
        .position('top right')
        .hideDelay(2500)
    );
  }

  static get $inject() {
    return ['$mdDialog', '$mdToast', '$translate'];
  }

  static get name() {
    return 'popupService';
  }
}
