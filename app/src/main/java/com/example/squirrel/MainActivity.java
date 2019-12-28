package com.example.squirrel;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //Переменная для работы с БД
    public static int id = 0;
    //Переменная для работы с БД
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Cursor userCursor;
    ArrayList<String> dataProjects = new ArrayList<String>();


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


/*
        Intent intent = new Intent(this, SignIn.class);
        if(mAuth.getCurrentUser() == null){
            startActivity(intent);
        }
*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Intent qrReader = new Intent(this, BarcodeCaptureActivity.class);
        new Drawer()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withHeader(R.layout.drawer_header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_home).
                                withIcon(FontAwesome.Icon.faw_home).withIdentifier(1),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_settings).withIdentifier(2),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_qr).withIdentifier(3)
                        /*запятая после прдыдущего!
                        new SecondaryDrawerItem().withName(R.string.drawer_item_help).
                                withIcon(FontAwesome.Icon.faw_cog),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_open_source).
                                withIcon(FontAwesome.Icon.faw_question).setEnabled(false),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_contact).
                                withIcon(FontAwesome.Icon.faw_github).withBadge("12+").withIdentifier(1)

                         */
                )
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // Скрываем клавиатуру при открытии Navigation Drawer
                        InputMethodManager inputMethodManager = (InputMethodManager) MainActivity.
                                this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(MainActivity.
                                this.getCurrentFocus().getWindowToken(), 0);
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {}
                })
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position,
                                            long id, IDrawerItem drawerItem) {

                        if(position == 1){

                        } else if(position == 2){

                        } else if (position == 3) {
                            qrReader.putExtra(BarcodeCaptureActivity.AutoFocus, true);
                            qrReader.putExtra(BarcodeCaptureActivity.UseFlash, false);

                            startActivity(qrReader);
                        }

                    }
                })
                .build();

        mDBHelper = new DatabaseHelper(this);
        mDBHelper.openDataBase();
        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }


        FloatingActionButton add = findViewById(R.id.addProject);

        final LinearLayout mainLayout  = new LinearLayout(this);
        final LinearLayout layout1 = new LinearLayout(this);

        add.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                mainLayout.setOrientation(LinearLayout.VERTICAL);
                layout1.setOrientation(LinearLayout.HORIZONTAL);
                final EditText name = new EditText(getApplicationContext());
                name.setText("Введите имя");


                Typeface tpf = Typeface.createFromAsset(getAssets(), "rostelekom.otf");
                name.setTypeface(tpf);
                name.setTextSize(18);
                name.setMinHeight(15);
                layout1.addView(name);

                mainLayout.addView(layout1);
                builder.setView(mainLayout);

                builder.setCancelable(true);
                builder.setPositiveButton(Html.fromHtml
                                ("<font color='#7AB5FD'>Добавить запись</font>"),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mDb = mDBHelper.getWritableDatabase();
                                addProject(name.getText().toString(), true);
                                //добавление в бд и запись в строчки
                                ContentValues cv = new ContentValues();
                                id++;
                                cv.put("id", id);
                                cv.put("name", name.getText().toString());
                                cv.put("shortName", "короткое описание");
                                cv.put("text", "hello, it's the best note ever");
                                //получение даты
                                Date currentDate = new Date();
                                DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy",
                                        Locale.getDefault());
                                String dateText = dateFormat.format(currentDate);
                                cv.put("date", dateText);
                                //запись
                                dataProjects.add(String.valueOf(id));
                                mDb.insert("Notes", null, cv);
                                mDb.close();
                            }
                        });
                AlertDialog dlg = builder.create();

                dlg.show();

                return true;
            }
        });

        updProjects();
        Toast.makeText(this, String.valueOf(id), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.addProject){
            //кнопка "Добавить проект"
            mDb = mDBHelper.getWritableDatabase();
            String nameNote = "Быстрая заметка " + id;
            id++;
            addProject(nameNote, true);
            //добавление в бд и запись в строчки
            ContentValues cv = new ContentValues();
            cv.put("id", id);
            cv.put("name", nameNote);
            cv.put("shortName", "короткое описание");
            cv.put("text", "hello, it's the best note ever");
            //получение даты
            Date currentDate = new Date();
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy",
                    Locale.getDefault());
            String dateText = dateFormat.format(currentDate);
            cv.put("date", dateText);
            //запись
            dataProjects.add(nameNote);
            mDb.insert("Notes", null, cv);
            mDb.close();
        }
    }

    //добавление проекта на активити и запись его в бд
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

        final LinearLayout mainLayout  = new LinearLayout(this);
        final LinearLayout layout1 = new LinearLayout(this);

        btn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                mDb = mDBHelper.getReadableDatabase();
                userCursor =  mDb.rawQuery("Select * from Notes", null);
                final int btnID = dataProjects.indexOf(btn.getText().toString());
                userCursor.moveToPosition(btnID);

                String message = userCursor.getString(2);

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());


                mainLayout.setOrientation(LinearLayout.VERTICAL);
                layout1.setOrientation(LinearLayout.HORIZONTAL);
                TextView tv = new TextView(getApplicationContext());
                tv.setText("\n  " + message);
                tv.setTextColor(Color.BLACK);
                Typeface tpf = Typeface.createFromAsset(getAssets(), "rostelekom.otf");
                tv.setTypeface(tpf);
                tv.setTextSize(18);
                tv.setMinHeight(15);
                layout1.addView(tv);

                mainLayout.addView(layout1);
                builder.setView(mainLayout);

                builder.setCancelable(true);
                builder.setPositiveButton(Html.fromHtml
                                ("<font color='#7AB5FD'>Удалить выбранную запись</font>"),
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        delete(btnID);

                    }
                });
                AlertDialog dlg = builder.create();

                dlg.show();
                return true;
            }
        });

        //динамическое добавление кнопок на активити
        linear.addView(view);
        ScrollView scroll = findViewById(R.id.scroll);
        scroll.fullScroll(ScrollView.FOCUS_DOWN);

        //добавление кнопки в бд
    }

    //удаление проекта из активити и удаление его из бд
    public void delete(int selected){
        if(id >= 0) {
            mDb = mDBHelper.getWritableDatabase();
            mDb.delete("Notes", "id = " + selected, null);
            dataProjects.remove(selected);
            LinearLayout linear = findViewById(R.id.linear);
            linear.removeViewAt(selected);
            if(id - 1 >=0){
                id--;
            } else {
                id = 0;
            }
        }

    }

    //обновление проектов на активити
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
        else {id = dataProjects.size() + 1;}
        Toast.makeText(this, String.valueOf(id), Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDb.close();
        userCursor.close();
    }

    @Override
    public void onBackPressed(){}
}

