package com.example.squirrel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthListener;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Intent intent = new Intent(this, SignIn.class);
        if(mAuth.getCurrentUser() == null){
            startActivity(intent);
        }

        //кнопка, добавляющая дело
        Button add = findViewById(R.id.addProject);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LinearLayout linear = findViewById(R.id.linear);
                final View view = getLayoutInflater().inflate(R.layout.item_project, null);
                TextView tv = findViewById(R.id.project);
                tv.setText("Новая запись");
                linear.addView(view);
            }
        });
    }

}
