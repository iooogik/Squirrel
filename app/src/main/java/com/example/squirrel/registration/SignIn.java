package com.example.squirrel.registration;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;

import com.example.squirrel.MainActivity;
import com.example.squirrel.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class SignIn extends android.app.Activity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);
        final Intent signin = new Intent(this, SignIn.class);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                startActivity(signin);
            }
        };

        findViewById(R.id.btn_signIn).setOnClickListener(this);
    }

    public void signIn(String em, String pass){
        final Intent intentMain = new Intent(this, MainActivity.class);
        mAuth.signInWithEmailAndPassword(em, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity(intentMain);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);
        signIn(email.getText().toString(), password.getText().toString());
    }

    public void onRegClick(View view){
        Intent intent = new Intent(this, Registration.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {

    }
}
