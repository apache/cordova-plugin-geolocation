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

import android.content.pm.PackageManager;
import android.Manifest;
import android.os.Build;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PermissionHelper;
import org.apache.cordova.PluginResult;
import org.apache.cordova.LOG;
import org.json.JSONArray;
import org.json.JSONException;

// ----------------------
import org.apache.cordova.geolocation.FusedLocationHelper;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;

import android.content.DialogInterface;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import java.util.ArrayList;
import android.support.v4.app.ActivityCompat;


//-----------------

import com.google.android.gms.common.api.GoogleApiClient;

import javax.security.auth.callback.Callback;

// 

/* private static final String actiongetLocation = "getLocation";
    private static final String actiongetCurrentAddress = "getCurrentAddress";
    protected FusedLocationHelper locHelper;
    
    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        Activity cordovaActivity = cordova.getActivity();

        locHelper = new FusedLocationHelper(cordovaActivity);
    }
    
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals(actiongetLocation)) {
            locHelper.GetLocation(callbackContext);
            return true;
        }
        else if (action.equals(actiongetCurrentAddress)) {
            locHelper.GetAddress(callbackContext);
            return true;
        }

        return false;
    }
 */
//

public class Geolocation extends CordovaPlugin {

    String TAG = "GeolocationPlugin";
    CallbackContext context;
    
    String [] permissionsToRequest = { Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION };
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private GoogleApiClient googleApiClient;
    // integer for permissions results request
    private static final int ALL_PERMISSIONS_RESULT = 1011;

    protected FusedLocationHelper locHelper;

     @Override
    public void pluginInitialize() {
        super.pluginInitialize();

        locHelper = new FusedLocationHelper(this.cordova.getActivity());
    }
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        LOG.d(TAG, "We are entering execute");
        context = callbackContext;
        
        // if(hasPermisssion())
        // {
            if (action.equals("startLocationTracking")) {
                locHelper.startLocationTracking(callbackContext);
                return true;
            }
            else if (action.equals("stopLocationTracking")) {
                locHelper.stopLocationTracking(callbackContext);
                return true;
            }
        /* }
        else {
            PermissionHelper.requestPermissions(this, ALL_PERMISSIONS_RESULT, permissionsToRequest);
        } */
        return true; 
    }


    public void onRequestPermissionResult(int requestCode, String[] permissions,
                                          int[] grantResults) throws JSONException
    {
        
      switch(requestCode) {
        case ALL_PERMISSIONS_RESULT:
            for(String perm : permissionsToRequest)
            {
                if(!PermissionHelper.hasPermission(this, perm))
                {
                    permissionsRejected.add(perm);
                }
            }

            if (permissionsRejected.size() > 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this.cordova.getActivity(),permissionsRejected.get(0))) {
                        new AlertDialog.Builder(this.cordova.getActivity()).
                            setMessage("These permissions are mandatory to get your location. You need to allow them.").
                            setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    PermissionHelper.requestPermissions(Geolocation.this, ALL_PERMISSIONS_RESULT, permissionsRejected.
                                        toArray(new String[permissionsRejected.size()]));
                                }
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    LOG.d(TAG, "Permission Denied!");
                                    PluginResult result = new PluginResult(PluginResult.Status.ILLEGAL_ACCESS_EXCEPTION);
                                    context.sendPluginResult(result);
                                    return;
                                }
                            }).create().show();

                        return;
                    }
                }
            } else {
                if (googleApiClient != null) {
                    googleApiClient.connect();
                    PluginResult result = new PluginResult(PluginResult.Status.OK);
                    context.sendPluginResult(result);
                }else {
                    LOG.d(TAG, "Google api client not instantiated!");
                    PluginResult result = new PluginResult(PluginResult.Status.INSTANTIATION_EXCEPTION);
                    context.sendPluginResult(result);
                    return;
                }
            }

            break;
        }
        
    }

    /* private boolean hasPermissionGranted(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        return ActivityCompat.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }
 */
    
    public boolean hasPermisssion() {
        for(String p : permissionsToRequest)
        {
            if(!PermissionHelper.hasPermission(this, p))
            {
                return false;
            }
        }
        return true;
    }
    

    /*
     * We override this so that we can access the permissions variable, which no longer exists in
     * the parent class, since we can't initialize it reliably in the constructor!
     */

    public void requestPermissions(int requestCode)
    {
        PermissionHelper.requestPermissions(this, requestCode, permissionsToRequest);
    }



}
