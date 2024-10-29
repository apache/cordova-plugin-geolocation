// GeolocationProxy.js
var geolocation = {
    getCurrentPosition: function(successCallback, errorCallback, options) {
        if (!navigator.geolocation) {
            errorCallback({
                code: 2,
                message: "Geolocation not supported"
            });
            return;
        }

        navigator.geolocation.getCurrentPosition(
            function(position) {
                successCallback({
                    coords: {
                        latitude: position.coords.latitude,
                        longitude: position.coords.longitude,
                        altitude: position.coords.altitude,
                        accuracy: position.coords.accuracy,
                        altitudeAccuracy: position.coords.altitudeAccuracy,
                        heading: position.coords.heading,
                        speed: position.coords.speed
                    },
                    timestamp: position.timestamp
                });
            },
            function(error) {
                errorCallback({
                    code: error.code,
                    message: error.message
                });
            },
            options
        );
    },

    watchPosition: function(successCallback, errorCallback, options) {
        if (!navigator.geolocation) {
            errorCallback({
                code: 2,
                message: "Geolocation not supported"
            });
            return -1;
        }

        return navigator.geolocation.watchPosition(
            function(position) {
                successCallback({
                    coords: {
                        latitude: position.coords.latitude,
                        longitude: position.coords.longitude,
                        altitude: position.coords.altitude,
                        accuracy: position.coords.accuracy,
                        altitudeAccuracy: position.coords.altitudeAccuracy,
                        heading: position.coords.heading,
                        speed: position.coords.speed
                    },
                    timestamp: position.timestamp
                });
            },
            function(error) {
                errorCallback({
                    code: error.code,
                    message: error.message
                });
            },
            options
        );
    },

    clearWatch: function(watchId) {
        if (navigator.geolocation) {
            navigator.geolocation.clearWatch(watchId);
        }
    }
};

// Registrar el proxy en Cordova
require('cordova/exec/proxy').add('Geolocation', geolocation);
