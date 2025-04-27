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

// list of timers in use
const timers = {};

// Returns a timeout failure, closed over a specified timeout value and error callback.
function createTimeout (errorCallback, timeout, id) {
    timers[id].timer = setTimeout(function () {
        clearTimeout(timers[id].timer);
        timers[id].timer = null;
        errorCallback({
            code: PositionError.TIMEOUT,
            message: 'Position retrieval timed out.'
        });
    }, timeout);
    return timers[id].timer;
}

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
        args = parseParameters(args);

        const id = utils.createUUID();

        // Timer var that will fire an error callback if no position is retrieved from native
        // before the "timeout" param provided expires
        timers[id] = { timer: null };

        const win = function (deviceApiLevel) {
            if (!timers[id].timer) {
                // Timeout already happened, or native fired error callback for
                // this geo request.
                // Don't continue with success callback.
                return;
            }
            // Workaround for bug specific to API 31 where requesting `enableHighAccuracy: false` results in TIMEOUT error.
            if (deviceApiLevel === 31) {
                if (typeof args === 'undefined') args = {};
                args.enableHighAccuracy = true;
            }
            const geo = cordova.require('cordova/modulemapper').getOriginalSymbol(window, 'navigator.geolocation'); // eslint-disable-line no-undef
            geo.getCurrentPosition((position) => {
                clearTimeout(timers[id].timer);
                if (!timers[id].timer) {
                    // Timeout already happened, or native fired error callback for
                    // this geo request.
                    // Don't continue with success callback.
                    return;
                }
                success(position);
            }, error, args);
        };
        const fail = function () {
            clearTimeout(timers[id].timer);
            timers[id].timer = null;
            if (error) {
                error(new PositionError(PositionError.PERMISSION_DENIED, 'Illegal Access'));
            }
        };

        if (args.timeout !== Infinity) {
            // If the timeout value was not set to Infinity (default), then
            // set up a timeout function that will fire the error callback
            // if no successful position was retrieved before timeout expired.
            timers[id].timer = createTimeout(error, args.timeout, id);
        } else {
            // This is here so the check in the win function doesn't mess stuff up
            // may seem weird but this guarantees timeoutTimer is
            // always truthy before we call into native
            timers[id].timer = true;
        }
        const enableHighAccuracy = typeof args === 'object' && !!args.enableHighAccuracy;
        exec(win, fail, 'Geolocation', 'getPermission', [enableHighAccuracy]);
        return timers[id];
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
