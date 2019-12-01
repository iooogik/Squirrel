package com.example.squirrel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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

        mDBHelper = new DatabaseHelper(this);
        mDBHelper.openDataBase();
        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }

        //получаем данные из бд в виде курсора
/*
        Intent intent = new Intent(this, SignIn.class);
        if(mAuth.getCurrentUser() == null){
            startActivity(intent);
        }
*/


        setContentView(R.layout.activity_main);

        Button add = findViewById(R.id.addProject);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProject(String.valueOf(id), true);
            }
        });

        updateProjects();

    }

    protected void addProject(String name, boolean New){
        LinearLayout linear = findViewById(R.id.linear);
        View view = getLayoutInflater().inflate(R.layout.item_project, null);
        Button btn = view.findViewById(R.id.project_name);
        final Intent openNote = new Intent(this, Note.class);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(openNote);
            }
        });
        btn.setText(name);
        linear.addView(view);
        if(New) {
            //добавление в бд и запись в строчки
            ContentValues cv = new ContentValues();
            cv.put("id", id + 1);
            cv.put("name", name);
            cv.put("shortName", "короткое описание");
            cv.put("text", " ");
            //получение даты
            Date currentDate = new Date();
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            String dateText = dateFormat.format(currentDate);
            cv.put("date", dateText);
            //запись
            dataProjects.add(String.valueOf(id + 1));
            id++;
            SQLiteDatabase database = new DatabaseHelper(this).getWritableDatabase();
            database.insert("Notes", null, cv);
        }

    }

    public void delete(View view){
        if(id >= 1) {
            mDb = mDBHelper.getWritableDatabase();
            mDb.delete("Notes", "id = " + id, null);
            //Toast.makeText(this, dataProjects.size() + " " + id, Toast.LENGTH_SHORT).show();
            dataProjects.remove(id - 1);
            LinearLayout linear = findViewById(R.id.linear);
            linear.removeViewAt(id);
            id--;


        }

    }

    public  void updateProjects(){

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
        id = dataProjects.size();
        //Toast.makeText(this, String.valueOf(id), Toast.LENGTH_SHORT).show();
    }

}

