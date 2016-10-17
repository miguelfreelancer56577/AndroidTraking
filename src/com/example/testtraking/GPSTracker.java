package com.example.testtraking;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class GPSTracker extends IntentService implements LocationListener {
    // TODO: setup

    //get context
    private Context context;

    // log name
    private final String TAG = "mgps";

    //actions
    public static final String ACTION_RUN_ISERVICE = "run";

    // flag for GPS Status
    protected boolean isGPSEnabled = false;

    // flag for network status
    protected boolean isNetworkEnabled = false;

    // flag for GPS Tracking is enabled
    protected boolean isGPSTrackingEnabled = false;

    protected Location location;
    protected double latitude;
    protected double longitude;

    // How many Geocoder should return our GPSTracker
    protected int geocoderMaxResults = 1;

    // The minimum distance to change updates in meters
    protected long minDistanceChangeForUpdates; // 10 meters

    // The minimum time between updates in milliseconds
    protected long minTimeBwUpdates;

    // Declaring a Location Manager
    protected LocationManager locationManager;

    // Store LocationManager.GPS_PROVIDER or LocationManager.NETWORK_PROVIDER information
    protected String provider_info;

    public GPSTracker() {
        super("GPSTracker");
        //context = this.getApplicationContext();
    }

    public GPSTracker(long minDistanceChangeForUpdates, long minTimeBwUpdates, int geocoderMaxResults) {
        this();
        this.geocoderMaxResults = geocoderMaxResults;
        this.minDistanceChangeForUpdates = minDistanceChangeForUpdates;
        this.minTimeBwUpdates = minTimeBwUpdates;
    }

    public GPSTracker(Context context, long minDistanceChangeForUpdates, long minTimeBwUpdates, int geocoderMaxResults) {
        super("GPSTracker");
        //this.context = context;
        this.geocoderMaxResults = geocoderMaxResults;
        this.minDistanceChangeForUpdates = minDistanceChangeForUpdates;
        this.minTimeBwUpdates = minTimeBwUpdates;
    }

    public GPSTracker(long minDistanceChangeForUpdates, long minTimeBwUpdates) {
        super("GPSTracker");
        this.minTimeBwUpdates = minTimeBwUpdates;
        this.minDistanceChangeForUpdates = minDistanceChangeForUpdates;
    }

    /**
     * GPSTracker latitude getter and setter
     *
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
     *
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

    public long getMinDistanceChangeForUpdates() {
        return minDistanceChangeForUpdates;
    }

    public void setMinDistanceChangeForUpdates(long minDistanceChangeForUpdates) {
        this.minDistanceChangeForUpdates = minDistanceChangeForUpdates;
    }

    public long getMinTimeBwUpdates() {
        return minTimeBwUpdates;
    }

    public void setMinTimeBwUpdates(long minTimeBwUpdates) {
        this.minTimeBwUpdates = minTimeBwUpdates;
    }

    public int getGeocoderMaxResults() {
        return geocoderMaxResults;
    }

    public void setGeocoderMaxResults(int geocoderMaxResults) {
        this.geocoderMaxResults = geocoderMaxResults;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_RUN_ISERVICE.equals(action)) {
                runTraking();
            }
        }
    }

    protected void runTraking() {
        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

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

            	Log.d(TAG, "request location");

                locationManager.requestLocationUpdates( provider_info, minTimeBwUpdates, minDistanceChangeForUpdates, this );

                if (locationManager != null) {
                	Log.d(TAG, "get first location ");
                    location = locationManager.getLastKnownLocation(provider_info);
                    getLocation(location);
                }
            }
        } catch (SecurityException e) {
            Log.e(TAG, "Impossible to connect to LocationManager", e);
        } catch (Exception e) {
            //e.printStackTrace();
            Log.e(TAG, "Impossible to connect to LocationManager", e);
        }

    }

    /**
     * Try to get my current location by GPS or Network Provider
     */
    public void getLocation(Location location) {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }

    public void stopUsingGPS() {
        if (locationManager != null) {
            try {
                locationManager.removeUpdates(GPSTracker.this);
            } catch (SecurityException e) {
                Log.e(TAG, "ERROR to stop GPS", e);
            } catch (Exception e) {
                //e.printStackTrace();
                Log.e(TAG, "ERROR to stop GPS", e);
            }
        }
    }

    /**
     * Get list of address by latitude and longitude
     *
     * @return null or List<Address>
     */
    public List<Address> getGeocoderAddress() {
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
     *
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
     *
     * @return null or locality
     */
    public String getLocality() {
        List<Address> addresses = getGeocoderAddress();

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            String locality = address.getLocality();

            return locality;
        } else {
            return null;
        }
    }

    /**
     * Try to get Postal Code
     *
     * @return null or postalCode
     */
    public String getPostalCode() {
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
     *
     * @return null or postalCode
     */
    public String getCountryName() {
        List<Address> addresses = getGeocoderAddress();
        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            String countryName = address.getCountryName();

            return countryName;
        } else {
            return null;
        }
    }

    @Override
    public void onDestroy() {
        stopUsingGPS();
        Log.i(TAG, "Service destroyed");
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        getLocation(this.location);
        Log.i(TAG, "Tracking...");
        Log.i(TAG, "Longitud: " + this.longitude + " Latitud: " + this.latitude);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.i(TAG, "Provider Status: " + status);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.i(TAG, "Provider ON");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i(TAG, "Provider OFF");
    }
}

