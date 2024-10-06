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
const Position = require('./Position');
const PositionError = require('./PositionError');

// Native watchPosition method is called async after permissions prompt.
// So we use additional map and own ids to return watch id synchronously.
const pluginToNativeWatchMap = {};
// Returns default params, overrides if provided with values
function parseParameters (options) {
    const opt = {
        maximumAge: 0,
        enableHighAccuracy: false,
        timeout: Infinity
    };

    if (options) {
        if (options.maximumAge !== undefined && !isNaN(options.maximumAge) && options.maximumAge > 0) {
            opt.maximumAge = options.maximumAge;
        }
        if (options.enableHighAccuracy !== undefined) {
            opt.enableHighAccuracy = options.enableHighAccuracy;
        }
        if (options.timeout !== undefined && !isNaN(options.timeout)) {
            if (options.timeout < 0) {
                opt.timeout = 0;
            } else {
                opt.timeout = options.timeout;
            }
        }
    }

    return opt;
}

module.exports = {
    getCurrentPosition: function (success, error, args) {
        const win = function (p) {
            // Workaround for bug specific to API 31 where requesting `enableHighAccuracy: false` results in TIMEOUT error.
            if (p.deviceApiLevel === 31) {
                if (typeof args === 'undefined') args = {};
                args.enableHighAccuracy = true;
            }
            const pos = new Position(
                {
                    latitude: p.latitude,
                    longitude: p.longitude,
                    altitude: p.altitude,
                    accuracy: p.accuracy,
                    heading: p.heading,
                    velocity: p.velocity,
                    altitudeAccuracy: p.altitudeAccuracy
                },
                p.timestamp
            );
            success(pos);
        };
        const fail = function () {
            if (error) {
                error(new PositionError(PositionError.PERMISSION_DENIED, 'Illegal Access'));
            }
        };
        const options = parseParameters(args);
        exec(win, fail, 'Geolocation', 'getCurrentPosition', [options.enableHighAccuracy, options.maximumAge, options.timeout]);
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
