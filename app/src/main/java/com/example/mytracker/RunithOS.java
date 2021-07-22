package com.example.mytracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

public class RunithOS extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase("android.intent.action.BOOT_COMPLETED")){
            GlobalInfo globalinfo=new GlobalInfo(context);
            globalinfo.LoadData();
            if(!TrackLocation.isRunning){
                TrackLocation trackLocation = new TrackLocation();
                LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        0, 0,trackLocation);
            }
            if(!MyService.isRunning){
                Intent intentt=new Intent(context,MyService.class);
                context.startService(intentt);
            }
        }
    }
}
