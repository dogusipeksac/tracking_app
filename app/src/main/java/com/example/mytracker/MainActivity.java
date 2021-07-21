 package com.example.mytracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.Toast;

 public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GlobalInfo globalInfo=new GlobalInfo(this);
        globalInfo.LoadData();
        CheckUserPermsions();

    }
     //access to permsions
     void CheckUserPermsions(){
         if ( Build.VERSION.SDK_INT >= 23){
             if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                     PackageManager.PERMISSION_GRANTED  ){
                 requestPermissions(new String[]{
                                 Manifest.permission.ACCESS_FINE_LOCATION,
                                 Manifest.permission.ACCESS_COARSE_LOCATION},
                         REQUEST_CODE_ASK_PERMISSIONS);
                 return ;
             }
         }

         startServices();// init the contact list

     }
     //get acces to location permsion
     final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
     @Override
     public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
         switch (requestCode) {
             case REQUEST_CODE_ASK_PERMISSIONS:
                 if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                     startServices();// init the contact list
                 } else {
                     // Permission Denied
                     Toast.makeText( this,"your message" , Toast.LENGTH_SHORT)
                             .show();
                 }
                 break;
             default:
                 super.onRequestPermissionsResult(requestCode, permissions, grantResults);
         }
     }

     void startServices(){
        //start location track
        if(!TrackLocation.isRunning){
            TrackLocation trackLocation = new TrackLocation();
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,trackLocation);
        }
    }
}