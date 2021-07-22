package com.example.mytracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    //adapter class
    ArrayList<AdapterItems> listnewsData = new ArrayList<>();
    MyCustomAdapter myadapter;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GlobalInfo globalInfo= new GlobalInfo(this);
        globalInfo.LoadData();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        CheckUserPermsions();


        myadapter=new MyCustomAdapter(listnewsData);
        ListView lsNews=findViewById(R.id.listViewMain);

        lsNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AdapterItems adapterItems=listnewsData.get(position);
                GlobalInfo.updatesInfo(adapterItems.phoneNumber);
                Intent intent=new Intent(getApplicationContext(),MapsActivity.class);
                intent.putExtra("phoneNumber",adapterItems.phoneNumber);
                startActivity(intent);
            }
        });
        lsNews.setAdapter(myadapter);//intisal with data

    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    void refresh(){
        listnewsData.clear();
        //list my tracking
        databaseReference.child("Users").child(GlobalInfo.phoneNumber).
                child("Finders").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();

                listnewsData.clear();
                if (td == null)  //no one allow you to find him
                {
                    System.out.println("fsaasfasffssafsfasfafs");
                    listnewsData.add(new AdapterItems("NoTicket", "no_desc"));
                    myadapter.notifyDataSetChanged();
                    return;
                }
                // List<Object> values = td.values();
                // get all contact to list
                ArrayList<AdapterItems> list_contact = new ArrayList<>();
                Cursor cursor = getContentResolver()
                        .query(ContactsContract
                                        .CommonDataKinds
                                        .Phone.CONTENT_URI,
                                null, null, null, null);
                while (cursor.moveToNext()) {
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                    String phoneNumber = cursor.getString(cursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    list_contact.add(new AdapterItems(name,GlobalInfo.formatPhoneNumber(phoneNumber)
                    ));


                }


                // if the name is save chane his text
                // case who find me
                String tinfo;
                for (String Numbers : td.keySet()) {
                    for (AdapterItems cs : list_contact) {
                        //IsFound = SettingSaved.WhoIFindIN.get(cs.Detals);  // for case who i could find list
                        if (cs.phoneNumber.length() > 0)
                            if (Numbers.contains(cs.phoneNumber)) {
                                listnewsData.add(new AdapterItems(cs.username, cs.phoneNumber));
                                break;
                            }

                    }

                }
                myadapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                // Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        myadapter.notifyDataSetChanged();
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
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    0, 0,trackLocation);
        }
        if(!MyService.isRunning){
            Intent intent=new Intent(this,MyService.class);
            startService(intent);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.addTracker:
                Intent intent=new Intent(this,MyTrackers.class);
                startActivity(intent);
                return true;
            case R.id.help:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //display news list
    private class MyCustomAdapter extends BaseAdapter {
        public  ArrayList<AdapterItems>  listnewsDataAdpater ;

        public MyCustomAdapter(ArrayList<AdapterItems> listnewsDataAdpater) {
            this.listnewsDataAdpater=listnewsDataAdpater;
        }


        @Override
        public int getCount() {
            return listnewsDataAdpater.size();
        }

        @Override
        public String getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            LayoutInflater mInflater = getLayoutInflater();

            View myView;
            final   AdapterItems s = listnewsDataAdpater.get(position);
            if(s.username.equals("NoTicket")){
                myView = mInflater.inflate(R.layout.news_ticket_no_news, null);
                return myView;
            }
            else {
                myView = mInflater.inflate(R.layout.single_row_conact, null);
                TextView txtUserName=( TextView) myView.findViewById(R.id.text_userName);
                txtUserName.setText(s.username);
                TextView textPhoneNumber=( TextView) myView.findViewById(R.id.text_Phone);
                textPhoneNumber.setText(s.phoneNumber);

                return myView;
            }


        }


    }
}