package com.example.mytracker;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyService extends IntentService{

    public static boolean isRunning=false;
    DatabaseReference databaseReference;
    public MyService() {
       super("MyService");
       isRunning=true;
       databaseReference= FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        databaseReference.child("Users").child(GlobalInfo.phoneNumber)
                .child("Updates").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Location location=TrackLocation.location;
                databaseReference.child("Users").child(GlobalInfo.phoneNumber)
                        .child("Location").child("Lat")
                        .setValue(TrackLocation.location.getLatitude());

                databaseReference.child("Users").child(GlobalInfo.phoneNumber)
                        .child("Location").child("Log")
                        .setValue(TrackLocation.location.getLongitude() );
                DateFormat df=new SimpleDateFormat("yyyy/MM/dd HH:MM:ss");
                Date date=new Date();
                databaseReference.child("Users").child(GlobalInfo.phoneNumber)
                        .child("Location").child("LastOnlineDate")
                        .setValue(df.format(date).toString());
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }
}
