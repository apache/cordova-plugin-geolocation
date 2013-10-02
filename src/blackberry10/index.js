/*
 * Copyright 2013 Research In Motion Limited.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
var ids = {},
    clientIds = [];

module.exports = {
    getLocation: function (success, fail, args, env) {
        var result = new PluginResult(args, env),
            options = {
                "enableHighAccuracy": decodeURIComponent(args[0]),
                "maximumAge": decodeURIComponent(args[1])
            };

        navigator.geolocation.getCurrentPosition(function (pos) {
            result.callbackOk(pos.coords, false);
        }, function (err) {
            result.error(err, false);
        }, options);

        result.noResult(true);
    },

    addWatch: function (success, fail, args, env) {
        var result = new PluginResult(args, env),
            options = {
                "enableHighAccuracy": decodeURIComponent(args[1])
            },
            clientId = decodeURIComponent(args[0]);
            id = navigator.geolocation.watchPosition(function (pos) {
                    result.callbackOk(pos.coords, false);
                }, function (err) {
                    result.error(err, false);
                }, options);

        ids[clientId] = id;
        clientIds.push(clientId);

        result.noResult(true);
    },

    clearWatch: function (success, fail, args, env) {
        var result = new PluginResult(args, env),
            clientId = decodeURIComponent(args[0]),
            id = ids[clientId];

        if (id) {
            navigator.geolocation.clearWatch(id);
            delete ids[clientId];
        }

        result.ok(null, false);
    },

    reset: function () {
        clientIds.forEach(function (clientId) {
            if (ids.hasOwnProperty(clientId)) {
                navigator.geolocation.clearWatch(ids[clientId]);
            }
        });
        clientIds = [];
        ids = {};
    }
};
