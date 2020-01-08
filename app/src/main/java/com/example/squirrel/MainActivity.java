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
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
    Fragment currFragment;
    Shop shopActivity = new Shop();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Cursor userCursor;
    SimpleCursorAdapter userAdapter;
    ListView userList;
    EditText userFilter;
    public static ArrayList<String> standartItems = new ArrayList<>();
    public static ArrayList<String> shopItems = new ArrayList<>();

    ArrayAdapter<String> adapterStndrtList;
    ArrayAdapter<String> adapterShopList;



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
                                //addProject(name.getText().toString(), true);
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
        userList = (ListView)findViewById(R.id.standartList);
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
        int identifier = 1;
        new Drawer()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withHeader(R.layout.drawer_header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_home).
                                withIcon(FontAwesome.Icon.faw_home).withIdentifier(identifier),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_settings).withIdentifier(identifier++),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_qr).withIdentifier(identifier++),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName("Стандартные заметки").withIdentifier(identifier++),
                        new SecondaryDrawerItem().withName("Покупки").withIdentifier(identifier++),
                        new SecondaryDrawerItem().withName("Карточки").withIdentifier(identifier++)

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
                            if(currFragment != null) {
                                closeFragment(currFragment);
                            }
                        } else if(position == 2){}
                        else if (position == 3) {
                            qrReader.putExtra(BarcodeCaptureActivity.AutoFocus, true);
                            qrReader.putExtra(BarcodeCaptureActivity.UseFlash, false);
                            startActivity(qrReader);
                        }
                        else if(position == 4){

                        }
                        else if(position == 5){

                        }
                        else if(position == 6){

                        }

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

            final String[] types = new String[]{"standart", "shop"};
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
                            String type = spinner.getSelectedItem().toString();
                            //addProject(nameNote.getText().toString(), true);
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

                            if(type.equals("standart")){
                                standartItems.add(nameNote.getText().toString());
                                adapterStndrtList.notifyDataSetChanged();
                            }else if(type.equals("shop")){
                                shopItems.add(nameNote.getText().toString());
                                adapterShopList.notifyDataSetChanged();
                            }
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

    private void showFragment(Fragment fragment){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        currFragment = fragment;
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.frame, fragment);
        ft.commit();
    }

    //удаление проекта из активити и удаление его из бд
    public void delete(int selected){
        if(id >= 0) {
            mDb = mDBHelper.getWritableDatabase();

            mDb.delete("Notes", "id=" + (selected), null);

            if(getType(standartItems.get(selected - 1)).equals("standart") ||
            getType(shopItems.get(selected - 1)).equals("shop")) {

                standartItems.remove(selected - 1);
                adapterStndrtList.notifyDataSetChanged();
            } else {
                shopItems.remove(selected - 2);
                adapterShopList.notifyDataSetChanged();
            }



            ContentValues cv = new ContentValues();
            for(int i = 0; i < dataProjects.size(); i++){
                cv.put("id", String.valueOf(i + 1));
                System.out.println(i + 1);
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

        final ListView standartList = findViewById(R.id.standartList);
        final ListView shopList = findViewById(R.id.shopList);

        while (!userCursor.isAfterLast()) {
            item = userCursor.getString(1); //колонки считаются с 0
            dataProjects.add(String.valueOf(item));

            if(getType(String.valueOf(item)).equals("standart")) {
                standartItems.add(String.valueOf(item));
            } else if(getType(String.valueOf(item)).equals("shop")){
                shopItems.add(String.valueOf(item));
            }

            userCursor.moveToNext();
        }
        userCursor.close();

        adapterShopList = new ArrayAdapter<>(this,
                R.layout.item_project, shopItems);

        adapterStndrtList = new ArrayAdapter<>(this,
                R.layout.item_project, standartItems);

        standartList.setAdapter(adapterStndrtList);
        shopList.setAdapter(adapterShopList);

        shopList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onItemListClicked(position + standartItems.size());
            }
        });

        standartList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onItemListClicked(position);
            }
        });

        standartList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position,
                                           long id) {
                onItemLongListClicked(position, "standart");
                return false;
            }
        });

        shopList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position,
                                           long id) {
                onItemLongListClicked(position, "shop");
                return false;
            }
        });

        if(dataProjects.size() == 0){id = 0;}
        else {id = dataProjects.size() + 1;}
    }

    private void onItemListClicked(int position){
        FrameLayout frameLayout = findViewById(R.id.frame);
        frameLayout.setVisibility(View.VISIBLE);
        Bundle args = new Bundle();
        String name = dataProjects.get(position);
        args.putString("button name", name);
        args.putInt("buttonID", dataProjects.indexOf(name));
        Toolbar toolbar = findViewById(R.id.toolbar);
        if(getType(name).equals("standart")) {

            standartNote.setArguments(args);
            showFragment(standartNote);
            toolbar.setSubtitle("Заметка: " + name);

        } else if(getType(name).equals("shop")){

            shopActivity.setArguments(args);
            showFragment(shopActivity);
            toolbar.setSubtitle("Заметка: " + name);

        }
    }

    private void onItemLongListClicked(int position, String type){

        final ListView standartList = findViewById(R.id.standartList);
        final ListView shopList = findViewById(R.id.shopList);

        String name = String.valueOf(standartList.getItemAtPosition(position));
        mDb = mDBHelper.getReadableDatabase();
        userCursor =  mDb.rawQuery("Select * from Notes", null);
        userCursor.moveToPosition(position);

        String message = userCursor.getString(2);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        final LinearLayout mainLayout  = new LinearLayout(MainActivity.this);
        final LinearLayout layout1 = new LinearLayout(MainActivity.this);

        mainLayout.setOrientation(LinearLayout.VERTICAL);
        layout1.setOrientation(LinearLayout.VERTICAL);
        TextView tv = new TextView(MainActivity.this);
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

        if(type.equals("shop")){
            position = position + standartItems.size() + 1;
        } else if(type.equals("standart")){
            position = position + 1;
        }

        builder.setCancelable(true);
        final int finalPosition = position;
        Toast.makeText(getApplicationContext(), String.valueOf(finalPosition), Toast.LENGTH_LONG).show();
        builder.setPositiveButton(Html.fromHtml
                        ("<font color='#7AB5FD'>Удалить выбранную запись</font>"),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Toast.makeText(getApplicationContext(), String.valueOf(position),
                        // Toast.LENGTH_LONG).show();
                        delete(finalPosition);
                    }
                });
        AlertDialog dlg = builder.create();
        dlg.show();

    }

    @Override
    public void onBackPressed() {
        if(currFragment != null) {
            closeFragment(currFragment);
        }
    }

    private void closeFragment(Fragment fragment){
        final FrameLayout frameLayout = findViewById(R.id.frame);
        if(frameLayout.getVisibility() == View.VISIBLE){
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            ft.remove(fragment).commit();
            //frameLayout.removeAllViews();
            frameLayout.setVisibility(View.GONE);
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setSubtitle(R.string.textNotes);
        }
    }
}

