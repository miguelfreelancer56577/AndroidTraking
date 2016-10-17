package com.example.testtraking;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

/**
 * Created by @moizest89 in SV on 8/19/15.
 */


public abstract  class GPSTracker implements LocationListener {

    // Get Class Name
    private static String TAG = "GPSTracker";

    protected Context context;

    // flag for GPS Status
    private boolean isGPSEnabled = false;

    // flag for network status
    private boolean isNetworkEnabled = false;

    // flag for GPS Tracking is enabled
    private boolean isGPSTrackingEnabled = false;

    protected Location location;
    private double latitude;
    private double longitude;

    // How many Geocoder should return our GPSTracker
    private int geocoderMaxResults;

    // The minimum distance to change updates in meters
    private long minDistance; 

    // The minimum time between updates in milliseconds
    private long minTime;

    // Declaring a Location Manager
    protected LocationManager locationManager;

    // Store LocationManager.GPS_PROVIDER or LocationManager.NETWORK_PROVIDER information
    private String provider_info;

    public GPSTracker(Context context) {
        this.context = context;
        this.minDistance = 0;
        this.minTime = 0;
        this.provider_info = null;
        this.geocoderMaxResults = 1;
    }

    public GPSTracker(Context context, long minDistance, long minTime) {
        this(context);
        this.minDistance = minDistance;
        this.minTime = minTime;
    }

    public GPSTracker(Context context, long minDistance, long minTime, int geocoderMaxResults) {
        this(context);
        this.minDistance = minDistance;
        this.minTime = minTime;
        this.geocoderMaxResults = geocoderMaxResults;
    }

    public void runLocationTracking(){
        try {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            //getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            //getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            // Try to get location if you GPS Service is enabled
            if (isGPSEnabled) {
                this.isGPSTrackingEnabled = true;

                Log.d(TAG, "Application use GPS Service");

                /*
                 * This provider determines location using
                 * satellites. Depending on conditions, this provider may take a while to return
                 * a location fix.
                 */

                provider_info = LocationManager.GPS_PROVIDER;

            } else if (isNetworkEnabled) { // Try to get location if you Network Service is enabled
                this.isGPSTrackingEnabled = true;

                Log.d(TAG, "Application use Network State to get GPS coordinates");

                /*
                 * This provider determines location based on
                 * availability of cell tower and WiFi access points. Results are retrieved
                 * by means of a network lookup.
                 */
                provider_info = LocationManager.NETWORK_PROVIDER;

            }
      
                    

            // Application can use GPS or Network Provider
            if (provider_info != null) {
            	
            	location = locationManager.getLastKnownLocation(provider_info);
                getLocation();
            	
            	locationManager.requestLocationUpdates(
                        provider_info,
                        minTime,
                        minDistance,
                        GPSTracker.this
                );
            	
            }
        }
        catch (Exception e)
        {
            //e.printStackTrace();
            Log.e(TAG, "Impossible to connect to LocationManager", e);
        }
}


    /**
     * Try to get my current location by GPS or Network Provider
     */
    public void getLocation() {

        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }

    }

    public void getLocation(Location location) {
        this.location = location;
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }

    /**
     * Update GPSTracker latitude and longitude
     */
    

    /**
     * GPSTracker latitude getter and setter
     * @return latitude
     */
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        return latitude;
    }

    /**
     * GPSTracker longitude getter and setter
     * @return
     */
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        return longitude;
    }

    /**
     * GPSTracker isGPSTrackingEnabled getter.
     * Check GPS/wifi is enabled
     */
    public boolean getIsGPSTrackingEnabled() {

        return this.isGPSTrackingEnabled;
    }
    
    /**
     * GPSTracker getGeocoderMaxResults getter.
     * @return concurrent geocoderMaxResults
     */
    public int getGeocoderMaxResults() {
		return geocoderMaxResults;
	}

    /**
     * GPSTracker getGeocoderMaxResults setter.
     * @param geocoderMaxResults
     * @return concurrent geocoderMaxResults
     */
	public void setGeocoderMaxResults(int geocoderMaxResults) {
		this.geocoderMaxResults = geocoderMaxResults;
	}
	
	/**
     * GPSTracker getMinDistance getter.
     * @return concurrent minDistance
     */
	public long getMinDistance() {
		return minDistance;
	}
	
	/**
     * GPSTracker setMinDistance setter.
     * @param minDistance
     * @return concurrent minDistance
     */
	public void setMinDistance(long minDistance) {
		this.minDistance = minDistance;
	}
	
	/**
     * GPSTracker getMinTime getter.
     * @return concurrent minTime
     */
	public long getMinTime() {
		return minTime;
	}

	/**
     * GPSTracker setMinTime setter.
     * @param minTime
     * @return concurrent minTime
     */
	public void setMinTime(long minTime) {
		this.minTime = minTime;
	}

	/**
     * Stop using GPS listener
     * Calling this method will stop using GPS in your app
     */
    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    /**
     * Function to show settings alert dialog
     */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        //Setting Dialog Title
        alertDialog.setTitle("Settings");

        //Setting Dialog Message
        alertDialog.setMessage("Show settings of gps");

        //On Pressing Setting button
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });

        //On pressing cancel button
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    /**
     * Get list of address by latitude and longitude
     * @return null or List<Address>
     */
    public List<Address> getGeocoderAddress( ) {
        if (location != null) {

            Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);

            try {
                /**
                 * Geocoder.getFromLocation - Returns an array of Addresses
                 * that are known to describe the area immediately surrounding the given latitude and longitude.
                 */
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, this.geocoderMaxResults);

                return addresses;
            } catch (IOException e) {
                //e.printStackTrace();
                Log.e(TAG, "Impossible to connect to Geocoder", e);
            }
        }

        return null;
    }

    /**
     * Try to get AddressLine
     * @return null or addressLine
     */
    public String getAddressLine() {
        List<Address> addresses = getGeocoderAddress();

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            String addressLine = address.getAddressLine(0);

            return addressLine;
        } else {
            return null;
        }
    }

    /**
     * Try to get Locality
     * @return null or locality
     */
    public String getLocality( ) {
        List<Address> addresses = getGeocoderAddress();

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            String locality = address.getLocality();

            return locality;
        }
        else {
            return null;
        }
    }

    /**
     * Try to get Postal Code
     * @return null or postalCode
     */
    public String getPostalCode( ) {
        List<Address> addresses = getGeocoderAddress();

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            String postalCode = address.getPostalCode();

            return postalCode;
        } else {
            return null;
        }
    }

    /**
     * Try to get CountryName
     * @return null or postalCode
     */
    public String getCountryName( ) {
        List<Address> addresses = getGeocoderAddress();
        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            String countryName = address.getCountryName();

            return countryName;
        } else {
            return null;
        }
    }

}