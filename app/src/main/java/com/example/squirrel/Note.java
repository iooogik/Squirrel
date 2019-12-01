package com.example.squirrel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;

public class Note extends android.app.Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note);

        ImageButton saveBtn = findViewById(R.id.buttonSave);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //код сохранения в бд
            }
        });

        //поделиться
        ImageButton shareBtn = findViewById(R.id.buttonShare);
        final Intent shareActivity = new Intent(this, Share.class);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(shareActivity);
            }
        });

    }
}
