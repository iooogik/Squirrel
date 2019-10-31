package com.example.squirrel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthListener;
    //Создаем список вьюх которые будут создаваться
    private List<View> allEds;
    //счетчик для визуального отображения проектов
    int counter = 0;


    protected void onCreate(Bundle savedInstanceState) {
        final Intent intent = new Intent(this, SignIn.class);
        /*
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                startActivity(intent);
                System.out.println("rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr");
            }
        };
        */
        //сделать отслеживание выхода и входа пользователя
        if(mAuth.getCurrentUser() == null){
            startActivity(intent);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void addProject(View v){
        Button addButton = findViewById(R.id.add_button);
        //инициализируем наш массив
        allEds = new ArrayList<View>();
        //находим linear
        final LinearLayout linear = findViewById(R.id.projects);
        counter++;
        final View view = getLayoutInflater().inflate(R.layout.item_project, null);
        Button btn_prj = findViewById(R.id.project_name);
        //добавляем все что создаем в массив
        allEds.add(view);
        //добавляем елементы в linearlayout
        linear.addView(view);
    }
}
