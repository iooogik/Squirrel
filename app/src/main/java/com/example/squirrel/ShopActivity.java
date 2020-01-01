package com.example.squirrel;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ShopActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_activity);

        StandartNote standartNote = new StandartNote();
        standartNote.updateDataActivity();
    }

    protected void getCheckers(){}

}
