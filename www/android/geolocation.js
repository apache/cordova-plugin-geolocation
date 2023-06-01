/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

const exec = cordova.require('cordova/exec'); // eslint-disable-line no-undef
const utils = require('cordova/utils');
const PositionError = require('./PositionError');

// Native watchPosition method is called async after permissions prompt.
// So we use additional map and own ids to return watch id synchronously.
const pluginToNativeWatchMap = {};

module.exports = {
    getCurrentPosition: function (success, error, args) {
        const win = function (deviceApiLevel) {
            // Workaround for bug specific to API 31 where requesting `enableHighAccuracy: false` results in TIMEOUT error.
            if (deviceApiLevel === 31) {
                if (typeof args === 'undefined') args = {};
                args.enableHighAccuracy = true;
            }
            const geo = cordova.require('cordova/modulemapper').getOriginalSymbol(window, 'navigator.geolocation'); // eslint-disable-line no-undef
            geo.getCurrentPosition(success, error, args);
        };
        const fail = function () {
            if (error) {
                error(new PositionError(PositionError.PERMISSION_DENIED, 'Illegal Access'));
            }
        };
        const enableHighAccuracy = typeof args === 'object' && !!args.enableHighAccuracy;
        exec(win, fail, 'Geolocation', 'getPermission', [enableHighAccuracy]);
    },

    watchPosition: function (success, error, args) {
        const pluginWatchId = utils.createUUID();

        const win = function (deviceApiLevel) {
            // Workaround for bug specific to API 31 where requesting `enableHighAccuracy: false` results in TIMEOUT error.
            if (deviceApiLevel === 31) {
                if (typeof args === 'undefined') args = {};
                args.enableHighAccuracy = true;
            }
            const geo = cordova.require('cordova/modulemapper').getOriginalSymbol(window, 'navigator.geolocation'); // eslint-disable-line no-undef
            pluginToNativeWatchMap[pluginWatchId] = geo.watchPosition(success, error, args);
        };

        const fail = function () {
            if (error) {
                error(new PositionError(PositionError.PERMISSION_DENIED, 'Illegal Access'));
            }
        };
        const enableHighAccuracy = typeof args === 'object' && !!args.enableHighAccuracy;
        exec(win, fail, 'Geolocation', 'getPermission', [enableHighAccuracy]);

        return pluginWatchId;
    },

    clearWatch: function (pluginWatchId) {
        const nativeWatchId = pluginToNativeWatchMap[pluginWatchId];
        const geo = cordova.require('cordova/modulemapper').getOriginalSymbol(window, 'navigator.geolocation'); // eslint-disable-line no-undef
        geo.clearWatch(nativeWatchId);
    }
};
