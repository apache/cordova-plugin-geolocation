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

var modulemapper = require('cordova/modulemapper'),
    exec = require('cordova/exec'),
    PositionError = require('./PositionError'),
    originalGeolocation = modulemapper.getOriginalSymbol(window, 'navigator.geolocation');

var checkGeolocationAvailability = function(win) {
    var fail = function (err) {
        console.log('GeolocationAvailabilityChecker.check failed: ' + err);
    };

    exec(win, fail, 'GeolocationAvailabilityChecker', 'check', []);
};

var failWhenGeolocationNotAvailable = function (errorCallback) {
    checkGeolocationAvailability(function(isAvailable) {
        if (!isAvailable) {
            errorCallback(new PositionError(PositionError.PERMISSION_DENIED));
        }
    });
};

var geolocation = {
    getCurrentPosition: function(successCallback, errorCallback, options) {
        failWhenGeolocationNotAvailable(errorCallback);
        return originalGeolocation.getCurrentPosition(successCallback, errorCallback, options);
    },

    watchPosition:function(successCallback, errorCallback, options) {
        failWhenGeolocationNotAvailable(errorCallback);
        return originalGeolocation.watchPosition(successCallback, errorCallback, options);
    },

    clearWatch:function() {
        return originalGeolocation.clearWatch.apply(originalGeolocation, arguments);
    }
};

module.exports = geolocation;
