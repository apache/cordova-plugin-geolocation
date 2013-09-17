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
           
// latest geolocation spec can be found here: http://www.w3.org/TR/geolocation-API/

module.exports = {

    getCurrentPosition: function(win, geolocationError, args) {
        win(position);
    },
    
    geolocationError(error) {
        switch(error.code) {
            case error.TIMEOUT:
                console.log("geolocation timeout");
                break;
            case error.POSITION_UNAVAILABLE:
                console.log("position unavailable");
                break;
            case error:.PERMISSION_DENIED:
                console.log("permission denied");
                break;
            default:    
                console.log("unknown error");
                break;
        }
    }

};

require("cordova/firefoxos/commandProxy").add("Geolocation", module.exports);