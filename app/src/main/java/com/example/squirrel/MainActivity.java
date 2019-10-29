package com.example.squirrel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    boolean bool = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        onResult();
    }

    protected void onResult(){

        if(bool) {
            Intent intent = new Intent(this, SignIn.class);
            startActivity(intent);
        }
    }
}
