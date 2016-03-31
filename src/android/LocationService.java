package org.apache.cordova.geolocation;

import org.json.JSONObject;

import android.Manifest;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.PendingIntent;
import android.app.AlarmManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.webkit.CookieManager;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;

public class LocationService extends BroadcastReceiver implements LocationListener {

	private static String uploadUrl = "http://test.dingdongcheguanjia.com/api/geolocation";

	public String getUploadUrl() {
		return uploadUrl;
	}

	public void setUploadUrl(String uploadUrl) {
		this.uploadUrl = uploadUrl;
	}

	public void start(Context context) {
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, LocationService.class);
		PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
		// 60秒运行一次
		alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 1000 * 60,
				alarmIntent);

		startLocation(context);
	}

	private void startLocation(Context context) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (context
					.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
					&& context.checkSelfPermission(
					Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				return;
			}
		}

		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		// 注册位置更新监听(最小时间间隔为60秒,最小距离间隔为5米)
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60 * 1000, 5, this);
		Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if (location != null) {
			ConnectivityManager connMgr = (ConnectivityManager)
					context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			if (networkInfo != null && networkInfo.isConnected()) {
				new UploadLocationTask().execute(location);
			} else {
				Log.d("LocationService", networkInfo.toString());
			}
		}
	}

	private class UploadLocationTask extends AsyncTask<Location, Void, String> {
		@Override
		protected String doInBackground(Location... locations) {
			uploadLocation(locations[0]);
			return "";
		}
		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {

		}

		public void uploadLocation(Location location) {
			try {
				JSONObject parameters = new JSONObject();
				parameters.put("longitude", location.getLongitude());
				parameters.put("latitude", location.getLatitude());

				String encoding = "UTF-8";
				byte[] data = parameters.toString().getBytes(encoding);
				URL url = new URL(LocationService.uploadUrl);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("POST");
				conn.setDoOutput(true);
				conn.setRequestProperty("Content-Type", "application/json;charset=" + encoding);
				conn.setRequestProperty("Content-Length", String.valueOf(data.length));
				conn.setConnectTimeout(10 * 1000);
				String cookie = CookieManager.getInstance().getCookie(LocationService.uploadUrl);
				conn.setRequestProperty("Cookie", cookie);

				OutputStream outStream = conn.getOutputStream();
				outStream.write(data);
				outStream.flush();
				outStream.close();
				Log.d("LocationService", conn.getResponseCode() + "");
			} catch (Exception e) {
				Log.d("LocationService", "uploadLocation error", e);
			}
		}
	}

	@Override
	public void onLocationChanged(Location location) {

	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent != null && intent.getAction() != null && intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			Log.d("LocationService", "android.intent.action.BOOT_COMPLETED");
			LocationService locationService = new LocationService();
			locationService.start(context);
		} else {
			Log.d("LocationService", "alarm");
			startLocation(context);
		}
	}

}