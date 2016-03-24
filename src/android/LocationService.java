package org.apache.cordova.geolocation;

import org.json.JSONObject;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.PendingIntent;
import android.app.AlarmManager;
import android.os.Bundle;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;

public class LocationService extends BroadcastReceiver implements LocationListener {

	private AlarmManager alarmManager;
	private PendingIntent alarmIntent;
	private LocationManager locationManager;

	public void start(Context context) {
		alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, LocationService.class);
		alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

		alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, System.currentTimeMillis(), 1000 * 60,
				alarmIntent);

		startLocation(context);
	}

	public void startLocation(Context context) {
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		// 注册位置更新监听(最小时间间隔为60秒,最小距离间隔为5米)
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 1, this);
	}

	@Override
	public void onLocationChanged(Location location) {
		try {
			String path = "http://test.dingdongcheguanjia.com/api/geolocation";

			JSONObject parameters = new JSONObject();
			parameters.put("longitude", location.getLongitude());
			parameters.put("latitude", location.getLatitude());

			String encoding = "UTF-8";
			byte[] data = parameters.toString().getBytes(encoding);
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			// application/x-javascript text/xml->xml数据
			// application/x-javascript->json对象
			// application/x-www-form-urlencoded->表单数据
			conn.setRequestProperty("Content-Type", "application/x-javascript; charset=" + encoding);
			conn.setRequestProperty("Content-Length", String.valueOf(data.length));
			conn.setConnectTimeout(5 * 1000);
			OutputStream outStream = conn.getOutputStream();
			outStream.write(data);
			outStream.flush();
			outStream.close();
			// System.out.println(conn.getResponseCode()); //响应代码 200表示成功
		} catch (Exception e) {

		}
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
		if (intent.getAction().equals("org.apache.cordova.geolocation.action.ALARM")) {
			startLocation(context);
		} else if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			LocationService locationService = new LocationService();
			locationService.start(context);
		}
	}

}