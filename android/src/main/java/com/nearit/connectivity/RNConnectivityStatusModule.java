package com.nearit.connectivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import androidx.core.content.ContextCompat;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RNConnectivityStatusModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;

    private final static String RN_CONNECTIVITY_STATUS_TOPIC = "RNConnectivityStatus";
    private final static String EVENT_TYPE = "eventType";
    private final static String EVENT_STATUS = "status";

    // Location permission status
    private static final String PERMISSION_LOCATION_GRANTED = "Location.Permission.Granted.Always";
    private static final String PERMISSION_LOCATION_DENIED = "Location.Permission.Denied";

    private final BroadcastReceiver mLocationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final boolean locationEnabled = intent.getAction() != null
                    && intent.getAction().matches(LocationManager.PROVIDERS_CHANGED_ACTION)
                    && checkLocationServices();

            final WritableMap eventMap = new WritableNativeMap();
            eventMap.putString(EVENT_TYPE, "location");
            eventMap.putBoolean(EVENT_STATUS, locationEnabled);
            getReactApplicationContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(RN_CONNECTIVITY_STATUS_TOPIC, eventMap);
        }
    };

    public RNConnectivityStatusModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNConnectivityStatus";
    }

    @javax.annotation.Nullable
    @Override
    public Map<String, Object> getConstants() {
        return Collections.unmodifiableMap(new HashMap<String, Object>() {
            {
                put("LocationGrantedAlways", PERMISSION_LOCATION_GRANTED);
                put("LocationDenied", PERMISSION_LOCATION_DENIED);
            }
        });
    }

    @Override
    public void initialize() {
        super.initialize();

        final IntentFilter locationFilter = new IntentFilter();
        locationFilter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        reactContext.getApplicationContext().registerReceiver(mLocationReceiver, locationFilter);
    }

    @ReactMethod
    public void areLocationServicesEnabled(final Promise promise) {
        try {
            promise.resolve(checkLocationServices());
        } catch (Exception e) {
            promise.reject("LOCATION_CHECK_ERROR", e.getMessage());
        }
    }

    @ReactMethod
    public void isLocationPermissionGranted(final Promise promise) {
        try {
            if (checkLocationPermission()) {
                promise.resolve(PERMISSION_LOCATION_GRANTED);
            } else {
                promise.resolve(PERMISSION_LOCATION_DENIED);
            }
        } catch (Exception e) {
            promise.reject("LOCATION_PERMISSION_CHECK_ERROR", e.getMessage());
        }
    }

    /**
     * Private methods
     */
    private boolean checkLocationServices() {
        final LocationManager locationManager = (LocationManager) getReactApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        return (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                | (locationManager != null && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }

    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(getReactApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
}