package com.example.mytracker;

import androidx.fragment.app.FragmentActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.mytracker.databinding.ActivityMapsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Bundle b=getIntent().getExtras();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        loadLocation(b.getString("phoneNumber"));
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

    }
    void loadLocation(String phoneNumber){
        databaseReference.child("Users").child(phoneNumber).
                child("Location").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();
                if (td==null)return;
             double lat=Double.parseDouble(td.get("Lat").toString());
             double lag=Double.parseDouble(td.get("Log").toString());
             sydney = new LatLng(lat,lag);
             lastDateOnline=td.get("LastOnlineDate").toString();
             loadMap();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                // Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }
    void loadMap(){
        SupportMapFragment mapFragment=(SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    LatLng sydney;
    String lastDateOnline;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        mMap.addMarker(new MarkerOptions().position(sydney).title("last online:"+lastDateOnline));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,15));
    }
}