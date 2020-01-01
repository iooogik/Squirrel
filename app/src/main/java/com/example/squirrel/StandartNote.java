package com.example.squirrel;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StandartNote extends android.app.Activity {

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;

    Cursor userCursor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.standart_note);

        /* БД ************************ */
        mDBHelper = new DatabaseHelper(this);
        mDBHelper.openDataBase();
        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }

        ImageButton saveBtn = findViewById(R.id.buttonSave);

        final Intent mainActivity = new Intent(this, MainActivity.class);


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dataName = "Notes";
                TextView name = findViewById(R.id.editName);
                TextView note = findViewById(R.id.editNote);
                TextView shortNote = findViewById(R.id.shortNote);

                updDatabase(dataName, name.getText().toString(),
                        note.getText().toString(), shortNote.getText().toString());

                startActivity(mainActivity);
            }
        });
        updateDataActivity();

        //поделиться

        final Intent shareActivity = new Intent(this, Share.class);

        ImageButton shareBtn = findViewById(R.id.buttonShare);

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(shareActivity);
            }
        });

    }

    @SuppressLint("SetTextI18n")
    protected void updateDataActivity(){
        TextView name = findViewById(R.id.editName);
        TextView note = findViewById(R.id.editNote);
        TextView shortNote = findViewById(R.id.shortNote);

        mDb = mDBHelper.getReadableDatabase();

        userCursor =  mDb.rawQuery("Select * from Notes", null);

        userCursor.moveToPosition(getBtnID());

        name.setText(getBtnName());
        shortNote.setText(userCursor.getString(2));
        note.setText(userCursor.getString(3));

        Toast.makeText(getApplicationContext(), String.valueOf(userCursor.isNull(5)), Toast.LENGTH_LONG).show();

        ImageView img = findViewById(R.id.qr_view);
        LinearLayout linearLayout = findViewById(R.id.layout_img);

        if(!userCursor.isNull(5)){
            linearLayout.setVisibility(View.VISIBLE);
            img.setImageBitmap(setImage());
            shortNote.setText("Расшифровка: " + shortNote.getText().toString());
            shortNote.setEnabled(false);
        }
    }

    protected Bitmap setImage(){
        mDb = mDBHelper.getWritableDatabase();
        userCursor = mDb.rawQuery("Select * from Notes", null);

        userCursor.moveToPosition(getBtnID());
        byte[] bytesImg = userCursor.getBlob(5);
        return BitmapFactory.decodeByteArray(bytesImg, 0, bytesImg.length);
    }

    private int getBtnID(){
        Bundle arguments = getIntent().getExtras();
        assert arguments != null;
        return arguments.getInt("buttonID");
    }

    private String getBtnName(){
        Bundle arguments = getIntent().getExtras();
        assert arguments != null;
        return arguments.getString("button name");
    }

    public void updDatabase(String databaseName, String name, String note, String shortNote){
        mDb = mDBHelper.getWritableDatabase();

        //код сохранения в бд
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("shortName", shortNote);
        cv.put("text", note);

        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy",
                Locale.getDefault());

        cv.put("date", dateFormat.format(currentDate));

        //обновление базы данных
        mDb.update(databaseName, cv, "id =" + (getBtnID() + 1), null);
    }

    @Override
    public void onBackPressed() {
        Intent main = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(main);
        finish();
    }
}
