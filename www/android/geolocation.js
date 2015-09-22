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


var exec = cordova.require('cordova/exec');

module.exports = {

    /*TODO: Fix scope issues with this cordova.require.  I have no idea exec works, but geo doesn't work */

    getCurrentPosition: function(success, error, args) {
        var win = function() {
          var geo = cordova.require('cordova/modulemapper').getOriginalSymbol(window, 'navigator.geolocation');
          geo.getCurrentPosition(success, error, {
            enableHighAccuracy: args[0],
            maximumAge: args[1]
          });
        };
        exec(win, error, "Geolocation", "getPermission", []);
    },

    watchPosition: function(success, error, args) {
        var win = function() {
            var geo = cordova.require('cordova/modulemapper').getOriginalSymbol(window, 'navigator.geolocation');
            geo.watchPosition(success, error, {
                enableHighAccuracy: args[1]
            });
        };
        exec(win, error, "Geolocation", "getPermission", []);
    },

    clearWatch: function(success, error, args) {
        var win = function() {
          var geo = cordova.require('cordova/modulemapper').getOriginalSymbol(window, 'navigator.geolocation');
          geo.clearWatch(args[0]);
        }
        exec(win, error, "Geolocation", "getPermission", []);
    }
};

