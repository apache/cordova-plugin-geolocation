/*
       Licensed to the Apache Software Foundation (ASF) under one
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
 */
package org.apache.cordova.geolocation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.Manifest;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;

import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Granularity;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;

import androidx.annotation.NonNull;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PermissionHelper;
import org.apache.cordova.PluginResult;
import org.apache.cordova.LOG;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public class Geolocation extends CordovaPlugin {

    String TAG = "GeolocationPlugin";
    CallbackContext context;

    String[] highAccuracyPermissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    String[] lowAccuracyPermissions = {Manifest.permission.ACCESS_COARSE_LOCATION};
    String[] permissionsToRequest;
    String[] permissionsToCheck;

    LocationManager manager;

    static final int REQUEST_CODE_GET_CURRENT_POSITION = 0;
    static final int REQUEST_CODE_WATCH_POSITION = 1;

    CurrentLocationRequest currentLocationRequest;

    FusedLocationProviderClient watchLocationClient;
    LocationRequest locationRequest;
    LocationCallback locationCallback;

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        LOG.d(TAG, "We are entering execute");
        context = callbackContext;
        switch (action) {
            case "getCurrentPosition": {
                if (isLocationProviderAvailable()) {
                    LOG.d(TAG, "Location Provider Unavailable!");
                    PluginResult result = new PluginResult(PluginResult.Status.ILLEGAL_ACCESS_EXCEPTION);
                    context.sendPluginResult(result);
                    return true;
                }

                boolean highAccuracy = args.getBoolean(0);
                permissionsToCheck = highAccuracy ? highAccuracyPermissions : lowAccuracyPermissions;

                currentLocationRequest = new CurrentLocationRequest.Builder()
                        .setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
                        .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                        .setMaxUpdateAgeMillis(args.getLong(1))
                        .setDurationMillis(args.isNull(2) ? Long.MAX_VALUE : args.getLong(2))
                        .build();

                // Always request both FINE & COARSE permissions on API <= 31 due to bug in WebView that manifests on these versions
                // See https://bugs.chromium.org/p/chromium/issues/detail?id=1269362
                permissionsToRequest = Build.VERSION.SDK_INT <= 31 ? highAccuracyPermissions : permissionsToCheck;

                if (hasPermission(permissionsToCheck)) {
                    getCurrentLocation();
                    return true;
                } else {
                    PermissionHelper.requestPermissions(this, REQUEST_CODE_GET_CURRENT_POSITION, permissionsToRequest);
                }
                return true;
            }
            case "watchPosition": {
                if (isLocationProviderAvailable()) {
                    LOG.d(TAG, "Location Provider Unavailable!");
                    PluginResult result = new PluginResult(PluginResult.Status.ILLEGAL_ACCESS_EXCEPTION);
                    context.sendPluginResult(result);
                    return true;
                }

                boolean highAccuracy = args.getBoolean(0);
                permissionsToCheck = highAccuracy ? highAccuracyPermissions : lowAccuracyPermissions;

                locationRequest = new LocationRequest.Builder(LocationRequest.Builder.IMPLICIT_MIN_UPDATE_INTERVAL)
                        .setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
                        .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                        .setMaxUpdateAgeMillis(args.getLong(1))
                        .setDurationMillis(args.isNull(2) ? Long.MAX_VALUE : args.getLong(2))
                        .build();

                // Always request both FINE & COARSE permissions on API <= 31 due to bug in WebView that manifests on these versions
                // See https://bugs.chromium.org/p/chromium/issues/detail?id=1269362
                permissionsToRequest = Build.VERSION.SDK_INT <= 31 ? highAccuracyPermissions : permissionsToCheck;

                if (hasPermission(permissionsToCheck)) {
                    requestLocationUpdates();
                    return true;
                } else {
                    PermissionHelper.requestPermissions(this, REQUEST_CODE_WATCH_POSITION, permissionsToRequest);
                }
                return true;
            }
            case "clearWatch":
                if (watchLocationClient != null) {
                    watchLocationClient.removeLocationUpdates(locationCallback);
                    watchLocationClient = null;
                }
                PluginResult result = new PluginResult(PluginResult.Status.OK);
                context.sendPluginResult(result);
                return true;
        }
        return false;
    }

    public void onRequestPermissionResult(int requestCode, String[] permissions,
                                          int[] grantResults) {
        PluginResult result;
        //This is important if we're using Cordova without using Cordova, but we have the geolocation plugin installed
        if (context != null) {
            for (int r : grantResults) {
                if (r == PackageManager.PERMISSION_GRANTED) {
                    if (requestCode == REQUEST_CODE_GET_CURRENT_POSITION) {
                        getCurrentLocation();
                    } else if (requestCode == REQUEST_CODE_WATCH_POSITION) {
                        requestLocationUpdates();
                    } else {
                        result = new PluginResult(PluginResult.Status.OK);
                        context.sendPluginResult(result);
                    }
                    return;
                }

            }
            LOG.d(TAG, "Permission Denied!");
            result = new PluginResult(PluginResult.Status.ILLEGAL_ACCESS_EXCEPTION);
            context.sendPluginResult(result);
        }
    }

    public boolean hasPermission(String[] permissions) {
        for (String p : permissions) {
            if (PermissionHelper.hasPermission(this, p)) {
                return true;
            }
        }
        return false;
    }

    /*
     * We override this so that we can access the permissions variable, which no longer exists in
     * the parent class, since we can't initialize it reliably in the constructor!
     */
    public void requestPermissions(int requestCode) {
        PermissionHelper.requestPermissions(this, requestCode, permissionsToRequest);
    }

    private boolean isLocationProviderAvailable() {
        manager = (LocationManager) this.cordova.getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        return !manager.isProviderEnabled(LocationManager.FUSED_PROVIDER);
    }

    private PluginResult getPluginResult(Location location) {
        if (location != null) {
            try {
                JSONObject result = getJsonObject(location);

                return new PluginResult(PluginResult.Status.OK, result);
            } catch (JSONException e) {
                return new PluginResult(PluginResult.Status.JSON_EXCEPTION);
            }
        } else {
            return new PluginResult(PluginResult.Status.ERROR);
        }
    }

    private static @NonNull JSONObject getJsonObject(Location location) throws JSONException {
        JSONObject result = new JSONObject();
        result.put("latitude", location.getLatitude());
        result.put("longitude", location.getLongitude());
        result.put("altitude", location.getAltitude());
        result.put("accuracy", location.getAccuracy());
        result.put("heading", location.getBearing());
        result.put("velocity", location.getSpeed());
        if (
                Build.VERSION_CODES.UPSIDE_DOWN_CAKE <= Build.VERSION.SDK_INT &&
                        location.hasMslAltitudeAccuracy()
        ) {
            result.put("altitudeAccuracy", location.getMslAltitudeAccuracyMeters());
        }
        result.put("time", location.getTime());
        result.put("deviceApiLevel", Build.VERSION.SDK_INT);
        return result;
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.cordova.getActivity().getApplicationContext());
        CancellationTokenSource cts = new CancellationTokenSource();

        fusedLocationClient.getCurrentLocation(
                        currentLocationRequest,
                        cts.getToken())
                .addOnSuccessListener(
                        this.cordova.getActivity(), location -> {
                            PluginResult r = getPluginResult(location);
                            context.sendPluginResult(r);
                        })
                .addOnFailureListener(this.cordova.getActivity(), e -> {
                    PluginResult r = new PluginResult(PluginResult.Status.ERROR);
                    context.sendPluginResult(r);
                });
    }

    @SuppressLint("MissingPermission")
    private void requestLocationUpdates() {
        watchLocationClient = LocationServices.getFusedLocationProviderClient(this.cordova.getActivity().getApplicationContext());
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                PluginResult r = getPluginResult(locationResult.getLastLocation());
                r.setKeepCallback(true);
                context.sendPluginResult(r);
            }
        };
        watchLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }
}
