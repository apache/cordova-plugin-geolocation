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

geolocation.watchPosition
=========================

Watches for changes to the device's current position.

    var watchId = navigator.geolocation.watchPosition(geolocationSuccess,
                                                      [geolocationError],
                                                      [geolocationOptions]);

Parameters
----------

- __geolocationSuccess__: The callback that is called with the current position.
- __geolocationError__: (Optional) The callback that is called if there was an error.
- __geolocationOptions__: (Optional) The geolocation options.

Returns
-------

- __String__: returns a watch id that references the watch position interval. The watch id should be used with `geolocation.clearWatch` to stop watching for changes in position.

Description
-----------

`geolocation.watchPosition` is an asynchronous function. It returns the device's current position when a change in position has been detected.  When the device has retrieved a new location, the `geolocationSuccess` callback is invoked with a `Position` object as the parameter.  If there is an error, the `geolocationError` callback is invoked with a `PositionError` object.

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
    //   This method accepts a `Position` object, which contains
    //   the current GPS coordinates
    //
    function onSuccess(position) {
        var element = document.getElementById('geolocation');
        element.innerHTML = 'Latitude: '  + position.coords.latitude      + '<br />' +
                            'Longitude: ' + position.coords.longitude     + '<br />' +
                            '<hr />'      + element.innerHTML;
    }

    // onError Callback receives a PositionError object
    //
    function onError(error) {
        alert('code: '    + error.code    + '\n' +
              'message: ' + error.message + '\n');
    }

    // Options: throw an error if no update is received every 30 seconds.
    //
    var watchID = navigator.geolocation.watchPosition(onSuccess, onError, { timeout: 30000 });
    

Full Example
------------

    <!DOCTYPE html>
    <html>
      <head>
        <title>Device Properties Example</title>

        <script type="text/javascript" charset="utf-8" src="cordova-2.6.0.js"></script>
        <script type="text/javascript" charset="utf-8">

        // Wait for Cordova to load
        //
        document.addEventListener("deviceready", onDeviceReady, false);

        var watchID = null;

        // Cordova is ready
        //
        function onDeviceReady() {
            // Throw an error if no update is received every 30 seconds
            var options = { timeout: 30000 };
            watchID = navigator.geolocation.watchPosition(onSuccess, onError, options);
        }
    
        // onSuccess Geolocation
        //
        function onSuccess(position) {
            var element = document.getElementById('geolocation');
            element.innerHTML = 'Latitude: '  + position.coords.latitude      + '<br />' +
                                'Longitude: ' + position.coords.longitude     + '<br />' +
                                '<hr />'      + element.innerHTML;
        }
    
	    // onError Callback receives a PositionError object
	    //
	    function onError(error) {
	        alert('code: '    + error.code    + '\n' +
	              'message: ' + error.message + '\n');
	    }

        </script>
      </head>
      <body>
        <p id="geolocation">Watching geolocation...</p>
      </body>
    </html>
