package org.apache.cordova.geolocation;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.apache.cordova.CallbackContext;

import android.Manifest;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import android.location.Location;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.text.TextUtils;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.content.DialogInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import android.content.pm.PackageManager;
import android.os.Build;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
// import android.support.v7.app.AlertDialog;
// import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.LocationListener;

public class FusedLocationHelper extends Activity implements GoogleApiClient.ConnectionCallbacks,
               GoogleApiClient.OnConnectionFailedListener, LocationListener, ResultCallback<LocationSettingsResult>   {
                   
    protected Activity mActivity = null;
    protected static final String TAG = "fusedlocation-plugin";
    protected CallbackContext mCallBackWhenGotLocation;
    protected GoogleApiClient mGoogleApiClient;
    protected LocationSettingsRequest mLocationSettingsRequest;
	protected LocationRequest mLocationRequest;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final long UPDATE_INTERVAL = 5000, FASTEST_INTERVAL = 5000; // = 5 seconds

    private ArrayList<String> permissionsToRequest;
	private ArrayList<String> permissionsRejected = new ArrayList<>();
	private ArrayList<String> permissions = new ArrayList<>();
	// integer for permissions results request
	private static final int ALL_PERMISSIONS_RESULT = 1011;
    
    protected boolean mGetAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // we add permissions we need to request location of the users
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        permissionsToRequest = permissionsToRequest(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (permissionsToRequest.size() > 0) {
        requestPermissions(permissionsToRequest.toArray(
                new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }
        }

        // we build google api client
        /* googleApiClient = new GoogleApiClient.Builder(this).
        addApi(LocationServices.API).
        addConnectionCallbacks(this).
        addOnConnectionFailedListener(this).build(); */
    }

    private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wantedPermissions) {
        if (!hasPermission(perm)) {
            result.add(perm);
        }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, 
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            &&  ActivityCompat.checkSelfPermission(this, 
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        return;
        }

        // Permissions ok, we get last location
        /* location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (location != null) {
        locationTv.setText("Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());
        }

        startLocationUpdates(); */
        checkLocationSettings();
    }

    private void startLocationUpdates() {
       
        if (ActivityCompat.checkSelfPermission(this, 
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            &&  ActivityCompat.checkSelfPermission(this, 
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        Toast.makeText(this, "You need to enable permissions to display location !", Toast.LENGTH_SHORT).show();
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        ErrorHappened("onConnectionFailed. Error code: " + connectionResult.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
        // locationTv.setText("Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());
        Log.d(TAG, "Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());
        }
    }


    public FusedLocationHelper(Activity activity) {
        mActivity = activity;
    }

    public void startLocationTracking(CallbackContext cb) {
        mGetAddress = false;
		mCallBackWhenGotLocation = cb;
		CheckForPlayServices();
        SetupLocationFetching(cb);
    }

    public void stopLocationTracking(CallbackContext cb) {
        mGetAddress = false;
		mCallBackWhenGotLocation = cb;
        StopLocationFetching(cb);
    }
    
    public void GetAddress(CallbackContext cb) {
        mGetAddress = true;
		mCallBackWhenGotLocation = cb;
		CheckForPlayServices();
        SetupLocationFetching(cb);
    }

	protected void CheckForPlayServices() {
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mActivity);
		if (status != ConnectionResult.SUCCESS) {
			Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(status, mActivity, 10, new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
              ErrorHappened("onCancel called on ErrorDialog. ");
            }
			});
			 if (errorDialog != null) {
                errorDialog.show();
            } else {
				ErrorHappened("CheckForPlayServices failed. Error code: " + status);
			}
		}
	}

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
       // Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }
    
    protected void ErrorHappened(String msg) {
        Log.i(TAG, msg);
        mCallBackWhenGotLocation.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, msg));
    }  

    protected void SetupLocationFetching(CallbackContext cb) {

         buildGoogleApiClient();
		 createLocationRequest();
         buildLocationSettingsRequest();
         mGoogleApiClient.connect();
    }

    protected void StopLocationFetching(CallbackContext cb) {
        // stop location updates
        if (mGoogleApiClient != null  &&  mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this); 
            mGoogleApiClient.disconnect();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

	 protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
    }
    
     protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }
    
    protected void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(this);
    }
    
    /* 
    protected void GetLastLocation() {
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (lastLocation != null) {
            try {
		    JSONObject jsonLocation = new JSONObject();
            jsonLocation.put("lat", String.valueOf(lastLocation.getLatitude()));
            jsonLocation.put("lon", String.valueOf(lastLocation.getLongitude()));
            if (mGetAddress) {
                GetAddressFromLocation(lastLocation);
            } else {
                mCallBackWhenGotLocation.success(jsonLocation);
            }
            }
            catch (JSONException ex) {
                 ErrorHappened("Error generating JSON from location"); 
            }         
        } else {
            ErrorHappened("no location available");         
        }
    }
    
    protected void GetAddressFromLocation(Location lastLocation) {
        Geocoder geocoder = new Geocoder(mActivity, Locale.getDefault());

        List<Address> addresses = null;
        
        try {
            addresses = geocoder.getFromLocation(
                    lastLocation.getLatitude(),
                    lastLocation.getLongitude(),
                    1);
        } catch (IOException ioException) {
             ErrorHappened("Service not available");       
        	return;
        } catch (IllegalArgumentException illegalArgumentException) {
             ErrorHappened("Invalid location params used");
        	return;
        }
        
        // Handle case where no address was found.
        if (addresses == null || addresses.size()  == 0) {
             ErrorHappened("No address found");
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();
            for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            mCallBackWhenGotLocation.success(TextUtils.join(System.getProperty("line.separator"), addressFragments));
        }
    } 
    */
    
    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.i(TAG, "All location settings are satisfied.");
                // GetLastLocation();
                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(mActivity, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {                   
                    ErrorHappened("PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:              
                ErrorHappened("Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	  Log.i(TAG, "onActivityResult called with reqestCode " + requestCode + " and resultCode " +resultCode);
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
                        // GetLastLocation();
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        ErrorHappened("User chose not to make required location settings changes.");                       
                        break;
                }
                break;
          }		
    }
    
    
}


