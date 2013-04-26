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

geolocation.clearWatch
======================

Stop watching for changes to the device's location referenced by the `watchID` parameter.

    navigator.geolocation.clearWatch(watchID);

Parameters
----------

- __watchID:__ The id of the `watchPosition` interval to clear. (String)

Description
-----------

`geolocation.clearWatch` stops watching changes to the device's location by clearing the `geolocation.watchPosition` referenced by `watchID`.

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

    // Options: watch for changes in position, and use the most
    // accurate position acquisition method available.
    //
    var watchID = navigator.geolocation.watchPosition(onSuccess, onError, { enableHighAccuracy: true });

    // ...later on...

    navigator.geolocation.clearWatch(watchID);


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
            // Get the most accurate position updates available on the
            // device.
            var options = { enableHighAccuracy: true };
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

        // clear the watch that was started earlier
        // 
        function clearWatch() {
            if (watchID != null) {
                navigator.geolocation.clearWatch(watchID);
                watchID = null;
            }
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
    	<button onclick="clearWatch();">Clear Watch</button>     
      </body>
    </html>
