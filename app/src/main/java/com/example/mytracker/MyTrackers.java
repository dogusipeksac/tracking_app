package com.example.mytracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Map;

public class MyTrackers extends AppCompatActivity {

    //adapter class
    ArrayList<AdapterItems> listnewsData = new ArrayList<AdapterItems>();
    MyCustomAdapter myadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trackers);

        //add data and view it
       // listnewsData.add(new AdapterItems("dogusipeksac","05050012248"));
        myadapter=new MyCustomAdapter(listnewsData);
        ListView lsNews=findViewById(R.id.listVieww);
        lsNews.setAdapter(myadapter);//intisal with data
        refresh();
        lsNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GlobalInfo.myTrackers.remove(listnewsData.get(position).phoneNumber);
                DatabaseReference mDatabaseReference= FirebaseDatabase.getInstance().getReference();
                mDatabaseReference.child("Users").child(listnewsData.get(position).phoneNumber)
                        .child("Finders")
                        .child(GlobalInfo.phoneNumber)
                        .removeValue();
                GlobalInfo globalInfo=new GlobalInfo(getApplicationContext());
                globalInfo.SaveData();
                refresh();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    void refresh(){
        listnewsData.clear();
        for (Map.Entry m:GlobalInfo.myTrackers.entrySet()){
            listnewsData.add(new AdapterItems(m.getValue().toString(),m.getKey().toString()));
        }
        myadapter.notifyDataSetChanged();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_contact_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.goback:
                GlobalInfo globalInfo=new GlobalInfo(this);
                globalInfo.SaveData();
                finish();
                return true;
            case R.id.add:
                CheckUserPermsions();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //access to permsions
    void CheckUserPermsions(){
        if ( Build.VERSION.SDK_INT >= 23){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) !=
                    PackageManager.PERMISSION_GRANTED  ){
                requestPermissions(new String[]{
                                Manifest.permission.READ_CONTACTS},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return ;
            }
        }

        PickContact();// init the contact list

    }
    //get acces to location permsion
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    PickContact();// init the contact list
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

    // pick phone number

    void PickContact(){
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT);
    }

    // Declare
    static final int PICK_CONTACT=1;
    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        switch (reqCode) {
            case (PICK_CONTACT) :
                if (resultCode == Activity.RESULT_OK) {

                    Uri contactData = data.getData();
                    Cursor c =  getContentResolver().query(contactData, null, null, null, null);
                    if (c.moveToFirst()) {


                        String id =c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                        String hasPhone =c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        String cNumber="No number";
                        if (hasPhone.equalsIgnoreCase("1")) {
                            Cursor phones = getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,
                                    null, null);
                            phones.moveToFirst();
                            cNumber =GlobalInfo.formatPhoneNumber (phones.getString(phones.getColumnIndex("data1")));
                            System.out.println("number is:"+cNumber);
                        }
                        String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                        GlobalInfo.myTrackers.put(cNumber,name);
                        DatabaseReference mDatabaseReference= FirebaseDatabase.getInstance().getReference();
                        mDatabaseReference.child("Users").child(cNumber)
                                .child("Finders")
                                .child(GlobalInfo.phoneNumber)
                                .setValue(true);
                        GlobalInfo globalInfo=new GlobalInfo(this);
                        globalInfo.SaveData();
                        refresh();
                        //update firebase and
                        //update list
                        //update database
                    }
                }
                break;
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
            View myView = mInflater.inflate(R.layout.single_row_conact, null);

            final   AdapterItems s = listnewsDataAdpater.get(position);

            TextView txtUserName=( TextView) myView.findViewById(R.id.text_userName);
            txtUserName.setText(s.username);
            TextView textPhoneNumber=( TextView) myView.findViewById(R.id.text_Phone);
            textPhoneNumber.setText(s.phoneNumber);

            return myView;
        }


    }

}