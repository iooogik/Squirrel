package com.example.squirrel;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
import android.view.Display;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //Переменная для работы с БД
    public static int id = 0;
    //Переменная для работы с БД
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    //размеры экрана
    public static int ScreenWidth = 0;
    public static int ScreenHeight = 0;
    StandartNote standartNote = new StandartNote();
    Shop shopActivity = new Shop();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Cursor userCursor;
    SimpleCursorAdapter userAdapter;
    ListView userList;
    EditText userFilter;


    ArrayList<String> dataProjects = new ArrayList<String>();



    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Display display = getWindowManager().getDefaultDisplay();
        ScreenHeight = display.getHeight();
        ScreenWidth = display.getWidth();


/*
        Intent intent = new Intent(this, SignIn.class);
        if(mAuth.getCurrentUser() == null){
            startActivity(intent);
        }
*/
        createToolbar();

        mDBHelper = new DatabaseHelper(this);
        mDBHelper.openDataBase();
        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }


        FloatingActionButton add = findViewById(R.id.addProject);

        final LinearLayout mainLayout  = new LinearLayout(this);

        add.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                mainLayout.setOrientation(LinearLayout.VERTICAL);
                int padding = 30;
                mainLayout.setPadding(padding, padding, padding, padding);

                final EditText name = new EditText(getApplicationContext());
                Typeface tpf = Typeface.createFromAsset(getAssets(), "rostelekom.otf");
                name.setHint("Введите имя");
                name.setTypeface(tpf);
                name.setTextSize(18);
                name.setMinimumWidth(1500);

                mainLayout.addView(name);


                final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setView(mainLayout);
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
                builder.setNegativeButton(Html.fromHtml
                        ("<font color='#7AB5FD'>Закрыть</font>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog dlg = builder.create();
                dlg.show();



                return true;
            }
        });

        EditText search = findViewById(R.id.search);
        userList = (ListView)findViewById(R.id.userList);
        String[] headers = new String[]{"name"};

        userAdapter = new SimpleCursorAdapter(this, android.R.layout.two_line_list_item,
                userCursor, headers, new int[]{android.R.id.text1, android.R.id.text2}, 0);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                userAdapter.getFilter().filter(s.toString());
            }
        });

        userAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                if (constraint == null || constraint.length() == 0) {

                    return mDb.rawQuery("select * from Notes", null);
                }
                else {
                    return mDb.rawQuery("select * from Notes" + " where " +
                            "name" + " like ?", new String[]{"%" + constraint.toString() + "%"});
                }
            }
        });

        userList.setAdapter(userAdapter);

        add.setOnClickListener(this);
        updProjects();

    }

    public void createToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setSubtitle(R.string.textNotes);
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
                        new SecondaryDrawerItem().withName(R.string.drawer_item_qr).withIdentifier(3),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName("Покупки").withIdentifier(4),
                        new SecondaryDrawerItem().withName("Карточки").withIdentifier(5)

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
                        try {
                            InputMethodManager inputMethodManager = (InputMethodManager) MainActivity.
                                    this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                            if (inputMethodManager != null) {
                                inputMethodManager.hideSoftInputFromWindow(Objects.requireNonNull(MainActivity.
                                        this.getCurrentFocus()).getWindowToken(), 0);
                            }
                        } catch (Exception e){
                            System.out.println(e);
                        }

                    }


                    @Override
                    public void onDrawerClosed(View drawerView) {}
                })
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position,
                                            long id, IDrawerItem drawerItem) {

                        if(position == 1){
                            closeFragment();
                        } else if(position == 2){}
                        else if (position == 3) {
                            qrReader.putExtra(BarcodeCaptureActivity.AutoFocus, true);
                            qrReader.putExtra(BarcodeCaptureActivity.UseFlash, false);
                            startActivity(qrReader);
                        } else if(position == 4){}
                        else if(position == 5){}

                    }
                })
                .build();

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.addProject){
            //кнопка "Добавить проект"
            final LinearLayout mainLayout  = new LinearLayout(this);
            final LinearLayout layout1 = new LinearLayout(this);
            //AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            mainLayout.setOrientation(LinearLayout.VERTICAL);
            layout1.setOrientation(LinearLayout.VERTICAL);
            //ввод названия заметки
            final EditText nameNote = new EditText(getApplicationContext());


            nameNote.setTextColor(Color.BLACK);
            final Typeface tpf = Typeface.createFromAsset(getAssets(), "rostelekom.otf");
            nameNote.setHint("Введите название");
            nameNote.setTypeface(tpf);
            nameNote.setTextSize(18);
            nameNote.setMinimumWidth(1500);
            layout1.addView(nameNote);

            final TextView tv = new TextView(getApplicationContext());
            tv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            tv.setText("    Пожалуйста, введите название!");
            tv.setTextColor(Color.RED);
            tv.setTextSize(13);
            tv.setTypeface(tpf);
            tv.setMinimumWidth(1500);
            tv.setVisibility(View.GONE);
            layout1.addView(tv);

            String[] types = new String[]{"standart", "shop"};
            //выбор типа
            final Spinner spinner = new Spinner(getApplicationContext());
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_dropdown_item, types);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent,
                                           View itemSelected, int selectedItemPosition, long selectedId) {

                    ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                    ((TextView) parent.getChildAt(0)).setTextSize(18);
                    ((TextView) parent.getChildAt(0)).setTypeface(tpf);

                }
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });



            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());


            layout1.addView(spinner);
            mainLayout.addView(layout1);
            builder.setView(mainLayout);

            builder.setPositiveButton(Html.fromHtml
                            ("<font color='#7AB5FD'>Добавить</font>"),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mDb = mDBHelper.getWritableDatabase();
                            addProject(nameNote.getText().toString(), true);
                            //добавление в бд и запись в строчки
                            ContentValues cv = new ContentValues();
                            cv.put("id", id);
                            cv.put("name", nameNote.getText().toString());
                            cv.put("shortName", "короткое описание");
                            cv.put("text", "hello, it's the best note ever");
                            cv.put("type", spinner.getSelectedItem().toString());
                            //получение даты
                            Date currentDate = new Date();
                            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy",
                                    Locale.getDefault());
                            String dateText = dateFormat.format(currentDate);
                            cv.put("date", dateText);
                            //запись
                            dataProjects.add(nameNote.getText().toString());
                            mDb.insert("Notes", null, cv);
                            mDb.close();
                            id++;
                        }
                    });

            AlertDialog dlg = builder.create();

            dlg.show();
        }
    }

    private String getType(String name){
        mDb = mDBHelper.getReadableDatabase();
        userCursor =  mDb.rawQuery("Select * from Notes", null);
        final int btnID = dataProjects.indexOf(name);
        userCursor.moveToPosition(btnID);

        return userCursor.getString(8);
    }

    //добавление проекта на активити и запись его в бд
    protected void addProject(String name, boolean New){
        LinearLayout linear = findViewById(R.id.standardNote);
        final LinearLayout shop = findViewById(R.id.shopNote);
        View view = getLayoutInflater().inflate(R.layout.item_project, null);
        final Button btn = view.findViewById(R.id.project_name);

        btn.setText(name);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FrameLayout frameLayout = findViewById(R.id.frame);
                frameLayout.setVisibility(View.VISIBLE);
                Bundle args = new Bundle();
                args.putString("button name", btn.getText().toString());
                args.putInt("buttonID", dataProjects.indexOf(btn.getText().toString()));
                if(getType(btn.getText().toString()).equals("standart")) {

                    standartNote.setArguments(args);
                    showFragment(standartNote);
                    Toolbar toolbar = findViewById(R.id.toolbar);
                    toolbar.setSubtitle("Заметка: " + btn.getText().toString());

                } else if(getType(btn.getText().toString()).equals("shop")){

                    shopActivity.setArguments(args);
                    showFragment(shopActivity);
                    Toolbar toolbar = findViewById(R.id.toolbar);
                    toolbar.setSubtitle("Заметка: " + btn.getText().toString());

                }
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
                layout1.setOrientation(LinearLayout.VERTICAL);
                TextView tv = new TextView(getApplicationContext());
                tv.setMinHeight(25);
                tv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
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
                        delete(btnID + 1);
                    }
                });
                AlertDialog dlg = builder.create();

                dlg.show();
                return true;

            }
        });
        if(getType(btn.getText().toString()).equals("standart")) {
            linear.addView(view);
        } else if(getType(btn.getText().toString()).equals("shop")){
            shop.addView(view);
        }


    }

    private void showFragment(Fragment fragment){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.frame, fragment);
        ft.commit();
    }

    //удаление проекта из активити и удаление его из бд
    public void delete(int selected){
        if(id >= 0) {
            mDb = mDBHelper.getWritableDatabase();
            mDb.delete("Notes", "id=" + selected, null);

            dataProjects.remove(selected - 1);
            LinearLayout linear;

            if(getType(dataProjects.get(id)).equals("standart")) {
                linear = findViewById(R.id.standardNote);
            } else {
                linear = findViewById(R.id.shopNote);
            }

            linear.removeViewAt(selected - 1);
            ContentValues cv = new ContentValues();
            for(int i = 0; i < dataProjects.size(); i++){
                cv.put("id", String.valueOf(i + 1));
                mDb.update("Notes", cv, "id =" + (i + 1), null);
            }

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
    }

    @Override
    public void onBackPressed() {
        closeFragment();
    }

    private void closeFragment(){
        final FrameLayout frameLayout = findViewById(R.id.frame);
        if(frameLayout.getVisibility() == View.VISIBLE){
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            ft.remove(standartNote).commit();
            frameLayout.removeAllViews();
            frameLayout.setVisibility(View.GONE);
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setSubtitle(R.string.textNotes);
        }
    }
}

