package com.example.squirrel;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Note extends android.app.Activity {

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    Cursor userCursor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note);

        /* БД ************************ */
        mDBHelper = new DatabaseHelper(this);
        mDBHelper.openDataBase();
        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }
        /* БД ************************ */

        updateData();

        ImageButton saveBtn = findViewById(R.id.buttonSave);

        Bundle arguments = getIntent().getExtras();
        int btnID = arguments.getInt("buttonID");

        final Intent mainActivity = new Intent(this, MainActivity.class);

        //Toast.makeText(this, String.valueOf(btnID + 1), Toast.LENGTH_SHORT).show();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDb = mDBHelper.getWritableDatabase();
                //код сохранения в бд
                TextView name = findViewById(R.id.editName);
                TextView note = findViewById(R.id.editNote);
                TextView shortNote = findViewById(R.id.shortNote);

                ContentValues cv = new ContentValues();

                cv.put("name", name.getText().toString());
                cv.put("shortName", shortNote.getText().toString());
                cv.put("text", note.getText().toString());

                Date currentDate = new Date();
                DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy",
                        Locale.getDefault());

                cv.put("date", dateFormat.format(currentDate));


                //получение данных из main activity
                Bundle arguments = getIntent().getExtras();
                int btnID = arguments.getInt("buttonID");
                //обновление базы данных

                mDb.update("Notes", cv, "id =" + (btnID + 1), null);

                startActivity(mainActivity);
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

    protected void updateData(){
        TextView name = findViewById(R.id.editName);
        TextView note = findViewById(R.id.editNote);
        TextView shortNote = findViewById(R.id.shortNote);
        mDb = mDBHelper.getReadableDatabase();
        userCursor =  mDb.rawQuery("Select * from Notes", null);
        Bundle arguments = getIntent().getExtras();
        int btnID = arguments.getInt("buttonID");
        Toast.makeText(this, String.valueOf(btnID), Toast.LENGTH_SHORT).show();
        userCursor.moveToPosition(btnID);

        name.setText(arguments.getString("button name"));

        shortNote.setText(userCursor.getString(2));
        note.setText(userCursor.getString(3));
    }

}
