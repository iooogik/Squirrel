package com.example.squirrel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

public class QR_Demo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr__demo);
        getQr();
        makeQr();
    }

    protected void getQr(){
        TextView tv = findViewById(R.id.textView5);
        Bundle arguments = getIntent().getExtras();
        assert arguments != null;
        String qr_text = arguments.getString("qr_text");
        Toast.makeText(getApplicationContext(), qr_text, Toast.LENGTH_LONG).show();
        tv.setText(qr_text);
    }

    protected void makeQr(){

    }

}
