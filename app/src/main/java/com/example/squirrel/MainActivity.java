package com.example.squirrel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ScrollView;
import android.widget.Toast;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    //Переменная для работы с БД
    public static int id = 0;
    Cursor userCursor;
    //Переменная для работы с БД
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    ArrayList<String> dataProjects = new ArrayList<String>();


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
/*
        Intent intent = new Intent(this, SignIn.class);
        if(mAuth.getCurrentUser() == null){
            startActivity(intent);
        }
*/
        mDBHelper = new DatabaseHelper(this);
        mDBHelper.openDataBase();
        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }

        //получаем данные из бд в виде курсора

        setContentView(R.layout.activity_main);

        FloatingActionButton add = findViewById(R.id.addProject);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDb = mDBHelper.getWritableDatabase();
                addProject(String.valueOf(id), true);
                //добавление в бд и запись в строчки
                ContentValues cv = new ContentValues();
                id++;
                cv.put("id", id);
                cv.put("name", String.valueOf(id));
                cv.put("shortName", "короткое описание");
                cv.put("text", "hello, it's the best note ever");
                //получение даты
                Date currentDate = new Date();
                DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                String dateText = dateFormat.format(currentDate);
                cv.put("date", dateText);
                //запись
                dataProjects.add(String.valueOf(id));
                mDb.insert("Notes", null, cv);
                mDb.close();
            }
        });

        updProjects();

    }

    protected void addProject(String name, boolean New){
        LinearLayout linear = findViewById(R.id.linear);
        View view = getLayoutInflater().inflate(R.layout.item_project, null);
        final Button btn = view.findViewById(R.id.project_name);
        final Intent openNote = new Intent(this, Note.class);
        btn.setText(name);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //получение id и названия нажатой кнопки и отправка этих данных в другое активити
                openNote.putExtra("button name", btn.getText().toString());
                openNote.putExtra("buttonID", dataProjects.indexOf(btn.getText().toString()));
                startActivity(openNote);
            }
        });
        //динамическое добавление кнопок на активити
        linear.addView(view);
        HorizontalScrollView scroll = findViewById(R.id.scrol);
        scroll.fullScroll(ScrollView.FOCUS_RIGHT);

    }

    public void delete(View view){
        if(id > 0) {
            mDb = mDBHelper.getWritableDatabase();
            mDb.delete("Notes", "id = " + id, null);
            //Toast.makeText(this, dataProjects.size() + " " + id, Toast.LENGTH_SHORT).show();
            dataProjects.remove(id - 1);
            LinearLayout linear = findViewById(R.id.linear);
            linear.removeViewAt(id - 1);
            id--;


        }

    }

    public  void updProjects(){
        //добавление новых проектов
        mDb = mDBHelper.getReadableDatabase();
        userCursor =  mDb.rawQuery("Select * from Notes", null);
        userCursor.moveToFirst();
        String item = "";
        while (!userCursor.isAfterLast()) {
            item = userCursor.getString(1); //колонки считаются с 0
            //Log.d("my best tag","**********************************" + item);
            dataProjects.add(String.valueOf(item));
            userCursor.moveToNext();
        }
        userCursor.close();
        for(int i = 0; i < dataProjects.size(); i ++){
            //Log.d("my best tag","**********************************" + dataProjects.get(i));
            if(dataProjects.get(i) != null) {
                addProject(dataProjects.get(i), false);
            }
        }
        if(dataProjects.size() == 0){id = 0;}
        else {id = dataProjects.size() - 1;}
        Toast.makeText(this, String.valueOf(id), Toast.LENGTH_SHORT).show();
    }

}

