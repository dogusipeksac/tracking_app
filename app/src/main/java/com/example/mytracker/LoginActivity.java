package com.example.mytracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {
    EditText editPhoneNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editPhoneNumber=findViewById(R.id.EDTNumber);
    }

    public void buttonNext(View view) {
        GlobalInfo.phoneNumber=GlobalInfo.formatPhoneNumber(editPhoneNumber.getText().toString());
        GlobalInfo.updatesInfo(GlobalInfo.phoneNumber);
        finish();
        Intent intent=new Intent(this,MyTrackers.class);
        startActivity(intent);

    }
}