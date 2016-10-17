package com.example.testtraking;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class Tracking extends GPSTracker{
	
	protected TextView longitud;
	protected TextView latitud;
	
	/**
	 * call super constructor
	 * @param context
	 */
	public Tracking(Context context, long minDistance, long minTime,TextView longitud,TextView latitud) {
		super(context, minDistance, minTime);
		this.longitud = longitud;
		this.latitud = latitud;
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Get a Location changed
	 */
	@Override
	public void onLocationChanged(Location location) {
		getLocation(location);
		if(getLatitude() != 0.0 && getLongitude() != 0.0){
			latitud.setText("Latitud: " + String.valueOf(getLatitude()));
			longitud.setText("Longitud: " + String.valueOf(getLongitude()));
		}else{
			latitud.setText("Latitud: (sin_datos)");
			longitud.setText("Longitud: (sin_datos)");
		}
	}
	
	/**
	 * Provider of gps disabled
	 */
	@Override
	public void onProviderDisabled(String provider){
		Log.d("gps","Provider OFF");
	}
	/**
	 * Provider of gps enabled
	 */
	@Override
	public void onProviderEnabled(String provider){
		Log.d("gps","Provider ON");
	}
	/**
	 * Listening on status changed
	 */
	@Override
	public void onStatusChanged(String provider, int status,Bundle extras){
		Log.i("gps", "Provider Status: " + status);
	}
}
