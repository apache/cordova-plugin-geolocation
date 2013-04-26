---
license: Licensed to the Apache Software Foundation (ASF) under one
         or more contributor license agreements.  See the NOTICE file
         distributed with this work for additional information
         regarding copyright ownership.  The ASF licenses this file
         to you under the Apache License, Version 2.0 (the
         "License"); you may not use this file except in compliance
         with the License.  You may obtain a copy of the License at

           http://www.apache.org/licenses/LICENSE-2.0

         Unless required by applicable law or agreed to in writing,
         software distributed under the License is distributed on an
         "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
         KIND, either express or implied.  See the License for the
         specific language governing permissions and limitations
         under the License.
---

Coordinates
===========

A set of properties that describe the geographic coordinates of a position.

Properties
----------

* __latitude__: Latitude in decimal degrees. _(Number)_
* __longitude__: Longitude in decimal degrees. _(Number)_
* __altitude__: Height of the position in meters above the ellipsoid. _(Number)_
* __accuracy__: Accuracy level of the latitude and longitude coordinates in meters. _(Number)_
* __altitudeAccuracy__: Accuracy level of the altitude coordinate in meters. _(Number)_
* __heading__: Direction of travel, specified in degrees counting clockwise relative to the true north. _(Number)_
* __speed__: Current ground speed of the device, specified in meters per second. _(Number)_

Description
-----------

The `Coordinates` object is created and populated by Cordova, and attached to the `Position` object. The `Position` object is then returned to the user through a callback function.

Supported Platforms
-------------------

- Android
- BlackBerry WebWorks (OS 5.0 and higher)
- iOS
- Windows Phone 7 and 8
- Bada 1.2 & 2.x
- webOS
- Tizen
- Windows 8

Quick Example
-------------

    // onSuccess Callback
    //
    var onSuccess = function(position) {
        alert('Latitude: '          + position.coords.latitude          + '\n' +
              'Longitude: '         + position.coords.longitude         + '\n' +
              'Altitude: '          + position.coords.altitude          + '\n' +
              'Accuracy: '          + position.coords.accuracy          + '\n' +
              'Altitude Accuracy: ' + position.coords.altitudeAccuracy  + '\n' +
              'Heading: '           + position.coords.heading           + '\n' +
              'Speed: '             + position.coords.speed             + '\n' +
              'Timestamp: '         + position.timestamp                + '\n');
    };

    // onError Callback
    //
    var onError = function() {
        alert('onError!');
    };

    navigator.geolocation.getCurrentPosition(onSuccess, onError);

Full Example
------------

    <!DOCTYPE html>
    <html>
      <head>
        <title>Geolocation Position Example</title>
        <script type="text/javascript" charset="utf-8" src="cordova-2.6.0.js"></script>
        <script type="text/javascript" charset="utf-8">

        // Set an event to wait for Cordova to load
        //
        document.addEventListener("deviceready", onDeviceReady, false);

        // Cordova is loaded and Ready
        //
        function onDeviceReady() {
            navigator.geolocation.getCurrentPosition(onSuccess, onError);
        }
    
        // Display `Position` properties from the geolocation
        //
        function onSuccess(position) {
            var div = document.getElementById('myDiv');
        
            div.innerHTML = 'Latitude: '             + position.coords.latitude  + '<br/>' +
                            'Longitude: '            + position.coords.longitude + '<br/>' +
                            'Altitude: '             + position.coords.altitude  + '<br/>' +
                            'Accuracy: '             + position.coords.accuracy  + '<br/>' +
                            'Altitude Accuracy: '    + position.coords.altitudeAccuracy  + '<br/>' +
                            'Heading: '              + position.coords.heading   + '<br/>' +
                            'Speed: '                + position.coords.speed     + '<br/>';
        }
    
        // Show an alert if there is a problem getting the geolocation
        //
        function onError() {
            alert('onError!');
        }

        </script>
      </head>
      <body>
        <div id="myDiv"></div>
      </body>
    </html>
    
Android Quirks
-------------

__altitudeAccuracy:__ This property is not support by Android devices, it will always return null.
