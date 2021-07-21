package com.example.mytracker;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import androidx.annotation.NonNull;

public class TrackLocation implements LocationListener {
    public static Location location;
    public static boolean isRunning=false;
    TrackLocation(){
        isRunning=true;
        location=new Location("not defined");
        location.setLatitude(0);
        location.setLongitude(0);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
    this.location=location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
}
